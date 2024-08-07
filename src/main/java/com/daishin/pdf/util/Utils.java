package com.daishin.pdf.util;

import com.daishin.pdf.dto.Error;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.log.LogCode;
import com.daishin.pdf.repository.ErrorRepository;
import com.daishin.pdf.response.ResponseCode;
import com.daishin.pdf.service.DetailSaveService;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.DetailInfoService;
import com.daishin.pdf.service.MasterSaveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Utils {

    private final DetailInfoService detailInfoService;
    private final MasterInfoService masterInfoService;

    private final DetailSaveService detailSaveService;

    private final ErrorRepository errorRepository;

    private final MasterSaveService masterSaveService;


    /**
     * pdf 저장
     * @param detail
     */
    public boolean savePdf(Detail detail, Logger logger){ //////////////////////////////////////OK

        boolean result = true;
        /////SSSpdf 저장SSS/////
        MultipartFile file = detail.getFile();
        String fileName = file.getOriginalFilename();
        
        //저장경로 (대량이면 dir 하나 더 생성)
        String path = detail.getPDF_PATH();

        //디렉토리 생성
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }

        //파일 저장처리SSS
        Path pdfPath = Paths.get(path).resolve(fileName);
        try {
            file.transferTo(pdfPath.toFile());
        } catch (Exception e) {
            logger.error(LogCode.PDF_ERROR+" : "+detail); //
            e.printStackTrace();
            Error error = new Error();
            error.setERROR_MESSAGE(e.getMessage()+"\n param : "+detail);
            error.setERROR_CODE(LogCode.PDF_ERROR);
            errorRepository.save(error);
            result = false;
        }
        return result;
    }

    ///////////////////////

    //json 저장
    public boolean saveJson(String master ,  Logger logger , String receivingPath , String completePath) {


        //저장경로 
        String path = receivingPath+master+"\\";

        Map<String , List> jsonList = new HashMap<>();

        List<Master> masterList = new ArrayList<>();
        Master findMaster = masterInfoService.findMaster(master);
        if(findMaster != null && findMaster.getError().equals(ResponseCode.SQL_ERROR)){
            return false;
        }

        masterList.add(findMaster);
        jsonList.put("master" , masterList);


        List<Detail> detailList = detailInfoService.getMasterGroup(master);
        if(detailList.isEmpty()){
            return false;
        }

        List<Detail> details = new ArrayList<>();
        for(Detail d : detailList){
            d.setPDF_PATH(completePath+d.getMASTER());
            details.add(d);
        }


        jsonList.put("detail" , details);


        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String jsonlist = mapper.writeValueAsString(jsonList);
            FileWriter fileWriter = new FileWriter(path+master+".json");
            fileWriter.write(jsonlist);
            fileWriter.close();
        } catch (Exception e) {
            logger.error(LogCode.JSON_ERROR+" : "+ master); //
            e.printStackTrace();
            Error error = new Error();
            error.setMASTER_KEY(master);
            error.setERROR_MESSAGE(e.getMessage()+"\n param : "+master);
            error.setERROR_CODE(LogCode.JSON_ERROR);
            errorRepository.save(error);
            return false;
        }


        return true;
        }


        //수신 완료 되면 receiving 에서 complete 폴더로 이동
        public int moveDir(String masterKey , Logger logger, String receivingPath , String completePath){

                int result = 1;
                Path sourceDir = Paths.get(receivingPath+masterKey);
                Path targetDir = Paths.get(completePath+masterKey);

                try {
                    //이동시킬 폴더 생성
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }

                    DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir);
                    for (Path entry : stream) {
                        Path targetPath = targetDir.resolve(entry.getFileName());
                        Files.move(entry, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    stream.close();
                    Files.delete(sourceDir);
                } catch (IOException e) {
                    logger.error(LogCode.FILE_MOVE_ERROR+" : "+ masterKey); //
                    e.printStackTrace();
                    Error error = new Error();
                    error.setMASTER_KEY(masterKey);
                    error.setERROR_MESSAGE(e.getMessage()+"\n param : "+masterKey);
                    error.setERROR_CODE(LogCode.FILE_MOVE_ERROR);
                    errorRepository.save(error);
                    result = -1;
                }

                List<Detail> details = detailInfoService.getMasterGroup(masterKey);

                if(details.isEmpty()){
                    result = 0;
                    return result;
                }

                for(Detail detail : details){
                    detail.setPDF_PATH(completePath+masterKey);
                    if(detailSaveService.updatePdfPath(detail) < 0){
                        result = 0;
                        return result;
                    }
                }

            return result;
        }



    public void checkDirectoryExists(String completePath) {

        List<Master> masters = masterInfoService.selectByStatus(200);
        if(masters.isEmpty()){
            return;
        }

        for(Master master : masters){
            Path directoryPath = Paths.get(completePath+master.getMASTER_KEY());
            if(!Files.exists(directoryPath)) {
                //존재하지 않는 경우 (이동시킨 경우) 200-> 300
                master.setSTATUS(300);
                masterSaveService.updateStatus(master);
            }
        }
    }


    public void deletePdf(String pdfPath , Logger logger){

        File file = new File(pdfPath);

        try{
            file.delete();
        }catch(Exception e){
            logger.error(LogCode.PDF_ERROR); //
            e.printStackTrace();
            Error error = new Error();
            error.setERROR_MESSAGE(e.getMessage());
            error.setERROR_CODE(LogCode.PDF_ERROR);
            errorRepository.save(error);
        }
    }

}


