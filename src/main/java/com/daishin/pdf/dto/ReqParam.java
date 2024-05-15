package com.daishin.pdf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReqParam {

    @JsonProperty("TR_KEY")
    private String TR_KEY;
    @JsonProperty("TOTAL_SEND_CNT")
    private String TOTAL_SEND_CNT;
    @JsonProperty("DLV_TYPE_CD")
    private String DLV_TYPE_CD;
    @JsonProperty("PRINT_TYPE_NM")
    private String PRINT_TYPE_NM;
    @JsonProperty("PAGE_CNT")
    private String PAGE_CNT;
    @JsonProperty("RET_YN")
    private String RET_YN;
    @JsonProperty("RECV_NUM")
    private String RECV_NUM;
    @JsonProperty("DM_LINK_KEY")
    private String DM_LINK_KEY;
    @JsonProperty("CIFNO")
    private String CIFNO;
    @JsonProperty("DLV_CD")
    private String DLV_CD;
    @JsonProperty("RECV_NM")
    private String RECV_NM;
    @JsonProperty("RECV_POST_CD")
    private String RECV_POST_CD;
    @JsonProperty("RECV_ADDR")
    private String RECV_ADDR;
    @JsonProperty("RECV_ADDR_DETAIL")
    private String RECV_ADDR_DETAIL;
    @JsonProperty("PDF_PATH")
    private String PDF_PATH;
    @JsonProperty("SAVE_DATE")
    private String SAVE_DATE;
    @JsonProperty("STATUS")
    private String STATUS;



}
