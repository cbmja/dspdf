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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Controller //우편 제작 요청 api
@RequiredArgsConstructor
@RequestMapping("/detail")
public class PostalCreationRequestController {

        private final DetailSaveService detailSaveService;
        private final DetailInfoService detailInfoService;

        private final Utils utils;

        private final MasterInfoService masterInfoService;
        private final MasterSaveService masterSaveService;

        private final Logger logger = LoggerFactory.getLogger("daishin");

    @PostMapping
    @ResponseBody
    public Map<String , String> detail(@ModelAttribute Detail detail){
        long startTime = System.nanoTime();

        //결과값
        Map<String , String> response = new LinkedHashMap<>();

        //요청 정보 log
        logger.info("request body : " +detail);

        //필수 항목 누락 체크
        List checkList = detailCheck(detail , logger);
        if((boolean)checkList.get(1)){
            response.put("필수값 누락" , (String)checkList.get(0));
            return response;
        }

        //중복 체크
        detail.setPK(detail.getTR_KEY()+"_"+detail.getRECV_NUM());
        if(detailInfoService.findReq(detail) != null){
            logger.error("중복된 요청 : "+detail);
            response.put("중복된 요청 : "+detail , "중복된 요청 : "+detail);
            return response;
        }

        //파일 저장 (pdf)
        utils.savePdf(detail , response , logger);

        //DB 저장(detail)
        //master_key 값 세팅
        if(detailSaveService.save(detail) <= 0){
            logger.error("DB 저장 실패 : "+detail);
        }

        //master 값 세팅
        String MASTER_KEY = detail.getMASTER();
        Master master = new Master(); //배치(대량)은 tr_key / 실시간(단일)은 날짜
        master.setMASTER_KEY(MASTER_KEY);
        master.setRECEIVED_TIME(detailInfoService.findReq(detail).getSAVE_DATE());

        //master 최초 저장
        if(masterInfoService.findMaster(MASTER_KEY) == null){
            if(!detail.getTOTAL_SEND_CNT().equals("1")){
                master.setTYPE("ARRANGEMENT"); //배치(대량)
                master.setTOTAL_SEND_CNT(detail.getTOTAL_SEND_CNT());
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
            _master.setRECEIVED_TIME(detailInfoService.findReq(detail).getSAVE_DATE());
            _master.setSEND_CNT(_master.getSEND_CNT()+1);
            masterSaveService.updateSendCnt(_master);
        }

        //배치(대량)그룹 전송 완료시 처리 / status 갱신 : 1(수신중) -> 2(수신완료) , JSON 파일저장
        if(!detail.getTOTAL_SEND_CNT().equals("1") && detailInfoService.countGroup(detail)==Integer.parseInt(detail.getTOTAL_SEND_CNT())){
            master.setSTATUS(2);
            masterSaveService.updateStatus(master);
            //JSON 파일 생성 및 저장
            utils.saveJson(detail.getMASTER()  , logger);
        }
        
        long endTime = System.nanoTime();
        logger.info("처리 시간 : "+(endTime - startTime));

        response.put("결과","OK");
        response.put("비고","SUCCESS");
        return response;

    }

    private List detailCheck(Detail detail , Logger logger){
        String errMsg = "";
        List list = new ArrayList<>();
        if(detail.getTR_KEY() == null || detail.getTR_KEY().isBlank()){
            errMsg += "TR_KEY , ";
        }
        if(detail.getDLV_TYPE_CD() == null || detail.getDLV_TYPE_CD().isBlank()){
            errMsg += "DLV_TYPE_CD , ";
        }
        if(detail.getPRINT_TYPE_NM() == null || detail.getPRINT_TYPE_NM().isBlank()){
            errMsg += "PRINT_TYPE_NM , ";
        }
        if(detail.getRECV_NUM() == null || detail.getRECV_NUM().isBlank()){
            errMsg += "RECV_NUM , ";
        }
        if(detail.getDM_LINK_KEY() == null || detail.getDM_LINK_KEY().isBlank()){
            errMsg += "DM_LINK_KEY , ";
        }
        if(detail.getPDF_NM() == null || detail.getPDF_NM().isBlank()){
            errMsg += "PDF_NM , ";
        }
        if(detail.getRECV_NM() == null || detail.getRECV_NM().isBlank()){
            errMsg += "RECV_NM , ";
        }
        if(detail.getRECV_POST_CD() == null || detail.getRECV_POST_CD().isBlank()){
            errMsg += "RECV_POST_CD , ";
        }
        if(detail.getRECV_ADDR() == null || detail.getRECV_ADDR().isBlank()){
            errMsg += "RECV_ADDR , ";
        }
        if(detail.getFile() == null || detail.getFile().isEmpty()){
            errMsg += "pdf file , ";
        }
        if(!errMsg.isEmpty()){
            errMsg = errMsg.substring(0, errMsg.length() - 2);
            logger.error(errMsg+"누락");
            list.add(errMsg);
            list.add(true);
            return list;
        }else {
            list.add("누락없음");
            list.add(false);
            return list;
        }
    }

/*

    private boolean fileCheck(MultipartFile File , Logger logger){
        if(File == null || File.isEmpty()){
            logger.error("pdf file 누락");
            return true;
        } else {
            return false;
        }
    }
*/



}
