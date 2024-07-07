package com.daishin.pdf.log;

public interface LogCode {



    //ERROR
    String SQL_ERROR = "SQL_ERROR"; //SQL 오류
    String JSON_ERROR = "JSON_ERROR"; //JSON 파일 생성 오류
    String PDF_ERROR = "PDF_ERROR"; //PDF 파일 저장 오류
    String MISSING_VALUE = "MISSING_VALUE"; //필수 항목 누락
    String DUPLICATE_VALUE = "DUPLICATE_VALUE"; //중복 전송 TR_KEY 와 RECV_NUM 이 동일한 경우 중복으로 처리
    String FILE_MOVE_ERROR = "FILE_MOVE_ERROR"; //파일 이동 오류 receiving -> complete 이동중 오류


    //INFO
    String DETAIL_REQUEST = "DETAIL_REQUEST"; //수신 데이터
    String WORK_TIME = "WORK_TIME"; //처리 시간

}
