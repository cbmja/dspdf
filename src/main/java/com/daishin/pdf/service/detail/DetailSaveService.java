package com.daishin.pdf.service.detail;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.repository.DetailRepository;
import com.daishin.pdf.util.Common;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



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

        if(detail.getPdfYn().equals("Y")){
            //파일저장
        //    savePdfFile(detail);
        }
        detailRepository.save(detail);
    }

/*

    //base64 인코딩된 파일 데이터를 디코딩하여 파일로 변환 후 저장
    //pdf 로 오는 경우 (pdfYn -> Y 인 경우)
    private void savePdfFile(Detail detail){

        byte[] decodedBytes = Base64.getDecoder().decode(detail.getPdf());
        String filePath = "C:\\DATA\\"+ Common.getCurrnetYYYYMM()+"\\"+detail.getTrKey()+"\\";
        String fileName = detail.getDmLinkKey()+".pdf";

    //파일 저장처리SSS

        //디렉토리 생성
        String path = "C:\\DATA\\"+Common.getCurrnetYYYYMM()+"\\"+detail.getTrKey();
        File dir = new File(path);
        dir.mkdirs();

        Path outputPath = Paths.get(filePath+fileName);
        try {
            // 바이트 배열을 파일로 저장
            Files.write(outputPath, decodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    //파일 저장처리EEE
        detail.setPdfNm(fileName);
        detail.setPdfPath(filePath+fileName);

    }

*/



}
