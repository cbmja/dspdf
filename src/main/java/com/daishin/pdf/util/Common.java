package com.daishin.pdf.util;

import com.daishin.pdf.dto.ReqParam;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

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






    //////////////////////파일 이동 SSSSSSSSSS //////////////////////
    /**
     * path1 에 있는 파일들을 path2로 옮김
     * @param path1
     * @param path2
     */
    public static void moveFile(String path1 , String path2){
        Path sourceDir = Paths.get(path1);
        Path targetDir = Paths.get(path2);

        try {
            // 대상 디렉토리 존재 확인 및 생성
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
                System.out.println("대상 디렉토리 생성: " + targetDir);
            }

            // 원본 디렉토리에서 모든 파일을 반복하여 처리
            Files.list(sourceDir).forEach(sourcePath -> {
                try {
                    // 대상 디렉토리 경로 생성
                    Path targetPath = targetDir.resolve(sourcePath.getFileName());

                    // 파일 존재 확인
                    if (Files.exists(sourcePath)) {
                        // 파일 이동
                        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("파일 이동: " + sourcePath + " -> " + targetPath);
                    } else {
                        System.out.println("이동할 파일이 존재하지 않습니다: " + sourcePath);
                    }
                } catch (IOException e) {
                    System.err.println("파일 이동 중 에러 발생: " + sourcePath);
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.err.println("디렉토리 읽기 중 에러 발생");
            e.printStackTrace();
        }
    }
//////////////////////파일 이동 EEEEEEEEEE //////////////////////

    ////+++++++++++++++++++++//////////////+++++++++++++++++++++///////////////////////////+++++++++++++++++++++////////////////////
/*

    public static ReqParam mapToObj(Map req){
        ReqParam reqParam = new ReqParam();
        reqParam.setApiKey((String)req.get("apiKey"));
        reqParam.setTR_KEY((String)req.get("TR_KEY"));
        reqParam.setTOTAL_SEND_CNT((String)req.get("TOTAL_SEND_CNT"));
        reqParam.setDLV_TYPE_CD((String)req.get("DLV_TYPE_CD"));
        reqParam.setPRINT_TYPE_NM((String)req.get("PRINT_TYPE_NM"));
        reqParam.setPAGE_CNT((String)req.get("PAGE_CNT"));
        reqParam.setRET_YN((String)req.get("RET_YN"));
        reqParam.setRECV_NUM((String)req.get("RECV_NUM"));
        reqParam.setDM_LINK_KEY((String)req.get("DM_LINK_KEY"));
        reqParam.setPDF_NM((String)req.get("PDF_NM"));
        reqParam.setDLV_CD((String)req.get("DLV_CD"));
        reqParam.setRECV_NM((String)req.get("RECV_NM"));
        reqParam.setRECV_POST_CD((String)req.get("RECV_POST_CD"));
        reqParam.setRECV_ADDR((String)req.get("RECV_ADDR"));
        reqParam.setRECV_ADDR_DETAIL((String)req.get("RECV_ADDR_DETAIL"));

        return reqParam;
    }
*/

    ////+++++++++++++++++++++//////////////+++++++++++++++++++++///////////////////////////+++++++++++++++++++++////////////////////
}
