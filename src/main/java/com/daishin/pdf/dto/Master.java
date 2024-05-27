package com.daishin.pdf.dto;


import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
public class Master {

    private String MASTER_KEY; //대량일 경우 TR_KEY , 단일건일 경우 날짜
    private String TOTAL_SEND_CNT; //총 건수 : 대량일 경우 TOTAL_SEND_CNT , 단일건일 경우 전일 14:00이상 ~ 금일 14:00미만 전송 건수
    private int SEND_CNT; //현재 전송 건수
    private String STATUS; //상태 : 대기중 , 출력중 , 발송중 등등
    private LocalDateTime RECEIVED_TIME; //마지막 건수 저장시간
}
