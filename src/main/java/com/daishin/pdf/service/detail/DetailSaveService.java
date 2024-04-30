package com.daishin.pdf.service.detail;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.repository.DetailRepository;
import com.daishin.pdf.util.Common;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DetailSaveService {

    private final DetailRepository detailRepository;

    public void save(Detail detail){
/*
        if(detail.getPdfYn().equals("Y")){
            //파일저장
            savePdfFile(detail);
        }
        */
        detailRepository.save(detail);
    }


/*

    //base64 인코딩된 파일 데이터를 디코딩하여 파일로 변환 후 저장
    //pdf 로 오는 경우 (pdfYn -> Y 인 경우)
    private void savePdfFile(Detail detail){

        MultipartFile pdf = detail.getPdf();
        
        //저장될 pdf 파일명
        String fileName = detail.getDmLinkKey()+".pdf";
        //파일 저장 경로
        String path = "C:\\DATA\\"+Common.getCurrnetYYYYMM()+"\\"+detail.getTrKey()+"\\"+"pdf";

        //디렉토리 생성
        File dir = new File(path);
        dir.mkdirs();
        

    //파일 저장처리SSS
        Path pdfPath = Paths.get(path).resolve(fileName);
        try {
            pdf.transferTo(pdfPath.toFile());
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    //파일 저장처리EEE
        detail.setPdfNm(fileName);
        detail.setPdfPath(path+"\\"+fileName);

    }

*/




}
