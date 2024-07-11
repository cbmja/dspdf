package com.daishin.pdf.page;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorSearch {


    private String search ="";
    private int page = 1;
    private String sort = "DESC"; //오름차순 , 내림차순 asc desc
    private String cate = "MASTER_KEY"; //검색 항목 where 다음
    private String sortCate = "CREATE_DATE";
    private int pageElement = 20;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}", message = "Invalid date format, please use 'yyyy-MM-ddTHH:mm'")
    private LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}", message = "Invalid date format, please use 'yyyy-MM-ddTHH:mm'")
    private LocalDateTime endDate = LocalDateTime.now().plusDays(1);

}
