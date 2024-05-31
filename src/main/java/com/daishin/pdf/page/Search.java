package com.daishin.pdf.page;

import lombok.Data;

@Data
public class Search {

    private String search ="";
    private int page =1;
    private String sort = "DESC"; //오름차순 , 내림차순 asc desc
    private String cate = "MASTER_KEY"; //검색 항목 where 다음
    private String sortCate = "STATUS_TIME";



}
