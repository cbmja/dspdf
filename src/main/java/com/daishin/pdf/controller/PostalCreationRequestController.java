package com.daishin.pdf.controller;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.service.ReqInfoService;
import com.daishin.pdf.service.ReqSaveService;
import com.daishin.pdf.util.Utils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller //우편 제작 요청 api
@RequiredArgsConstructor
public class PostalCreationRequestController {

        private final ReqSaveService reqSaveService;
        private final ReqInfoService reqInfoService;
        private final Utils utils;

        private final Logger logger = LoggerFactory.getLogger("daishin");


    @PostMapping("/upload")
    @ResponseBody
    public Map<String , String> uploadAndUnzip(@RequestParam(name = "File" , required = false) MultipartFile File , @ModelAttribute ReqParam req) throws IOException {

        //결과
        Map<String , String> response = new LinkedHashMap<>();

        //요청 정보 수신
        logger.info("request body : " +req);
        logger.info("pdf file name : "+File.getOriginalFilename());

        //null check
        if(reqCheck(req , logger)){
            response.put("필수값 누락" , "필수값 누락");
        }
        if(fileCheck(File , logger)){
            response.put("pdf 누락" , "pdf 누락");
        }
        if(!response.isEmpty()){
            return response;
        }
        //null check

        //중복체크
        req.setPK(req.getTR_KEY()+"_"+req.getRECV_NUM());
        if(reqInfoService.findReq(req) != null){
            logger.error("중복된 요청 : "+req);
            response.put("중복된 요청 : "+req , "중복된 요청 : "+req);
            return response;
        }
        //중복체크

        //파일 저장 (pdf)
        utils.savePdf(File , req , response , logger);

        //DB 저장
        if(reqSaveService.save(req) <= 0){
            logger.error("DB 저장 실패 : "+req);
        }

        //파일 저장 (json)
        utils.saveJson(File , req ,response ,logger);

        return response;

    }

    private boolean reqCheck(ReqParam req , Logger logger){
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

        if(errMsg.length()>=1){
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
