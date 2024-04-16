package com.daishin.pdf.dto;

import lombok.Data;

@Data
public class Master {

    private String apiKey; //기업 식별키
    private String trKey; //전송단위그룹 식별키
    private String totalSendCnt; //단위 그룹 인원
    private String dlvSttusCd; //배달 항목 : 일반 , 등기 , 내용증명 , 배달증명 등
    private String printTypeNm; //인쇄 유형 : 안내장 , 등
    private String pageCnt; //페이지 수



}
