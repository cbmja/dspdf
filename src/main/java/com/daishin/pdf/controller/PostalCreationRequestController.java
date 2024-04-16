package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.service.master.MasterSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller //우편 제작 요청 api
@RequiredArgsConstructor
public class PostalCreationRequestController {

        private final MasterSaveService masterSaveService;

    @PostMapping("/pcReq")
    @ResponseBody
    public Map<String, String> postalCreationRequest(Master master , Detail detail){

        Map<String , String > response = new LinkedHashMap<>();
        response.put("succ" , "성공");
        System.out.println(master);
        System.out.println("////////////");
        System.out.println(detail);
        masterSaveService.save(master);
        return response;
    }

}
