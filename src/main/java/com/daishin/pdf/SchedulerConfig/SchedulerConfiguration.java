package com.daishin.pdf.SchedulerConfig;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.service.MasterSaveService;
import com.daishin.pdf.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfiguration {

    private final Utils utils;
    private final MasterSaveService masterSaveService;
    private final Logger logger = LoggerFactory.getLogger("daishin");

    //14시 05분에 실행
    @Scheduled(cron = "00 10 14 * * *")
    public void run() throws IOException {
        if(checkTime()){

            Master master = new Master();
            master.setMASTER_KEY(LocalDate.now()+"");
            master.setSTATUS("2(수신완료)");
            //master 작업 상태 업데이트
            masterSaveService.updateStatus(master);
            //json 파일 생성
            utils.saveJson(LocalDate.now()+"" , logger);

        }
    }

    //현재 시각이 14:00 이후인지 체크하는 메서드
    private boolean checkTime(){
        return LocalTime.now().isAfter(LocalTime.of(14 , 0));
    }


}
