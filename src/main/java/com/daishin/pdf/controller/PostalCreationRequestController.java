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

        //파일저장처리
        Common.savePdf(File , req);
        
        //db저장
        reqSaveService.save(req);

        response.put("결과" , "수신 완료 등등");
        return response;

    }



}
