package com.daishin.pdf.page;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class Search {

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
    @Pattern(regexp = "MASTER_KEY|STATUS|TYPE")
    private String cate = "MASTER_KEY"; //검색 항목 where 다음
    @NotNull
    @Pattern(regexp = "MASTER_KEY|STATUS|RECEIVED_TIME|STATUS_TIME|TYPE")
    private String sortCate = "STATUS_TIME";
    @Min(1)
    @Max(200)
    private int pageElement = 20;



}
