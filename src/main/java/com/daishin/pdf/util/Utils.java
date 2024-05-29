package com.daishin.pdf.util;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.ReqInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Utils {

    private final ReqInfoService reqInfoService;
    private final MasterInfoService masterInfoService;


    /**
     * pdf 저장
     * @param file
     * @param reqParam
     */
    public void savePdf(MultipartFile file , ReqParam reqParam , Map<String , String> response , Logger logger){

        /////SSSpdf 저장SSS/////

        //String fileName = file.getOriginalFilename().substring(0 , file.getOriginalFilename().length()-4);
        String fileName = file.getOriginalFilename();
        
        //저장경로 (대량이면 dir 하나 더 생성)
        String path ;

        //단일
        if(reqParam.getTOTAL_SEND_CNT().equals("1")){

            LocalTime currentTime = LocalTime.now();

            // 기준 시간 설정
            LocalTime comparisonTime = LocalTime.of(17, 51);

            //14 이전이면 년도-월-오늘날짜
            if (currentTime.isBefore(comparisonTime)) {
                path = "C:\\DATA\\"+ LocalDate.now()+"\\";
            } else {
            //14 이후이면 년도-월-내일날짜    
                path = "C:\\DATA\\"+LocalDate.now().plusDays(1L)+"\\";
            }
        } else {
        //대량
            path = "C:\\DATA\\"+reqParam.getTR_KEY()+"\\";
        }


        //디렉토리 생성
        File dir = new File(path);
        dir.mkdirs();

        //파일 저장처리SSS
        Path pdfPath = Paths.get(path).resolve(fileName);
        try {
            file.transferTo(pdfPath.toFile());
        } catch (IllegalStateException | IOException e) {
            logger.error("pdf 저장 실패 : "+file.getOriginalFilename()+" / "+reqParam);
            e.printStackTrace();
        }
        /////EEEpdf 저장EEE/////
        reqParam.setPDF_PATH(path+fileName);
    }

    ///////////////////////

    //대량 (배치) json 저장
    public void saveJson(String master ,  Logger logger) throws IOException {

        //저장경로 
        String path = "C:\\DATA\\"+master+"\\";

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        //response.put("["+reqParam.getTR_KEY()+"] 전송 완료" , "["+reqParam.getTR_KEY()+"] 전송 완료");

        Map<String , List> jsonList = new HashMap<>();

        List<Master> masterList = new ArrayList<>();
        masterList.add(masterInfoService.findMaster(master));
        jsonList.put("master" , masterList);
        jsonList.put("detail" , reqInfoService.getMasterGroup(master));

        String jsonlist = mapper.writeValueAsString(jsonList);
        try {
            FileWriter fileWriter = new FileWriter(path+master+".json");
            fileWriter.write(jsonlist);
            fileWriter.close();
        } catch (IOException e) {
            logger.error("json 저장 실패 master : ["+master+" ]");

            e.printStackTrace();
        }

        }




////////////////////////////////////////////////////////
/*
    public void saveJson(MultipartFile file , ReqParam reqParam , Map<String , String> response , Logger logger) throws IOException {

        //확장자 제외 파일명
        String fileName = file.getOriginalFilename().substring(0 , file.getOriginalFilename().length()-4);

        //저장경로 (대량이면 dir 하나 더 생성)
        String path = "";

        //단일
        if(reqParam.getTOTAL_SEND_CNT().equals("1")){

            LocalTime currentTime = LocalTime.now();

            // 기준 시간 설정
            LocalTime comparisonTime = LocalTime.of(14, 0);

            //당일 전송건도 시간에 따라 TR_KEY가 동일하게 전송된다면 아래 수정 필요
            if (currentTime.isBefore(comparisonTime)) {
                path = "C:\\DATA\\"+ LocalDate.now()+"\\";
            } else {
                path = "C:\\DATA\\"+LocalDate.now().plusDays(1L)+"\\";
            }
        }

        //대량
        if(!reqParam.getTOTAL_SEND_CNT().equals("1")){
            path = "C:\\DATA\\"+reqParam.getTR_KEY()+"\\";
        }


        ObjectMapper mapper = new ObjectMapper();

        //SSSSSSSSSSSSSSSS단일SSSSSSSSSSSSSSSS
        if(reqParam.getTOTAL_SEND_CNT().equals("1")){

            //reqParam(발송정보 json으로 변환)
            String json = mapper.writeValueAsString(reqParam);

            try {
                FileWriter fileWriter = new FileWriter(path+fileName+".json");
                fileWriter.write(json);
                fileWriter.close();
            } catch (IOException e) {
                logger.error("json 저장 실패 : "+reqParam);
                response.put("json 저장 실패" , "json 저장 실패");
                e.printStackTrace();
            }
        }
        //EEEEEEEEEEEEEEEE단일EEEEEEEEEEEEEEEE
        //SSSSSSSSSSSSSSSS대량SSSSSSSSSSSSSSSS
        else {
             // 대량일 경우 3가지 조건을 만족할 경우 json 저장
             // 1- TOTAL_SEND_CNT != 1   //총 전송 건수가 1이 아닌 경우(위의 if 문에서 체크)
             // X 2- RECV_NUM == TOTAL_SEND_CNT   //그룹 내 순번이 그룹 총 전송 건수와 같은 경우(마지막 전송건일 경우)
             // 3- TR_KEY 로 select 했을 때 총 갯수가 TOTAL_SEND_CNT 와 동일할 경우
             // (select count(*) from dbo.REQ where TR_KEY = #{TR_KEY})

            String totalTrGroup = reqParam.getTOTAL_SEND_CNT();
            //순서대로 전송되는지 여부 마지막 순번(recv_num)이 마지막에 전송되어야 하단 조건 통과함
            if(reqInfoService.countGroup(reqParam)==Integer.parseInt(totalTrGroup)){

                response.put("["+reqParam.getTR_KEY()+"] 그룹 전송 완료" , "["+reqParam.getTR_KEY()+"] 그룹 전송 완료");

                List<ReqParam> jsonList = reqInfoService.getTrGroup(reqParam);

                String jsonlist = mapper.writeValueAsString(jsonList);
                try {
                    FileWriter fileWriter = new FileWriter(path+reqParam.getTR_KEY()+".json");
                    fileWriter.write(jsonlist);
                    fileWriter.close();
                } catch (IOException e) {
                    logger.error("json 저장 실패 : "+reqParam);
                    response.put("json 저장 실패" , "json 저장 실패");
                    e.printStackTrace();
                }
            }
        }
        //EEEEEEEEEEEEEEEE대량EEEEEEEEEEEEEEEE
    }
*/
}


