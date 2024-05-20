package com.daishin.pdf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Master {

    @JsonProperty("MASTER_KEY")
    private String MASTER_KEY; //대량일 경우 TR_KEY , 단일건일 경우 날짜
    @JsonProperty("TOTAL_SEND_CNT")
    private String TOTAL_SEND_CNT; //총 건수 : 대량일 경우 TOTAL_SEND_CNT , 단일건일 경우 전일 14:00이상 ~ 금일 14:00미만 전송 건수
    @JsonProperty("STATUS")
    private String STATUS; //상태 : 대기중 , 출력중 , 발송중 등등
}
