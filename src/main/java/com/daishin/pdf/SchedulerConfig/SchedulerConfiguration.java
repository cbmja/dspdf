package com.daishin.pdf.SchedulerConfig;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.log.LogCode;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import com.daishin.pdf.service.DetailInfoService;
import com.daishin.pdf.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfiguration {

    private final Utils utils;
    private final MasterSaveService masterSaveService;
    private final MasterInfoService masterInfoService;
    private final DetailInfoService detailInfoService;
    private final Logger logger = LoggerFactory.getLogger("daishin");

    //실시간(단일) json 생성 및 상태 변화 1 -> 2
    @Scheduled(cron = "00 03 14 * * *")
    public void run() throws IOException {
        if(checkTime()){

            Master master = new Master();
            master.setMASTER_KEY(LocalDate.now()+"");
            master.setSTATUS(2);

            int total = detailInfoService.countMaster(master.getMASTER_KEY());
            if(total < 0){
            return;
            }
            master.setTOTAL_SEND_CNT(total+"");

            //master 작업 상태 업데이트
            int masterUpdateResult = masterSaveService.updateStatusAndTotalCnt(master);
            if(masterUpdateResult <= 0){
                return;
            }

            //json 파일 생성
            utils.saveJson(LocalDate.now()+"" , logger);

        }
    }


    // 5분마다 체크
    // 상태 변화 된 지 2시간이 지났으면 다음 상태로 (1(수신중)일때는 해당 안됨)
    @Scheduled(fixedRate = 300000)
    public void changeStatus(){

        //현재상태가 3,4,5,6 인 master만 select
        //최종 단계가 7라고 가정
        List<Master> masterList = masterInfoService.selectStatusBetween2_7();
        if(masterList == null || masterList.isEmpty()){
            return;
        } else if (masterList.get(0).getError().equals(LogCode.SQL_ERROR)) {
            return;
        }

        for(Master master : masterList){
                //현재 상태에서 2시간이 지난 master만 상태값 +1
                if (master.getSTATUS_TIME().plusHours(2L).isBefore(LocalDateTime.now())) {
                    master.setSTATUS(master.getSTATUS() + 1);
                    masterSaveService.updateStatus(master);
                }
        }
    }


    //현재 시각이 14:00 이후인지 체크하는 메서드
    private boolean checkTime(){
        return LocalTime.now().isAfter(LocalTime.of(14 , 0));
    }


}
