package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.service.detail.DetailSaveService;
import com.daishin.pdf.service.master.MasterSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller //우편 제작 요청 api
@RequiredArgsConstructor
public class PostalCreationRequestController {

        private final MasterSaveService masterSaveService;
        private final DetailSaveService detailSaveService;

    @PostMapping("/pcReq/form")
    @ResponseBody
    public Map<String, String> formRequest(Master master , Detail detail){

        Map<String , String > response = new LinkedHashMap<>();
        response.put("succ" , "성공");
        System.out.println(master);
        System.out.println("////////////");
        System.out.println(detail);
        masterSaveService.save(master);
        return response;
    }

    @PostMapping("/pcReq/json")
    @ResponseBody
    /*@RequestBody Master master , @RequestBody(required = false) Map test*/
    public Map<String, String> jsonRequest(@RequestBody Map requests){

        Master master = mapToMaster(requests);
        List<Detail> detailList = mapToDetailList(requests);

        for(Detail d : detailList){
            detailSaveService.save(d);
            System.out.println("+++++++++++++++");
            System.out.println(d);
        }

        Map<String , String > response = new LinkedHashMap<>();
        response.put("succ" , "성공");
        System.out.println("/////////////////");
        System.out.println(master);
        masterSaveService.save(master);
        return response;
    }



    private Master mapToMaster(Map requests){
        Master master = new Master();
        master.setApiKey((String)requests.get("apiKey"));
        master.setTrKey((String)requests.get("trKey"));
        master.setTotalSendCnt((String)requests.get("totalSendCnt"));
        master.setDlvSttusCd((String)requests.get("dlvSttusCd"));
        master.setPrintTypeNm((String)requests.get("printTypeNm"));
        master.setPageCnt((String)requests.get("pageCnt"));
        return master;
    }

    private List<Detail> mapToDetailList(Map requests){
        List<Map> details = (List<Map>) requests.get("details");
        List<Detail> detailList = new ArrayList<>();
        for (Map detail : details){
            Detail d = new Detail();
            d.setTrKey((String)requests.get("trKey"));
            d.setRecvNum((String)detail.get("recvNum"));
            d.setDmLinkKey((String)detail.get("dmLinkKey"));
            d.setRegNo((String)detail.get("regNo"));
            d.setPostCode((String)detail.get("postCode"));
            d.setRetYn((String)detail.get("retYn"));
            d.setPdfYn((String)detail.get("pdfYn"));
            d.setDocData((String)detail.get("docData"));
            d.setPdf((String)detail.get("pdf"));
            detailList.add(d);
        }
        return detailList;
    }



}
