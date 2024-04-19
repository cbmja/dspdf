package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.service.detail.DetailSaveService;
import com.daishin.pdf.service.master.MasterInfoService;
import com.daishin.pdf.service.master.MasterSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller //우편 제작 요청 api
@RequiredArgsConstructor
public class PostalCreationRequestController {

        private final MasterSaveService masterSaveService;
        private final MasterInfoService masterInfoService;

        private final DetailSaveService detailSaveService;


    @PostMapping("/pcReq")
    @ResponseBody
    /*@RequestBody Master master , @RequestBody(required = false) Map test*/
    public Map<String, String> jsonRequest(@RequestPart(name="master" , required = false) Map master , @RequestPart(name="pdf", required = false) MultipartFile pdf
                                            , @RequestPart(name="detail", required = false) List<Map> detail){
/*
        System.out.println(master);
        System.out.println("//////////////");
        System.out.println(pdf);
        System.out.println("//////////////");
        System.out.println(detail);
*/
        //결과 코드//
        Map<String , String> response = new LinkedHashMap<>();

        //우편 제작 요청 데이터 map -> DTO//
        Master master_ = mapToMaster(master);
        List<Detail> detailList = mapToDetailList(detail , master_ , pdf);

        //nullcheck//
        masterNullCheck(master_ , response);
        detailNullCheck(detailList , response);





        //누락된 항목 있을 경우 저장하지않음//
        if(response.size() > 0){
            return response;
        }

        for(Detail d : detailList){
            detailSaveService.save(d);
        }

        response.put("전송 완료" , "전송 완료");
        //이미 존재하는 tr_key이면 수정
        if(masterInfoService.findByTrKey(master_.getTrKey()) != null){
            response.put("master 수정" , "master 수정");
            masterSaveService.update(master_);
        } else { //없으면 저장
            masterSaveService.save(master_);
        }




        return response;


    }

    //master 필수값 체크
    private void masterNullCheck(Master master , Map response){

        if(master.getApiKey() == null){
            response.put("apikey 누락" , "apikey 누락");
        }
        if(master.getTrKey() == null){
            response.put("trKey 누락" , "trKey 누락");
        }
        if(master.getDlvSttusCd() == null){
            response.put("dlvSttusCd 누락" , "dlvSttusCd 누락");
        }
        if(master.getPrintTypeNm() == null){
            response.put("printTypeNm 누락" , "printTypeNm 누락");
        }

    }
    //detail 필수값 체크
    private void detailNullCheck(List<Detail> detailList , Map response){

        if (detailList.size() <= 0){
            response.put("detail이 없습니다" , "detail이 없습니다");
        } else {
        //dmlinkKey 누락된 사람은 어떻게 알려줘야함
            for (Detail detail : detailList){
                if(detail.getRecvNum() == null){
                    response.put("["+detail.getDmLinkKey()+"] recvNum 누락" , "recvNum 누락");
                }
                if(detail.getDmLinkKey() == null){
                    response.put("["+detail.getDmLinkKey()+"] dmLinkKey 누락" , "dmLinkKey 누락");
                }
                if(detail.getPostCode() == null){
                    response.put("["+detail.getDmLinkKey()+"] postCode 누락" , "postCode 누락");
                }
                if(detail.getPdfYn() == null){
                    response.put("["+detail.getDmLinkKey()+"] pdfYn 누락" , "pdfYn 누락");
                }
                //pdfYn 이 Y 인데 pdf파일이 없을 경우
                if(detail.getPdfYn().equals("Y") && detail.getPdf() == null){
                    response.put("["+detail.getDmLinkKey()+"] pdf 누락" , "pdf 누락");
                }
                //pdfYn 이 N 인데 docdata파일이 없을 경우
                if(detail.getPdfYn().equals("N") && detail.getDocData() == null){
                    response.put("["+detail.getDmLinkKey()+"] docData 누락" , "docData 누락");
                }
            }
        }
    }


    private Master mapToMaster(Map master){
        Master master_ = new Master();
        master_.setApiKey((String)master.get("apiKey"));
        master_.setTrKey((String)master.get("trKey"));
        master_.setTotalSendCnt((String)master.get("totalSendCnt"));
        master_.setDlvSttusCd((String)master.get("dlvSttusCd"));
        master_.setPrintTypeNm((String)master.get("printTypeNm"));
        master_.setPageCnt((String)master.get("pageCnt"));
        master_.setPstFile((String)master.get("pstFile"));
        return master_;
    }

    private List<Detail> mapToDetailList(List<Map> detail , Master master_ , MultipartFile pdf){
        List<Map> details = detail;

        List<Detail> detailList = new ArrayList<>();

        for (Map detail_ : details){
            Detail d = new Detail();
            d.setTrKey(master_.getTrKey());
            d.setRecvNum((String)detail_.get("recvNum"));
            d.setDmLinkKey((String)detail_.get("dmLinkKey"));
            d.setRegNo((String)detail_.get("regNo"));
            d.setPostCode((String)detail_.get("postCode"));
            d.setRetYn((String)detail_.get("retYn"));
            d.setPdfYn((String)detail_.get("pdfYn"));
            d.setDocData((String)detail_.get("docData"));
            if(d.getPdfYn().equals("N")){
                d.setDocDataStatus("접수완료");
            }
            if(d.getPdfYn().equals("Y")){
                d.setPdf(pdf);
            }

            detailList.add(d);
        }

        return detailList;
    }



}
