package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import com.daishin.pdf.service.DetailInfoService;
import com.daishin.pdf.service.DetailSaveService;
import com.daishin.pdf.util.Utils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

@Controller //우편 제작 요청 api
@RequiredArgsConstructor
public class PostalCreationRequestController {

        private final DetailSaveService detailSaveService;
        private final DetailInfoService detailInfoService;

        private final Utils utils;

        private final MasterInfoService masterInfoService;
        private final MasterSaveService masterSaveService;

        private final Logger logger = LoggerFactory.getLogger("daishin");

    @PostMapping("/detail")
    @ResponseBody
    public Map<String , String> uploadAndUnzip(@RequestParam(name = "File" , required = false) MultipartFile File , @ModelAttribute Detail req){
        long startTime = System.nanoTime();

        //결과값
        Map<String , String> response = new LinkedHashMap<>();

        //요청 정보 log
        logger.info("request body : " +req);
        if(File != null){
            logger.info("pdf file name : "+File.getOriginalFilename());
        }
        //요청 정보 log

        //필수 항목 누락 체크
        if(reqCheck(req , logger)){
            response.put("필수값 누락" , "필수값 누락");
        }
        if(fileCheck(File , logger)){
            response.put("pdf 누락" , "pdf 누락");
        }
        if(!response.isEmpty()){
            return response;
        }
        //필수 항목 누락 체크

        //중복 체크
        req.setPK(req.getTR_KEY()+"_"+req.getRECV_NUM());

        if(detailInfoService.findReq(req) != null){
            logger.error("중복된 요청 : "+req);
            response.put("중복된 요청 : "+req , "중복된 요청 : "+req);
            return response;
        }
        //중복 체크

        //파일 저장 (pdf)
        utils.savePdf(File , req , response , logger);

        //DB 저장(detail)
        //master_key 값 세팅
        if(detailSaveService.save(req) <= 0){
            logger.error("DB 저장 실패 : "+req);
        }

        //master 값 세팅
        String MASTER_KEY = req.getMASTER();
        Master master = new Master(); //배치(대량)은 tr_key / 실시간(단일)은 날짜
        master.setMASTER_KEY(MASTER_KEY);
        master.setRECEIVED_TIME(detailInfoService.findReq(req).getSAVE_DATE());
        //master 값 세팅

        //master 최초 저장
        if(masterInfoService.findMaster(MASTER_KEY) == null){
            if(!req.getTOTAL_SEND_CNT().equals("1")){
                master.setTYPE("ARRANGEMENT"); //배치(대량)
                master.setTOTAL_SEND_CNT(req.getTOTAL_SEND_CNT());
            }else{
                master.setTOTAL_SEND_CNT("수신중");
                master.setTYPE("REAL_TIME"); //실시간(단일)
            }
            master.setSEND_CNT(1);
            master.setSTATUS(1);
            masterSaveService.save(master);
        }else{
        //전송 건수 갱신
            Master _master = masterInfoService.findMaster(MASTER_KEY);

            _master.setRECEIVED_TIME(detailInfoService.findReq(req).getSAVE_DATE());
            _master.setSEND_CNT(_master.getSEND_CNT()+1);
            masterSaveService.updateSendCnt(_master);
        }

        //배치(대량)그룹 전송 완료시 처리 / status 갱신 : 1(수신중) -> 2(수신완료) , JSON 파일저장
        if(!req.getTOTAL_SEND_CNT().equals("1") && detailInfoService.countGroup(req)==Integer.parseInt(req.getTOTAL_SEND_CNT())){
            master.setSTATUS(2);
            masterSaveService.updateStatus(master);
            //JSON 파일 생성 및 저장
            utils.saveJson(req.getMASTER()  , logger);
        }
        
        long endTime = System.nanoTime();
        logger.info("처리 시간 : "+(endTime - startTime));

        response.put("결과","OK");
        response.put("비고","SUCCESS");
        return response;

    }

    private boolean reqCheck(Detail req , Logger logger){
        String errMsg = "";
        if(req.getTR_KEY() == null || req.getTR_KEY().isEmpty()){
            errMsg += "TR_KEY , ";
        }
        if(req.getDLV_TYPE_CD() == null || req.getDLV_TYPE_CD().isEmpty()){
            errMsg += "DLV_TYPE_CD , ";
        }
        if(req.getPRINT_TYPE_NM() == null || req.getPRINT_TYPE_NM().isEmpty()){
            errMsg += "PRINT_TYPE_NM , ";
        }
        if(req.getRECV_NUM() == null || req.getRECV_NUM().isEmpty()){
            errMsg += "RECV_NUM , ";
        }
        if(req.getDM_LINK_KEY() == null || req.getDM_LINK_KEY().isEmpty()){
            errMsg += "DM_LINK_KEY , ";
        }
        if(req.getPDF_NM() == null || req.getPDF_NM().isEmpty()){
            errMsg += "PDF_NM , ";
        }
        if(req.getRECV_NM() == null || req.getRECV_NM().isEmpty()){
            errMsg += "RECV_NM , ";
        }
        if(req.getRECV_POST_CD() == null || req.getRECV_POST_CD().isEmpty()){
            errMsg += "RECV_POST_CD , ";
        }
        if(req.getRECV_ADDR() == null || req.getRECV_ADDR().isEmpty()){
            errMsg += "RECV_ADDR , ";
        }

        if(!errMsg.isEmpty()){
            errMsg = errMsg.substring(0, errMsg.length() - 2);
            logger.error(errMsg+"누락");
            return true;
        }else {
            return false;
        }


    }

    
    private boolean fileCheck(MultipartFile File , Logger logger){
        if(File == null || File.isEmpty()){
            logger.error("pdf file 누락");
            return true;
        } else {
            return false;
        }
    }



}
