package com.daishin.pdf.util;

import com.daishin.pdf.dto.ReqParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class Utils {


    /**
     * pdf 저장
     * @param file
     * @param reqParam
     */
    public void savePdf(MultipartFile file , ReqParam reqParam , Map<String , String> response){

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

        /////EEEjson 저장EEE/////
        reqParam.setPDF_PATH(path+fileName+".pdf");

    }

    public void saveJson(MultipartFile file , ReqParam reqParam , Map<String , String> response) throws IOException {
        String fileName = file.getOriginalFilename().substring(0 , file.getOriginalFilename().length()-4);
    /*
        //구분자
        String cate = fileName.substring(0,1);

        //파일 저장 경로
        String path = "C:\\DATA\\"+cate+"\\";
    */

        //파일 저장 경로 (구분자로 나눠야 하면 위에 걸로)
        String path = "C:\\DATA\\";

        /////SSSjson 저장SSS/////

        //단일
        if(reqParam.getTOTAL_SEND_CNT().equals("1")){
            ObjectMapper mapper = new ObjectMapper();

            //reqParam(발송정보 json으로 변환)
            String json = mapper.writeValueAsString(reqParam);

            try {
                FileWriter fileWriter = new FileWriter(path+fileName+".json");
                fileWriter.write(json);
                fileWriter.close();
            } catch (IOException e) {
                response.put("error" , "json 저장 실패");
                e.printStackTrace();
            }
        } else {
            //대량 tr_key 로 count 했을 때 갯수가 total_send_cnt 와 동일 하고
            //해당 tr_key 를 가진 것들 중 recv_num 이 total_send_cnt 와 동일한 로우가 있을 경우 모두 전송된 것으로 간주


        }
    }

}
