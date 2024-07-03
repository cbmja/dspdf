package com.daishin.pdf.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
public class Master {
    @JsonProperty("MASTER_KEY")
    private String MASTER_KEY; //대량일 경우 TR_KEY , 단일건일 경우 날짜
    @JsonProperty("TOTAL_SEND_CNT")
    private String TOTAL_SEND_CNT; //총 건수 : 대량일 경우 TOTAL_SEND_CNT , 단일건일 경우 전일 14:00이상 ~ 금일 14:00미만 전송 건수
    @JsonProperty("SEND_CNT")
    private int SEND_CNT; //현재 전송 건수
    @JsonProperty("STATUS")
    private int STATUS; //상태 : 대기중 , 출력중 , 발송중 등등
    @JsonProperty("RECEIVED_TIME")
    private LocalDateTime RECEIVED_TIME; //마지막 건수 저장시간
    @JsonProperty("STATUS_TIME")
    private LocalDateTime STATUS_TIME; //현재 상태 시작 시간
    @JsonProperty("TYPE")
    private String TYPE; //배치 : AR / 실시간 : RT

    @JsonIgnore
    private String error="";
    @JsonIgnore
    private String statusName;
    @JsonIgnore
    private String typeName;
}
