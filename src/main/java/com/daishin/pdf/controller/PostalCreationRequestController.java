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
    public Map<String, String> jsonRequest(@RequestPart(name="master" , required = false) Map master , @RequestPart(name="pdf", required = false) MultipartFile pdf
                                            , @RequestPart(name="detail", required = false) List<Map> detail){

        //결과 코드//
        Map<String , String> response = new LinkedHashMap<>();

        //request map -> DTO//
        Master master_ = mapToMaster(master);
        List<Detail> detailList = mapToDetailList(detail , master_ , pdf);

        //master update
        if(masterInfoService.findByTrKey(master_.getTrKey()) != null && detailList.size() == 0){

            Master oMaster = masterInfoService.findByTrKey(master_.getTrKey());
            Master yMaster = master_;
            int cnt = 1;

            if(!oMaster.getTotalSendCnt().equals(yMaster.getTotalSendCnt())){
                response.put("[ "+oMaster.getTrKey()+" ] master 수정"+ cnt++ , "[ TOTAL_SEND_CNT ] : "+oMaster.getTotalSendCnt() +" -> "+yMaster.getTotalSendCnt());
            }
            if(!oMaster.getDlvSttusCd().equals(yMaster.getDlvSttusCd())){
                response.put("[ "+oMaster.getTrKey()+" ] master 수정"+ cnt++ , "[ DLV_STTUS_CD ] : "+oMaster.getDlvSttusCd() +" -> "+yMaster.getDlvSttusCd());
            }
            if(!oMaster.getPrintTypeNm().equals(yMaster.getPrintTypeNm())){
                response.put("[ "+oMaster.getTrKey()+" ] master 수정"+ cnt++ , "[ PRINT_TYPE_NM ] : "+oMaster.getPrintTypeNm() +" -> "+yMaster.getPrintTypeNm());
            }
            if(!oMaster.getPageCnt().equals(yMaster.getPageCnt())){
                response.put("[ "+oMaster.getTrKey()+" ] master 수정"+ cnt++ , "[ PAGE_CNT ] : "+oMaster.getPageCnt() +" -> "+yMaster.getPageCnt());
            }
            if(!oMaster.getApiKey().equals(yMaster.getApiKey())){
                response.put("[ "+oMaster.getTrKey()+" ] master 수정"+ cnt++ , "[ API_KEY ] : "+oMaster.getApiKey() +" -> "+yMaster.getApiKey());
            }
            if(!oMaster.getPstFile().equals(yMaster.getPstFile())){
                response.put("[ "+oMaster.getTrKey()+" ] master 수정"+ cnt++ , "[ PST_FILE ] : "+oMaster.getPstFile() +" -> "+yMaster.getPstFile());
            }

            masterSaveService.update(master_);
            return response;
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
    private void detailNullCheck(Detail detail , Map response){

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

    //ok
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

    //ok
    private List<Detail> mapToDetailList(List<Map> detail , Master master_ , MultipartFile pdf){
        List<Map> details = detail;

        List<Detail> detailList = new ArrayList<>();
        if(details != null){
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
                if(d.getPdfYn().equals("N") && d.getDocData() != null){
                    d.setDocDataStatus("접수완료");
                }
                if(d.getPdfYn().equals("Y")){
                    d.setPdf(pdf);
                }
                detailList.add(d);
            }
        }
        //detail null 인 경우 detailList size 가 0
        return detailList;
    }



}
