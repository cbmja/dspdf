package com.daishin.pdf.util;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Common {

    public static String getCurrnetYYYYMM() {
        SimpleDateFormat sdfyyyymm = new SimpleDateFormat("yyyyMM");
        Date now = new Date();
        return sdfyyyymm.format(now);
    }

    public static Map txtToEntity(MultipartFile pstFile){

        // txt -> json 파싱
        try {
            // MultipartFile을 읽어서 Map 객체로 변환
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(pstFile.getInputStream(), Map.class);

            // json -> map
            List<Map> details = (List)map.get("detail");
            List<Detail> detailList = mapToDetailList(details);

            //map -> entity
            Master master = mapToMaster((Map)map.get("master"));

            Map entity = new HashMap<>();
            entity.put("master" , master);
            entity.put("detailList" , detailList);
            return entity;

        } catch (IOException e) {
            e.printStackTrace();
        }

       return null;
    }

    public static Master mapToMaster(Map map){

        Master master = new Master();
        master.setTrKey((String)map.get("TR_KEY"));
        master.setTotalSendCnt((String)map.get("TOTAL_SEND_CNT"));
        master.setDlvSttusCd((String)map.get("DLV_STTUS_CD"));
        master.setPrintTypeNm((String)map.get("PRINT_TYPE_NM"));
        master.setPageCnt((String)map.get("PAGE_CNT"));
        master.setRetYn((String)map.get("RET_YN"));
        master.setPdfYn((String)map.get("PDF_YN"));

        return master;
    }

    public static List<Detail> mapToDetailList(List<Map> list){

        List<Map> details = list;

        List<Detail> detailList = new ArrayList<>();

        for(Map map : details){
            Detail detail = new Detail();

            detail.setTrKey((String)map.get("TR_KEY"));
            detail.setRecvNum((String)map.get("RECV_NUM"));
            detail.setDmLinkKey((String)map.get("DM_LINK_KEY"));
            detail.setDlvCd((String)map.get("DLV_CD"));
            detail.setRecvNm((String)map.get("RECV_NM"));
            detail.setRecvPostCd((String)map.get("RECV_POST_CD"));
            detail.setRecvAddr((String)map.get("RECV_ADDR"));
            detail.setRecvAddrDetail((String)map.get("RECV_ADDR_DETAIL"));

            detailList.add(detail);
        }

        return detailList;
    }


}
