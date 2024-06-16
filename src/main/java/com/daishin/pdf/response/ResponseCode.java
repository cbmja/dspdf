package com.daishin.pdf.response;

public interface ResponseCode {

    String SUCCESS = "SU";
    String DUPLICATE_VALUE = "DV"; //중복
    String MISSING_VALUE = "MV"; //필수값 누락
    String DATABASE_ERROR = "DBE"; //DB 오류
    String REQUEST = "REQ"; //DB 오류
    String FILE_SAVE_FAIL = "FF"; //파일 저장 오류
}
