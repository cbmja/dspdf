package com.daishin.pdf.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Status {

    private int NUM;
    private int STATUS_CODE;
    private String WAIT_TIME;
    private String CHANGE_TYPE;
    private LocalDateTime CREATE_DATE;
    private String STATUS_NAME;

}
