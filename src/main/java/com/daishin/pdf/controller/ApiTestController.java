package com.daishin.pdf.controller;

import com.daishin.pdf.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller //테스트
@RequiredArgsConstructor
public class ApiTestController {

    private final TestRepository testRepository;

    @PostMapping("/apiTest")
    @ResponseBody
    public Map<String, String> test(String test){

        Map<String , String > response = new LinkedHashMap<>();
        response.put("succ" , "성공");
        System.out.println(test);
        testRepository.insertTest(test);
        return response;
    }



}
