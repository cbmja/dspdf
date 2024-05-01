package com.daishin.pdf.util;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Common {

    /**
     * 파일 저장 일시 포맷
     * @return
     */
    public static String getCurrnetYYYYMM() {
        SimpleDateFormat sdfyyyymm = new SimpleDateFormat("yyyyMM");
        Date now = new Date();
        return sdfyyyymm.format(now);
    }

    /**
     * multipartFile을 entity 객체로 변환
     * @param pstFile
     * @return Master , List<Detail> 들어있는 map
     */
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
        master.setDlvTypeCd((String)map.get("DLV_TYPE_CD"));
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

    //////////////////////파일 압축 해제 후 저장 SSSSSSSSSS //////////////////////

    public static void unZipAndSave(MultipartFile file , String path){
        // 임시 디렉토리 생성 및 파일 저장
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    // 파일이 있는 경로 추출
                    File newFile = newFile(new File(path), zipEntry);
                    // 디렉토리가 없으면 생성
                    if (newFile.getParentFile() != null && !newFile.getParentFile().exists()) {
                        newFile.getParentFile().mkdirs();
                    }
                    // 파일 쓰기
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        IOUtils.copy(zipInputStream, fos);
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            return ;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

//////////////////////파일 압축 해제 후 저장 EEEEEEEEEE //////////////////////
}
