package com.daishin.pdf.util;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.service.ReqInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Utils {

    private final ReqInfoService reqInfoService;


    /**
     * pdf 저장
     * @param file
     * @param reqParam
     */
    public void savePdf(MultipartFile file , ReqParam reqParam , Map<String , String> response){

        /////SSSpdf 저장SSS/////

        //String fileName = file.getOriginalFilename().substring(0 , file.getOriginalFilename().length()-4);
        String fileName = file.getOriginalFilename();
        
        //저장경로 (대량이면 dir 하나 더 생성)
        String path = reqParam.getTOTAL_SEND_CNT().equals("1") ? "C:\\DATA\\" : "C:\\DATA\\"+reqParam.getTR_KEY()+"\\";

        //디렉토리 생성
        File dir = new File(path);
        dir.mkdirs();

        //파일 저장처리SSS
        Path pdfPath = Paths.get(path).resolve(fileName);
        try {
            file.transferTo(pdfPath.toFile());

        } catch (IllegalStateException | IOException e) {
            if(reqParam.getTOTAL_SEND_CNT().equals("1")){
                response.put("pdf 저장 실패(단일)" , "pdf 저장 실패(단일)");
            } else {
                response.put("pdf 저장 실패(대량)" , "pdf 저장 실패(대량)");
            }
            e.printStackTrace();
        }
        /////EEEpdf 저장EEE/////
        reqParam.setPDF_PATH(path+fileName+".pdf");
    }

    public void saveJson(MultipartFile file , ReqParam reqParam , Map<String , String> response) throws IOException {

        //확장자 제외 파일명
        String fileName = file.getOriginalFilename().substring(0 , file.getOriginalFilename().length()-4);

        //저장 경로
        String path = reqParam.getTOTAL_SEND_CNT().equals("1") ? "C:\\DATA\\" : "C:\\DATA\\"+reqParam.getTR_KEY()+"\\";

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
                response.put("json 저장 실패(단일)" , "json 저장 실패(단일)");
                e.printStackTrace();
            }
        }
        //EEEEEEEEEEEEEEEE단일EEEEEEEEEEEEEEEE
        //SSSSSSSSSSSSSSSS대량SSSSSSSSSSSSSSSS
        else {
            /*대량일 경우 3가지 조건을 만족할 경우 json 저장
             1- TOTAL_SEND_CNT != 1   //총 전송 건수가 1이 아닌 경우(위의 if 문에서 체크)
             2- RECV_NUM == TOTAL_SEND_CNT   //그룹 내 순번이 그룹 총 전송 건수와 같은 경우(마지막 전송건일 경우)
             3- TR_KEY 로 select 했을 때 총 갯수가 TOTAL_SEND_CNT 와 동일할 경우
             (select count(*) from dbo.REQ where TR_KEY = #{TR_KEY})
            */
            String totalTrGroup = reqParam.getTOTAL_SEND_CNT();

            if(reqParam.getRECV_NUM().equals(totalTrGroup) && reqInfoService.countGroup(reqParam)==Integer.parseInt(totalTrGroup)){

                response.put("["+reqParam.getTR_KEY()+"] 그룹 전송 완료" , "["+reqParam.getTR_KEY()+"] 그룹 전송 완료");

                List<ReqParam> jsonList = reqInfoService.getTrGroup(reqParam);

                String jsonlist = mapper.writeValueAsString(jsonList);
                try {
                    FileWriter fileWriter = new FileWriter(path+reqParam.getTR_KEY()+".json");
                    fileWriter.write(jsonlist);
                    fileWriter.close();
                } catch (IOException e) {
                    response.put("json 저장 실패(대량)" , "json 저장 실패(대량)");
                    e.printStackTrace();
                }
            }
        }
        //EEEEEEEEEEEEEEEE대량EEEEEEEEEEEEEEEE
    }

}
