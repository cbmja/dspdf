package com.daishin.pdf.controller;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.service.ReqInfoService;
import com.daishin.pdf.service.ReqSaveService;
import com.daishin.pdf.util.Utils;
import lombok.RequiredArgsConstructor;
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


    @PostMapping("/upload")
    @ResponseBody
    public Map<String , String> uploadAndUnzip(@RequestParam(name = "File" , required = false) MultipartFile File , @ModelAttribute ReqParam req) throws IOException {
        //결과
        Map<String , String> response = new LinkedHashMap<>();

        //중복체크
        req.setPK(req.getTR_KEY()+"_"+req.getRECV_NUM());
        if(reqInfoService.findReq(req) != null){
            response.put("중복된 정보 ["+req.getTR_KEY()+"] 그룹의 ["+req.getRECV_NUM()+"] 번은 이미 저장 되었습니다."
                    , "중복된 정보 ["+req.getTR_KEY()+"] 그룹의 ["+req.getRECV_NUM()+"] 번은 이미 저장 되었습니다.");
            return response;
        }

        //파일 저장 (pdf)
        utils.savePdf(File , req , response);

        //DB 저장
        if(reqSaveService.save(req) <= 0){
            if(req.getTOTAL_SEND_CNT().equals("1")){
                response.put("DB 저장 실패(단일)" , "DB 저장 실패(단일)");
            }else {
                response.put("DB 저장 실패(대량)" , "DB 저장 실패(대량)");
            }
        }

        //파일 저장 (json)
        utils.saveJson(File , req ,response);

        return response;

    }



}
