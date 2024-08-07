package com.daishin.pdf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@ToString(exclude = {"PK", "SAVE_DATE" , "PDF_PATH" ,"MASTER" , "PDF_NM" , "error"})
public class Detail {

    @JsonProperty("PK")
    private String PK;
    @JsonProperty("TR_KEY")
    private String TR_KEY;
    @JsonProperty("TOTAL_SEND_CNT")
    private Integer TOTAL_SEND_CNT;
    @JsonProperty("DLV_TYPE_CD")
    private String DLV_TYPE_CD;
    @JsonProperty("PRINT_TYPE_NM")
    private String PRINT_TYPE_NM;
    @JsonProperty("PAGE_CNT")
    private Integer PAGE_CNT;
    @JsonProperty("RET_YN")
    private String RET_YN;
    @JsonProperty("RECV_NUM")
    private Integer RECV_NUM;
    @JsonProperty("DM_LINK_KEY")
    private String DM_LINK_KEY;
    @JsonProperty("CIFNO")
    private String CIFNO;
    @JsonProperty("DLV_CD")
    private String DLV_CD;
    @JsonProperty("RECV_NM")
    private String RECV_NM;
    @JsonProperty("RECV_POST_CD")
    private String RECV_POST_CD;
    @JsonProperty("RECV_ADDR")
    private String RECV_ADDR;
    @JsonProperty("RECV_ADDR_DETAIL")
    private String RECV_ADDR_DETAIL;
    @JsonProperty("PDF_PATH")
    private String PDF_PATH;
    @JsonProperty("SAVE_DATE")
    private LocalDateTime SAVE_DATE;
    @JsonProperty("MASTER")
    private String MASTER;
    @JsonProperty("PDF_NM")
    private String PDF_NM;
    @JsonProperty("File")
    private MultipartFile File;

    @JsonIgnore
    private String error="";




    public Detail detailSetting(Detail detail , String receivingPath){

        // pdf_path , master 값 설정 SSS
        String path = "";
        String master = "";
        //단일
        if(detail.getTOTAL_SEND_CNT() == 1){

            LocalTime currentTime = LocalTime.now();

            // 기준 시간 설정
            LocalTime comparisonTime = LocalTime.of(14, 0);

            //14 이전이면 년도-월-오늘날짜
            if (currentTime.isBefore(comparisonTime)) {
                path = receivingPath+ LocalDate.now()+"\\";
                master = LocalDate.now()+"";
            } else {
                //14 이후이면 년도-월-내일날짜
                path = receivingPath+LocalDate.now().plusDays(1L)+"\\";
                master = LocalDate.now().plusDays(1L)+"";
            }
        } else {
            //대량
            path = receivingPath+ detail.getTR_KEY()+"\\";
            master = detail.getTR_KEY();
        }
        detail.setPDF_PATH(path);
        detail.setMASTER(master);
        detail.setPK(detail.getTR_KEY()+"_"+detail.getRECV_NUM());
        detail.setPDF_NM(detail.getFile().getOriginalFilename());
        return detail;
    }



}
