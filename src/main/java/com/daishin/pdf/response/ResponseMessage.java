package com.daishin.pdf.response;

public interface ResponseMessage {

    String SU = "Success";
    String DV = "Duplicate vlue"; //중복
    String MV = "Missing value"; //필수값 누락
    String DBE = "Database error"; //DB 오류
    String FF = "File save fail"; //파일 저장 오류

}
