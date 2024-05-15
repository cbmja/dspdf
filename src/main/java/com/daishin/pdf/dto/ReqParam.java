package com.daishin.pdf.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReqParam {


    private String TR_KEY;
    private String TOTAL_SEND_CNT;
    private String DLV_TYPE_CD;
    private String PRINT_TYPE_NM;
    private String PAGE_CNT;
    private String RET_YN;
    private String RECV_NUM;
    private String DM_LINK_KEY;
    private String CIFNO;
    private String DLV_CD;
    private String RECV_NM;
    private String RECV_POST_CD;
    private String RECV_ADDR;
    private String RECV_ADDR_DETAIL;
    private String PDF_PATH;
    private LocalDateTime SAVE_DATE;
    private String STATUS;



}
