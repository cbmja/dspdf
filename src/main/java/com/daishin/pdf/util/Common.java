package com.daishin.pdf.util;

import com.daishin.pdf.dto.ReqParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


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
    public static void savePdf(MultipartFile file , ReqParam reqParam , Map<String , String> response) throws IOException {

        /////SSSpdf 저장SSS/////
        //저장될 pdf 파일명 (확장자 제외)
        String fileName = file.getOriginalFilename().substring(0 , file.getOriginalFilename().length()-4);
    /*
        //구분자
        String cate = fileName.substring(0,1);

        //파일 저장 경로
        String path = "C:\\DATA\\"+cate+"\\";
    */

        //파일 저장 경로 (구분자로 나눠야 하면 위에 걸로)
        String path = "C:\\DATA\\";

        //디렉토리 생성
        File dir = new File(path);
        dir.mkdirs();

        //파일 저장처리SSS
        Path pdfPath = Paths.get(path).resolve(fileName+".pdf");
        try {
            file.transferTo(pdfPath.toFile());
            //response.put("결과" , "pdf저장 성공");
        } catch (IllegalStateException | IOException e) {
            response.put("error" , "pdf 저장 실패");
            e.printStackTrace();
        }
        /////EEEpdf 저장EEE/////

        /////SSSjson 저장SSS/////
        ObjectMapper mapper = new ObjectMapper();

        //reqParam(발송정보 json으로 변환)
        String jsonInString = mapper.writeValueAsString(reqParam);

        try {
            FileWriter fileWriter = new FileWriter(path+fileName+".json");
            fileWriter.write(jsonInString);
            fileWriter.close();
        } catch (IOException e) {
            response.put("error" , "json 저장 실패");
            e.printStackTrace();
        }
        /////EEEjson 저장EEE/////
    }

}
