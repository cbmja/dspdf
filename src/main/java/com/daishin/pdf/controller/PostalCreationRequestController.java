package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Error;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.log.LogCode;
import com.daishin.pdf.repository.ErrorRepository;
import com.daishin.pdf.response.ResponseCode;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import com.daishin.pdf.service.DetailInfoService;
import com.daishin.pdf.service.DetailSaveService;
import com.daishin.pdf.util.Utils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Controller //우편 제작 요청 api
@RequiredArgsConstructor
@RequestMapping("/detail")
public class PostalCreationRequestController {

        private final ErrorRepository errorRepository;

        private final DetailSaveService detailSaveService;
        private final DetailInfoService detailInfoService;

        private final Utils utils;

        private final MasterInfoService masterInfoService;
        private final MasterSaveService masterSaveService;

        private final Logger logger = LoggerFactory.getLogger("daishin");

    @PostMapping
    @ResponseBody
    public Map<String , String> detail(@ModelAttribute Detail _detail){

        //////////////////////////////////////OK        //////////////////////////////////////OK
        long startTime = System.nanoTime();
        //결과값
        Map<String , String> response = new LinkedHashMap<>();

        //요청 정보 log
        logger.info(LogCode.DETAIL_REQUEST +" : " +_detail);

        //detail 값 세팅 : PDF_PATH , MASTER , PK
        Detail detail = _detail.detailSetting(_detail);

        //필수 항목 누락 체크
        List checkList = detailCheck(detail);
        if((boolean)checkList.get(1)){
            response.put(ResponseCode.RESULT, ResponseCode.ERROR);
            response.put(ResponseCode.REMARK, ResponseCode.MISSING_VALUE+(String)(checkList.get(0)));
            Error error = new Error();
            error.setERROR_MESSAGE(ResponseCode.MISSING_VALUE+(String)(checkList.get(0)));
            errorRepository.save(error);
            return response;
        }


        //중복 체크
        Detail existDetail = detailInfoService.findDetail(detail);
        if(existDetail != null){
            //중복 값이 있는 경우
            if(existDetail.getError().isBlank()){
                response.put(ResponseCode.RESULT, ResponseCode.ERROR);
                response.put(ResponseCode.REMARK, ResponseCode.DUPLICATE_VALUE+"TR_KEY ["+detail.getTR_KEY()+" ] / RECV_NUM [ "+detail.getRECV_NUM()+" ]");
                Error error = new Error();
                error.setERROR_MESSAGE(ResponseCode.DUPLICATE_VALUE+"TR_KEY ["+detail.getTR_KEY()+" ] / RECV_NUM [ "+detail.getRECV_NUM()+" ]");
                errorRepository.save(error);
                return response;
            }else if(existDetail.getError().equals(ResponseCode.SQL_ERROR)){
            //Sql Exception
                response.put(ResponseCode.RESULT, ResponseCode.ERROR);
                response.put(ResponseCode.REMARK, ResponseCode.SQL_ERROR);
                return response;
            }
        }


        //중복되는 파일명 있을경우 덮어쓰기 됨
        //파일 저장 (pdf) //
        if(!utils.savePdf(detail , logger)){
            response.put(ResponseCode.RESULT, ResponseCode.ERROR);
            response.put(ResponseCode.REMARK, ResponseCode.FILE_ERROR);
            return response;
        }


        //DB 저장(detail)
        if(detailSaveService.save(detail) <= 0){
            response.put(ResponseCode.RESULT , ResponseCode.ERROR);
            response.put(ResponseCode.REMARK , ResponseCode.SQL_ERROR);
            return response;
        }


        //DB에서 detail select -> master에 들어갈 데이터 수집
        Detail findDetail = detailInfoService.findDetail(detail);
        if(findDetail != null && findDetail.getError().equals(ResponseCode.SQL_ERROR)){
            response.put(ResponseCode.RESULT, ResponseCode.ERROR);
            response.put(ResponseCode.REMARK, ResponseCode.SQL_ERROR);
            return response;
        }
        LocalDateTime detailSavedTime = findDetail.getSAVE_DATE();
        //master 값 세팅
        String MASTER_KEY = detail.getMASTER();
        Master master = new Master(); //배치(대량)은 tr_key / 실시간(단일)은 날짜
        master.setMASTER_KEY(MASTER_KEY);
        master.setRECEIVED_TIME(detailSavedTime);


        //master_key 로 select 해서 존재 하면 update 존재하지 않으면 새로 저장
        Master findMaster = masterInfoService.findMaster(MASTER_KEY);
        if(findMaster != null && findMaster.getError().equals(ResponseCode.SQL_ERROR)){
            response.put(ResponseCode.RESULT, ResponseCode.ERROR);
            response.put(ResponseCode.REMARK, ResponseCode.SQL_ERROR);
            return response;
        }


        //첫 저장
        if(findMaster == null){
            if(!detail.getTOTAL_SEND_CNT().equals("1")){
                master.setTYPE("ARRANGEMENT"); //배치(대량)
                master.setTOTAL_SEND_CNT(detail.getTOTAL_SEND_CNT());
            }else{
                master.setTOTAL_SEND_CNT("수신중");
                master.setTYPE("REAL_TIME"); //실시간(단일)
            }
            master.setSEND_CNT(1);
            master.setSTATUS(1);
            int masterSaveResult = masterSaveService.save(master);
            //저장 실패시
            if(masterSaveResult <= 0){
                response.put(ResponseCode.RESULT, ResponseCode.ERROR);
                response.put(ResponseCode.REMARK, ResponseCode.SQL_ERROR);
                return response;
            }
        }else{
        //전송 건수 갱신
            findMaster.setRECEIVED_TIME(detailSavedTime);
            findMaster.setSEND_CNT(findMaster.getSEND_CNT()+1);
            int masterUpdateResult = masterSaveService.updateSendCnt(findMaster);
            //업데이트 실패
            if(masterUpdateResult <= 0){
                response.put(ResponseCode.RESULT, ResponseCode.ERROR);
                response.put(ResponseCode.REMARK, ResponseCode.SQL_ERROR);
                return response;
            }
        }


        //배치(대량)그룹 전송 완료 처리 / status 갱신 : 1(수신중) -> 2(수신완료) , JSON 파일저장
        int detailGroupCnt = detailInfoService.countGroup(detail);
        if(detailGroupCnt <= 0){
            response.put(ResponseCode.RESULT, ResponseCode.ERROR);
            response.put(ResponseCode.REMARK, ResponseCode.SQL_ERROR);
            return response;
        }


        //현재 수신한 detail이, 속한 그룹의 마지막 건 인지 체크
        if(!detail.getTOTAL_SEND_CNT().equals("1") && detailGroupCnt==Integer.parseInt(detail.getTOTAL_SEND_CNT())){
            master.setSTATUS(2);
            int masterUpdateResult = masterSaveService.updateStatus(master);
            //업데이트 실패
            if(masterUpdateResult <=0){
                response.put(ResponseCode.RESULT, ResponseCode.ERROR);
                response.put(ResponseCode.REMARK, ResponseCode.SQL_ERROR);
                return response;
            }

            //JSON 파일 생성 및 저장
            if(!utils.saveJson(detail.getMASTER()  , logger)){
                response.put(ResponseCode.RESULT, ResponseCode.ERROR);
                response.put(ResponseCode.REMARK, ResponseCode.JSON_ERROR);
                return response;
            }
        }


        long endTime = System.nanoTime();
        logger.info(LogCode.WORK_TIME+" : "+(endTime - startTime)); //
        response.put(ResponseCode.RESULT,ResponseCode.OK);
        response.put(ResponseCode.REMARK,ResponseCode.SUCCESS);
        return response;

    }

    private List detailCheck(Detail detail){
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
            logger.error(LogCode.MISSING_VALUE+" : "+errMsg);
            errMsg = errMsg.substring(0, errMsg.length() - 3);
            list.add(errMsg);
            list.add(true);
            return list;
        }else {
            list.add("누락없음");
            list.add(false);
            return list;
        }
    }

}
