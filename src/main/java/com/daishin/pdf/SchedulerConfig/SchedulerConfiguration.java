package com.daishin.pdf.SchedulerConfig;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.Status;
import com.daishin.pdf.service.*;
import com.daishin.pdf.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfiguration {

    private final Utils utils;
    private final MasterSaveService masterSaveService;
    private final MasterInfoService masterInfoService;
    private final DetailInfoService detailInfoService;

    private final StatusInfoService statusInfoService;

    private final MasterDeleteService masterDeleteService;
    private final DetailDeleteService detailDeleteService;

    private final Logger logger = LoggerFactory.getLogger("daishin");

    //실시간(단일) json 생성 및 상태 변화 1 -> 2
    @Scheduled(cron = "00 03 14 * * *")
    public void run() {

            Master master = new Master();
            master.setMASTER_KEY(LocalDate.now()+"");
            master.setSTATUS(200);

            int total = detailInfoService.countMaster(master.getMASTER_KEY());
            if(total <= 0){
            return;
            }

            master.setTOTAL_SEND_CNT(total+"");

            //master 작업 상태 업데이트
            if(masterSaveService.updateStatusAndTotalCnt(master) <= 0){
                return;
            }

            //json 파일 생성 및 폴더 이동 receiving -> complete
            utils.saveJson(LocalDate.now()+"" , logger);

            //폴더 이동
            utils.moveDir(master.getMASTER_KEY() , logger);

    }


    // 3분마다 체크
    // 설정 시간 만큼 시간이 경과 했으면 다음 상태로 (1(수신중)일때는 해당 안됨)
    // 수신 완료된 폴더 중 이동 시킨 것이 있다면 200->300
    @Scheduled(fixedRate = 180000)
    public void changeStatus(){ //okokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokokok

        //현재 상태가 300 이상이고 마지막 코드 미만인 master
        //최종 단계가 7라고 가정
        List<Status> statusList = statusInfoService.selectAll();
        List<Master> masterList = masterInfoService.selectStatusBetween300AndLast(statusList.get(statusList.size()-1).getSTATUS_CODE());
        if(!masterList.isEmpty() && !statusList.isEmpty()){

            for(Master master : masterList){
                Status status = statusInfoService.selectByStatusCode(master.getSTATUS());

                if(status.getSTATUS_NAME() != null && !status.getSTATUS_NAME().isEmpty()){
                    if(status.getCHANGE_TYPE().equals("AUTO")){
                        String waitTime = status.getWAIT_TIME().trim();

                        //시간, 분 추출
                        String regex = "(\\d+)H(\\d+)M";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(waitTime);

                        String h = "";
                        String m = "";

                        if (matcher.find()) {
                            h = matcher.group(1); // 'h' 앞의 숫자
                            m = matcher.group(2); // 'h'와 'm' 사이의 숫자
                        }

                        long hour = Long.parseLong(h);
                        long min = Long.parseLong(m);

                        int nextCode = -1;
                        for(int i=0; i<statusList.size(); i++){
                            if(statusList.get(i).getSTATUS_CODE() == status.getSTATUS_CODE()){
                                nextCode = statusList.get(i+1).getSTATUS_CODE();
                                break;
                            }
                        }

                        //설정한 시간만큼 시간이 지났다면 다음 상태로 변경
                        if (master.getSTATUS_TIME().plusHours(hour).plusMinutes(min).isBefore(LocalDateTime.now())) {
                            master.setSTATUS(nextCode);
                            masterSaveService.updateStatus(master);
                        }
                    }
                }
            }
        }
        //이동시킨 master 상태 변화 200 -> 300
        utils.checkDirectoryExists();

    }

    //마지막 상태에서 1달 지난 master 삭제
    @Scheduled(cron = "00 00 19 * * *")
    public void delete() {

        List<Status> statusList = statusInfoService.selectAll();
        //마지막 상태인 master만 select
        List<Master> masterList = masterInfoService.selectByStatus(statusList.get(statusList.size()-1).getSTATUS_CODE());
        
        for(Master master : masterList){
            if(master.getSTATUS_TIME().isBefore(LocalDateTime.now().minusMonths(1L))){
                List<Detail> detailList = detailInfoService.getMasterGroup(master.getMASTER_KEY());
                for(Detail detail : detailList){
                    detailDeleteService.deleteById(detail);
                }
                masterDeleteService.deleteById(master);
            }
        }
    }


}
