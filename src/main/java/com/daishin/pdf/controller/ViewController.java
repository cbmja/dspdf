package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
import com.daishin.pdf.page.SelectOption;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import com.daishin.pdf.service.DetailInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final MasterInfoService masterInfoService;
    private final MasterSaveService masterSaveService;

    private final DetailInfoService detailInfoService;


    @GetMapping("/mList")
    public String masterList(@ModelAttribute Search search, Model model){

        //페이징 처리
        Page page = new Page(search.getPage() , masterInfoService.countSearch(search) , search);
        System.out.println(page+"////////////");
        model.addAttribute("p" , page);
        List<Master> masterList = masterInfoService.selectMastersByPage(page);
        model.addAttribute("masterList" , masterList);

        //selectOption
        model.addAttribute("cateList" , SelectOption.getCateList());
        model.addAttribute("statusList" , SelectOption.getStatusList());
        model.addAttribute("sortCateList" , SelectOption.getSortCateList());
        model.addAttribute("sortList" , SelectOption.getSortList());
        //페이징 처리
        return "masterList";
    }

    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam("statusData") String statusData , @ModelAttribute Search search , Model model) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> statusMap = objectMapper.readValue(statusData, new TypeReference<Map<String, String>>() {});

        for(String master_key : statusMap.keySet()){
            Master master = masterInfoService.findMaster(master_key);
            //master.setMASTER_KEY(master_key);
            master.setSTATUS(Integer.parseInt(statusMap.get(master_key)));

            //실시간 건에 대하여 상태를 1(수신중)으로 바꾸려는 경우
            //total_send_cnt 를 다시 "수신중"으로 바꿔줌
            if(statusMap.get(master_key).equals("1") && !master.getTOTAL_SEND_CNT().equals("수신중") && master.getTYPE().equals("REAL_TIME")){
                master.setTOTAL_SEND_CNT("수신중");
                masterSaveService.updateStatusAndTotalCnt(master);
            }else if(!statusMap.get(master_key).equals("1") && master.getTOTAL_SEND_CNT().equals("수신중")){
            //현재 상태가 1(수신중)인 실시간 건수에 대해서 다른 상태로 바꾸는 경우 (total_send_cnt 가 "수신중"이면 현재 상태가 1인 실시간 건임)
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
        List<Master> masterList = masterInfoService.selectMastersByPage(page);
        model.addAttribute("masterList" , masterList);

        //selectOption
        model.addAttribute("cateList" , SelectOption.getCateList());
        model.addAttribute("statusList" , SelectOption.getStatusList());
        model.addAttribute("sortCateList" , SelectOption.getSortCateList());
        model.addAttribute("sortList" , SelectOption.getSortList());
        //페이징 처리
        return "masterList";
    }

}
