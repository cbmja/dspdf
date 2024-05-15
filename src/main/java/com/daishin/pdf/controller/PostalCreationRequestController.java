package com.daishin.pdf.controller;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.service.ReqSaveService;
import com.daishin.pdf.util.Common;
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


    @PostMapping("/upload")
    @ResponseBody
    public Map<String , String> uploadAndUnzip(@RequestParam(name = "File" , required = false) MultipartFile File , @ModelAttribute ReqParam req) throws IOException {
        //결과
        Map<String , String> response = new LinkedHashMap<>();

        //파일 저장 (pdf , json)
        Common.saveFile(File , req , response);
        
        //DB 저장
        if(reqSaveService.save(req) <= 0){
            response.put("error" , "DB 저장 실패");
        } else {
            response.put("result" , "success");
        }
        
        return response;

    }



}
