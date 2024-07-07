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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Utils {

    private final DetailInfoService detailInfoService;
    private final MasterInfoService masterInfoService;

    private final DetailSaveService detailSaveService;

    private final ErrorRepository errorRepository;


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
            errorRepository.save(error);
            result = false;
        }
        return result;
    }

    ///////////////////////

    //json 저장
    public boolean saveJson(String master ,  Logger logger) {


        //저장경로 
        String path = "C:\\DATA\\receiving\\"+master+"\\";

        Map<String , List> jsonList = new HashMap<>();

        List<Master> masterList = new ArrayList<>();
        Master findMaster = masterInfoService.findMaster(master);
        if(findMaster != null && findMaster.getError().equals(ResponseCode.SQL_ERROR)){
            return false;
        }

        masterList.add(findMaster);
        jsonList.put("master" , masterList);


        List<Detail> detailList = detailInfoService.getMasterGroup(master).stream().map(d ->{
            d.setPDF_PATH("C:\\DATA\\complete\\"+d.getMASTER());
            return d;
        }).toList();

        if(detailList.isEmpty()){
            return false;
        }
        jsonList.put("detail" , detailList);


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
            errorRepository.save(error);
            return false;
        }
        
        //폴더 이동
        moveDir(master,logger);

        return true;
        }


        public int moveDir(String masterKey , Logger logger){

                Path sourceDir = Paths.get("C:\\DATA\\receiving\\"+masterKey);
                Path targetDir = Paths.get("C:\\DATA\\complete\\"+masterKey);

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
                    errorRepository.save(error);
                }

                List<Detail> details = detailInfoService.getMasterGroup(masterKey);

                for(Detail detail : details){
                    detail.setPDF_PATH("C:\\DATA\\complete\\"+masterKey);

                    detailSaveService.updatePdfPath(detail);
                }



            return 0;
        }

}


