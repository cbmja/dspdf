package com.daishin.pdf.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Error {

    private int ID;
    private String ERROR_MESSAGE;
    private LocalDateTime CREATE_DATE;
    private String MASTER_KEY="";
    private String ERROR_CODE;


}
