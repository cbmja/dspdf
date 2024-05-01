package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.service.detail.DetailSaveService;
import com.daishin.pdf.service.master.MasterInfoService;
import com.daishin.pdf.service.master.MasterSaveService;
import com.daishin.pdf.util.Common;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Controller //우편 제작 요청 api
@RequiredArgsConstructor
public class PostalCreationRequestController {

        private final MasterSaveService masterSaveService;
        private final MasterInfoService masterInfoService;

        private final DetailSaveService detailSaveService;

/*
    @PostMapping("/txt")
    @ResponseBody
    public void test(@RequestParam(name="apiKey",required = false) String apiKey , @RequestParam(name="pstFile",required = false) MultipartFile pstFile
                                                                   , @RequestParam(name="File",required = false) List<MultipartFile> File ) throws IOException {

        // txt 파일에 있는 json entity 객체로 변환
       // Map entity = Common.txtToEntity(pstFile);

        Master master = (Master)entity.get("master");
        master.setApiKey(apiKey);

        List<Detail> detailList = (List<Detail>)entity.get("detailList");

        masterSaveService.save(master);

        for(Detail d : detailList){
            d.setTrKey(master.getTrKey());
            detailSaveService.save(d);
        }
        
    }
*/
    ///////////////

    @PostMapping("/upload")
    @ResponseBody
    public Map<String , String> uploadAndUnzip(@RequestParam("file") MultipartFile file , @RequestParam(name="apiKey",required = false) String apiKey) throws IOException {

        Map<String , String> response = new TreeMap<>();

        //압축 해제 후 저장
        Common.unZipAndSave(file , "C:\\DATA\\zip");

        Map entity = Common.txtToEntity("C:\\DATA\\zip\\json.json");

        Master master = (Master)entity.get("master");
        master.setApiKey(apiKey);

        List<Detail> detailList = (List<Detail>)entity.get("detailList");

        masterSaveService.save(master);

        for(Detail d : detailList){
            d.setTrKey(master.getTrKey());
            detailSaveService.save(d);
        }

        response.put("결과" , "???");
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
public Map<String, Object> parseJsonToMap(String jsonFilePath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map = mapper.readValue(new File(jsonFilePath), Map.class);
    return map;
}


}
