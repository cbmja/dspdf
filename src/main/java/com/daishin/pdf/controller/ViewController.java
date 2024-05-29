package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import com.daishin.pdf.service.ReqInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final MasterInfoService masterInfoService;
    private final MasterSaveService masterSaveService;
    private final ReqInfoService reqInfoService;


    @GetMapping("/mList")
    public String masterList(@ModelAttribute Search search, Model model){

        int total = masterInfoService.countSearch(search.getSearch());

        Page page = new Page(search.getPage() , total);
        page.setSearch(search.getSearch());

        model.addAttribute("total" , total);
        model.addAttribute("p" , page);

        List<Master> masterList = masterInfoService.selectMastersByPage(page);

        model.addAttribute("masterList" , masterList);

        return "masterList";
    }

    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam("statusData") String statusData) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> statusMap = objectMapper.readValue(statusData, new TypeReference<Map<String, String>>() {});

        for(String master_key : statusMap.keySet()){
            Master master = masterInfoService.findMaster(master_key);
            //master.setMASTER_KEY(master_key);
            master.setSTATUS(Integer.parseInt(statusMap.get(master_key)));
            if(master.getTOTAL_SEND_CNT().equals("실시간")){
                int total = reqInfoService.countMaster(master.getMASTER_KEY());
                master.setTOTAL_SEND_CNT(total+"");
                masterSaveService.updateStatusAndTotalCnt(master);
            }else{
                masterSaveService.updateStatus(master);
            }

        }


        return "redirect:/mList";
    }

}
