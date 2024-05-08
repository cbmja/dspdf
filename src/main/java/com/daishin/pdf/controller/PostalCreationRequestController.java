package com.daishin.pdf.controller;

import com.daishin.pdf.service.ReqSaveService;
import com.daishin.pdf.service.detail.DetailSaveService;
import com.daishin.pdf.service.master.MasterInfoService;
import com.daishin.pdf.service.master.MasterSaveService;
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
    public Map<String , String> uploadAndUnzip(@RequestParam(name = "File" , required = false) MultipartFile File , @RequestParam Map<String,String> req) throws IOException {


        //결과
        Map<String , String> response = new LinkedHashMap<>();

        System.out.println(File+"+++++++++++++++++++++++");
        System.out.println(req+"////////////////////////");
        System.out.println(Common.mapToObj(req)+"---------------");

        reqSaveService.save(Common.mapToObj(req));

        /*
        //압축 파일 저장 경로 (저장할 파일명에 변경이 필요 하다면 조금 복잡해 질듯. 일단 압축을 풀고 읽은 뒤에 trKey를 꺼내서 다시 저장 해줘야 하므로)
        String path = "C:\\DATA\\zip"; //임시저장 압축해제 및 파싱
        String path2 = "C:\\DATA\\save"; //실제 저장 경로

        //압축 해제 후 저장
        Common.unZipAndSave(pstFile , path);

        //저장된 .json파일명
        String jsonFileName = Common.findJsonFiles(path).get(0);

        //압축 해제 후 json 파일을 Dto 객체로 파싱
        Map entity = Common.txtToEntity(path+"\\"+jsonFileName);
        
        //파일 이동 / 이동해서 저장할대 파일명 , 경로는 아직 미정
        Common.moveFile(path , path2);
        

        //Dto객체 생성
        Master master = (Master)entity.get("master");
        master.setApiKey(apiKey);
        List<Detail> detailList = (List<Detail>)entity.get("detailList");

        //DB저장
        masterSaveService.save(master);

        for(Detail d : detailList){
            d.setTrKey(master.getTrKey());
            detailSaveService.save(d);
        }
        
        //path 경로에 있는 모든 파일 삭제 / 이동시켰으니 따로 삭제할 필요는 없을듯 
        //json파일 삭제 (pdf도 삭제해야함)
        //Common.deleteJsonFiles(path);
        */

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
