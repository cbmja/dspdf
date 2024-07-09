package com.daishin.pdf.response;

public interface ResponseCode {

    //KEY
    String RESULT = "결과";
    String REMARK = "비고";
    String ERROR = "ERROR";
    String SUCCESS = "SUCCESS";

    String UPDATE = "UPDATE";


    //VALUE
    String OK = "OK";
    String SQL_ERROR = "SQL_ERROR";
    String FILE_ERROR = "FILE_ERROR";
    String JSON_ERROR = "JSON_ERROR";
    String MISSING_VALUE = "MISSING_VALUE : ";
    String PROCESSED_REQUEST  = "PROCESSED_REQUEST  : ";

}
