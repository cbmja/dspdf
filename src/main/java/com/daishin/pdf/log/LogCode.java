package com.daishin.pdf.log;

public interface LogCode {

    String INFORMATION = "INFO";
    String DURATION_TIME = "DT";
    String DUPLICATE_VALUE = "DV"; //중복
    String MISSING_VALUE = "MV"; //필수값 누락
    String DATABASE_ERROR = "DBE"; //DB 오류
    String FILE_SAVE_FAIL = "FF"; //파일 저장 오류
    String JSON_SAVE_FAIL = "JF"; //파일 저장 오류

}
