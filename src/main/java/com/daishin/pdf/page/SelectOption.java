package com.daishin.pdf.page;


import com.daishin.pdf.dto.Status;
import com.daishin.pdf.service.StatusInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SelectOption {

    private final StatusInfoService statusInfoService;

    public LinkedHashMap<String,String> getCateList(){

        LinkedHashMap<String,String> cateList = new LinkedHashMap<>();
        cateList.put("MASTER_KEY" , "그룹키");
        cateList.put("STATUS" , "현재 상태");
        cateList.put("TYPE" , "타입");
        return cateList;
    }
    public List<Status> getStatusList(){

        /*      List<String> statusList = new ArrayList<>();
        statusList.add("1");
        statusList.add("2");
        statusList.add("3");
        statusList.add("4");
        statusList.add("5");*/
        return statusInfoService.selectAll();
    }
    public LinkedHashMap<String,String> getSortCateList(){

        LinkedHashMap<String,String> sortCateList = new LinkedHashMap<>();
        sortCateList.put("MASTER_KEY" , "그룹키");
        sortCateList.put("STATUS" , "현재 상태");
        sortCateList.put("RECEIVED_TIME" , "마지막 건수 수신 시각");
        sortCateList.put("STATUS_TIME" , "상태 변경 시각");
        sortCateList.put("TYPE" , "타입");

        return sortCateList;
    }
    public LinkedHashMap<String,String> getSortList(){

        LinkedHashMap<String,String> sortList = new LinkedHashMap<>();
        sortList.put("ASC" , "오름차순");
        sortList.put("DESC" , "내림차순");

        return sortList;
    }

}
