package com.daishin.pdf.SchedulerConfig;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.Status;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import com.daishin.pdf.service.DetailInfoService;
import com.daishin.pdf.service.StatusInfoService;
import com.daishin.pdf.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private final Logger logger = LoggerFactory.getLogger("daishin");

    //실시간(단일) json 생성 및 상태 변화 1 -> 2
    @Scheduled(cron = "00 03 14 * * *")
    public void run() { //////////////////////////////////////OK
        if(checkTime()){

            List<Status> statusList = statusInfoService.selectAll();

            Master master = new Master();
            master.setMASTER_KEY(LocalDate.now()+"");
            master.setSTATUS(statusList.get(1).getSTATUS_CODE());

            int total = detailInfoService.countMaster(master.getMASTER_KEY());
            if(total <= 0){
            return;
            }

            master.setTOTAL_SEND_CNT(total+"");

            //master 작업 상태 업데이트
            if(masterSaveService.updateStatusAndTotalCnt(master) <= 0){
                return;
            }

            //json 파일 생성
            utils.saveJson(LocalDate.now()+"" , logger);
        }
    }


    // 3분마다 체크
    // 상태 변화 된 지 2시간이 지났으면 다음 상태로 (1(수신중)일때는 해당 안됨)
    @Scheduled(fixedRate = 180000)
    public void changeStatus(){ //////////////////////////////////////OK

        //현재상태가 3,4,5,6 인 master만 select
        //최종 단계가 7라고 가정
        List<Master> masterList = masterInfoService.selectAll();
        List<Status> statusList = statusInfoService.selectAll();

        if(!masterList.isEmpty() && !statusList.isEmpty()){

            for(Master master : masterList){
                Status status = statusInfoService.selectByStatusCode(master.getSTATUS());

                if(status.getSTATUS_NAME() != null && !status.getSTATUS_NAME().isEmpty()){
                    if(status.getCHANGE_TYPE().equals("AUTO") && status.getIS_LAST().equals("FALSE")){
                        String waitTime = status.getWAIT_TIME().trim();

                        // 정규 표현식을 사용하여 시간과 분을 추출
                        String regex = "(\\d+)H(\\d+)M";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(waitTime);

                        String h = "";
                        String m = "";

                        if (matcher.find()) {
                            h = matcher.group(1); // 'h' 앞의 숫자
                            m = matcher.group(2); // 'h'와 'm' 사이의 숫자
                        }

                        Long hour = Long.parseLong(h);
                        Long min = Long.parseLong(m);

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

    }


    //현재 시각이 14:00 이후인지 체크하는 메서드
    private boolean checkTime(){
        return LocalTime.now().isAfter(LocalTime.of(14 , 0));
    }


}
