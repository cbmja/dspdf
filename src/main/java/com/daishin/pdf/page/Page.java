package com.daishin.pdf.page;

import lombok.Data;

@Data
public class Page {

    private int total;//총 게시물 수 ok
    
    private int pageElement = 2;//한 페이지에 보여줄 게시물 수 ok
    private int pageSize = 2;//보여줄 페이지 블럭 수 ok
    private int startPage;//보여지는 블럭의 시작번호
    private int endPage;//보여지는 블럭의 마지막 번호


    private int page; //현재 페이지 ok
    private int totalPage;//총 페이지 수
    
    private int startNum;//첫 게시물 번호 ok
    private int endNum;//마지막 게시물 번호 ok

    private int currentPageBlock;//현재 보이는 페이지 블럭 수

    /**
     * 현재페이지 , 총 게시물 수
     * @param page
     * @param total
     */
    public Page(int page , int total){
        this.page = page;
        this.total = total;
        this.totalPage = total % this.pageElement > 0 ? total / this.pageElement + 1 : total / this.pageElement;

        this.startNum = (this.page-1)*this.pageElement;
        this.endNum = this.page == this.totalPage ? this.total : startNum + this.pageElement - 1;


        this.endPage = (( this.startNum / (this.pageElement * this.pageSize) ) * this.pageSize) + this.pageSize > this.totalPage
                                ? this.totalPage : (( this.startNum / (this.pageElement * this.pageSize) ) * this.pageSize) + this.pageSize;

        this.startPage = ((( this.startNum / (this.pageElement * this.pageSize) ) * this.pageSize) + this.pageSize)-this.pageSize+1;

        if(this.totalPage % this.pageSize == 0){

        }else{
            if(this.totalPage > this.pageSize){

            }
            if(this.totalPage < this.pageSize){

            }


        }

    }


}
