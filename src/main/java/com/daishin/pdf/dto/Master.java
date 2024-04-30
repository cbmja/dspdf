package com.daishin.pdf.dto;

import lombok.Data;

@Data
public class Master {

    private String trKey; //전송단위그룹 식별키0
    private String totalSendCnt; //단위 그룹 인원0
    private String dlvTypeCd; //배달 항목 : 일반 , 등기 , 내용증명 , 배달증명 등0
    private String printTypeNm; //인쇄 유형 : 안내장 , 등0
    private String pageCnt; //페이지 수0
    private String apiKey; //기업 식별키0
    private String retYn; //반송여부0
    private String pdfYn; //PDF여부




}
