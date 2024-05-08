package com.daishin.pdf.util;

import com.daishin.pdf.dto.ReqParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public final class Common {



    /**
     * 파일 저장 일시 포맷
     * @return
     */
    public static String getDate() {
        SimpleDateFormat sdfyyyymm = new SimpleDateFormat("yyyyMM_HHmmss");
        Date now = new Date();
        return sdfyyyymm.format(now);
    }

    /**
     * pdf 저장
     * @param file
     * @param reqParam
     */
    public static void savePdf(MultipartFile file , ReqParam reqParam){



        //저장될 pdf 파일명
        String fileName = getDate()+"_"+file.getOriginalFilename();
        //파일 저장 경로
        String path = "C:\\\\DATA\\\\"+reqParam.getTR_KEY()+"\\";
    
        //디렉토리 생성
        File dir = new File(path);
        dir.mkdirs();
    
    
        //파일 저장처리SSS
        Path pdfPath = Paths.get(path).resolve(fileName);
        try {
            file.transferTo(pdfPath.toFile());
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    
    }

}
