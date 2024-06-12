package com.daishin.pdf.page;


import java.util.ArrayList;
import java.util.List;

public final class SelectOption {

    public static List<String> getCateList(){
        List<String> cateList = new ArrayList<>();
        cateList.add("MASTER_KEY");
        cateList.add("STATUS");
        cateList.add("TYPE");
        return cateList;
    }
    public static List<String> getStatusList(){
        List<String> statusList = new ArrayList<>();
        statusList.add("1");
        statusList.add("2");
        statusList.add("3");
        statusList.add("4");
        statusList.add("5");
        return statusList;
    }
    public static List<String> getSortCateList(){
        List<String> sortCateList = new ArrayList<>();
        sortCateList.add("MASTER_KEY");
        sortCateList.add("STATUS");
        sortCateList.add("RECEIVED_TIME");
        sortCateList.add("STATUS_TIME");
        sortCateList.add("TYPE");
        return sortCateList;
    }
    public static List<String> getSortList(){
        List<String> sortList = new ArrayList<>();
        sortList.add("ASC");
        sortList.add("DESC");
        return sortList;
    }

}
