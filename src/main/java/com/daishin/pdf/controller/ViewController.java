package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
import com.daishin.pdf.page.SelectOption;
import com.daishin.pdf.response.ResponseCode;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import com.daishin.pdf.service.DetailInfoService;
import com.daishin.pdf.service.StatusInfoService;
import com.daishin.pdf.util.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final MasterInfoService masterInfoService;
    private final MasterSaveService masterSaveService;

    private final DetailInfoService detailInfoService;

    private final SelectOption selectOption;

    private final StatusInfoService statusInfoService;

    private final Utils utils;

    private final Logger logger = LoggerFactory.getLogger("daishin");


    @GetMapping("/masters")
    public String masterList(@ModelAttribute Search search, Model model){

        //페이징 처리
        Page page = new Page(search.getPage() , masterInfoService.countSearch(search) , search);
        model.addAttribute("p" , page);
        List<Master> masters = masterInfoService.selectMastersByPage(page);

        List<Master> masterList = masters.stream().map(m ->{
            m.setStatusName(statusInfoService.selectByStatusCode(m.getSTATUS()).getSTATUS_NAME());
            m.setTypeName(m.getTYPE().equals("REAL_TIME")?"실시간":"배치");
            return m;
        }).collect(Collectors.toList());

        model.addAttribute("masterList" , masterList);
        //selectOption
        model.addAttribute("cateList" , selectOption.getCateList());
        model.addAttribute("statusList" , selectOption.getStatusList());
        model.addAttribute("sortCateList" , selectOption.getSortCateList());
        model.addAttribute("sortList" , selectOption.getSortList());
        //페이징 처리
        return "masterList";
    }

    @PostMapping("/masters/update")
    public String changeStatus(@RequestParam("statusData") String statusData , @ModelAttribute Search search , Model model) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> statusMap = objectMapper.readValue(statusData, new TypeReference<Map<String, String>>() {});

        for(String master_key : statusMap.keySet()){
            Master master = masterInfoService.findMaster(master_key);
            if(master != null && master.getError().equals(ResponseCode.SQL_ERROR)){
                break;
            }
            //master.setMASTER_KEY(master_key);
            master.setSTATUS(Integer.parseInt(statusMap.get(master_key)));

            //바꾸려는 상태가 100(수신중)인 실시간 건
            //total_send_cnt 를 다시 "수신중"으로 바꿔줌
            if(statusMap.get(master_key).equals("100") && master.getTYPE().equals("REAL_TIME")){
                master.setTOTAL_SEND_CNT("수신중");
                masterSaveService.updateStatusAndTotalCnt(master);
            }else if(!statusMap.get(master_key).equals("100") && master.getTOTAL_SEND_CNT().equals("수신중")){
            //현재 상태가 수신중이고 , 수신중 이외의 상태로 바꾸려는 경우
            //total_send_cnt 를 현재 수신 건수로 바꿔줌    
                int total = detailInfoService.countMaster(master.getMASTER_KEY());
                master.setTOTAL_SEND_CNT(total+"");
                masterSaveService.updateStatusAndTotalCnt(master);
            }else{
            //이외의 경우에는 상태만 업데이트
                masterSaveService.updateStatus(master);
            }

        }
        
        //수정 이후에도 이후 작업을 동일한 페이지에서 이어서 할 수 있도록 redirect 하지 않고 여기서 페이징 처리
        //페이징 처리
        Page page = new Page(search.getPage() , masterInfoService.countSearch(search) , search);
        model.addAttribute("p" , page);
        List<Master> masters = masterInfoService.selectMastersByPage(page);


        List<Master> masterList = masters.stream().map(m ->{
            m.setStatusName(statusInfoService.selectByStatusCode(m.getSTATUS()).getSTATUS_NAME());
            m.setTypeName(m.getTYPE().equals("REAL_TIME")?"실시간":"배치");
            return m;
        }).collect(Collectors.toList());
        model.addAttribute("masterList" , masterList);

        //selectOption
        model.addAttribute("cateList" , selectOption.getCateList());
        model.addAttribute("statusList" , selectOption.getStatusList());
        model.addAttribute("sortCateList" , selectOption.getSortCateList());
        model.addAttribute("sortList" , selectOption.getSortList());
        //페이징 처리
        return "masterList";
    }


/*
    @GetMapping("/masters/move")
    public String move(){

        utils.createMove(logger);

        return "redirect:/masters";
    }
*/


}
