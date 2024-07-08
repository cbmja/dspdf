package com.daishin.pdf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Error {

    @JsonProperty("NUM")
    private int NUM;
    @JsonProperty("ERROR_MESSAGE")
    private String ERROR_MESSAGE;
    @JsonProperty("CREATE_DATE")
    private LocalDateTime CREATE_DATE;
    @JsonProperty("REPOSITORY")
    private String REPOSITORY;
    @JsonProperty("MASTER_KEY")
    private String MASTER_KEY;

}
