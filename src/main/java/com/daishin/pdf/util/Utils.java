package com.daishin.pdf.util;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.log.LogCode;
import com.daishin.pdf.response.ResponseCode;
import com.daishin.pdf.response.ResponseMessage;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.DetailInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Utils {

    private final DetailInfoService detailInfoService;
    private final MasterInfoService masterInfoService;


    /**
     * pdf 저장
     * @param detail
     */
    public void savePdf(Detail detail, Map<String , String> response , Logger logger){

        /////SSSpdf 저장SSS/////
        MultipartFile file = detail.getFile();
        String fileName = file.getOriginalFilename();
        
        //저장경로 (대량이면 dir 하나 더 생성)
        String path = detail.getPDF_PATH();

        //디렉토리 생성
        File dir = new File(path);
        dir.mkdirs();

        //파일 저장처리SSS
        Path pdfPath = Paths.get(path).resolve(fileName);
        try {
            file.transferTo(pdfPath.toFile());
        } catch (Exception e) {
            logger.error(LogCode.PDF_ERROR+" : "+file.getOriginalFilename()); //
            response.put(LogCode.RESULT, LogCode.ERROR);
            response.put(LogCode.REMARK, "파일 저장 실패: "+e.getMessage());
            e.printStackTrace();
        }
    }

    ///////////////////////

    //json 저장
    public void saveJson(String master ,  Logger logger) {

        //저장경로 
        String path = "C:\\DATA\\"+master+"\\";

        Map<String , List> jsonList = new HashMap<>();

        List<Master> masterList = new ArrayList<>();
        masterList.add(masterInfoService.findMaster(master));
        jsonList.put("master" , masterList);
        jsonList.put("detail" , detailInfoService.getMasterGroup(master));

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String jsonlist = mapper.writeValueAsString(jsonList);
            FileWriter fileWriter = new FileWriter(path+master+".json");
            fileWriter.write(jsonlist);
            fileWriter.close();
        } catch (Exception e) {
            //logger.error(LogCode.JSON_SAVE_FAIL+" : "+masterInfoService.findMaster(master));
            logger.error(LogCode.JSON_ERROR+" : "+ master); //
            e.printStackTrace();
        }

        }

}


