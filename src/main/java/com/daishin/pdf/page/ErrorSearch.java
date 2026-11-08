package com.daishin.pdf.page;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorSearch {


    @NotNull
    @Size(max = 1000)
    private String search ="";
    @Min(1)
    @Max(1000000)
    private int page = 1;
    @NotNull
    @Pattern(regexp = "ASC|DESC")
    private String sort = "DESC"; //오름차순 , 내림차순 asc desc
    @NotNull
    @Pattern(regexp = "CREATE_DATE|ERROR_CODE|MASTER_KEY")
    private String cate = "MASTER_KEY"; //검색 항목 where 다음
    @NotNull
    @Pattern(regexp = "CREATE_DATE")
    private String sortCate = "CREATE_DATE";
    @Min(1)
    @Max(200)
    private int pageElement = 20;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate = LocalDateTime.now().plusDays(1);

}
