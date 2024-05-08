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


    ///////////////

    @PostMapping("/upload")
    @ResponseBody
    public Map<String , String> uploadAndUnzip(@RequestParam(name = "File" , required = false) MultipartFile File , @ModelAttribute ReqParam req) throws IOException {
        //결과
        Map<String , String> response = new LinkedHashMap<>();

        //파일저장처리
        
        
        //db저장
        reqSaveService.save(req);



        response.put("결과" , "수신 완료 등등");
        return response;

    }

/////////////////////////////
    /*

        {
        "master" :{
        "TR_KEY":"111-111"
        ,"TOTAL_SEND_CNT":"3"
        ,"PDF_YN":"N"
        ,"DLV_TYPE_CD":"일반"
        ,"PRINT_TYPE_NM":"안내장"
        ,"PAGE_CNT":"78"
        ,"RET_YN":"N"},
        "detail":[
        {"RECV_NUM" : "1" , "DM_LINK_KEY" : "12-1"  , "DLV_CD" : "집중국코드^|집중국명^|배달국코드^|배달국명^|집배팅코드^|집배구코드^|집배인증용" , "RECV_NM" : "홍길동" , "RECV_POST_CD" : "122-2" , "RECV_ADDR" : "서울시 노원구333" , "RECV_ADDR_DETAIL" : "531-2903"}
        , {"RECV_NUM" : "2" , "DM_LINK_KEY" : "12-2"  , "DLV_CD" : "집중국코드^|집중국명^|배달국코드^|배달국명^|집배팅코드^|집배구코드^|집배인증용" , "RECV_NM" : "홍이동" , "RECV_POST_CD" : "102-42" , "RECV_ADDR" : "서울시 강남구444" , "RECV_ADDR_DETAIL" : "212-5303"}
        , {"RECV_NUM" : "3" , "DM_LINK_KEY" : "12-3"  , "DLV_CD" : "집중국코드^|집중국명^|배달국코드^|배달국명^|집배팅코드^|집배구코드^|집배인증용" , "RECV_NM" : "홍삼동" , "RECV_POST_CD" : "213-2" , "RECV_ADDR" : "인천 중구555" , "RECV_ADDR_DETAIL" : "203-5323"}
        ]
        }

    */



}
