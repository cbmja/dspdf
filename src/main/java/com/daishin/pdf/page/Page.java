package com.daishin.pdf.page;

import lombok.Data;

@Data
public class Page {

    private int total;//총 게시물 수 ok
    
    private int pageElement = 2;//한 페이지에 보여줄 게시물 수 ok
    private int pageSize = 5;//페이지 블럭 수 ok

    private int page; //현재 페이지 ok
    private int totalPage;//총 페이지 수
    
    private int startNum;//첫 게시물 번호 ok
    private int endNum;//마지막 게시물 번호 ok

    /**
     * 현재페이지 , 총 게시물 수
     * @param page
     * @param total
     */
    public Page(int page , int total){
        this.page = page;
        this.total = total;
        this.totalPage = total % this.pageElement > 0 ? total / this.pageElement + 1 : total / this.pageElement;

        this.startNum = (this.page-1)*this.pageElement + 1 -1 ;
        this.endNum = this.page == this.totalPage ? this.total : startNum + this.pageElement - 1;

    }


}
