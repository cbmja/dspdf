package com.daishin.pdf.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class Detail {

    private String trKey; //전송단위그룹 식별키
    private String recvNum; //순번 (단위그룹 내의 순번인지 전체 순번인지)
    private String dmLinkKey; //연계 식별키 (회원마다 할당되는 고유 번호?)
    private String regNo; //등기 번호
    private String postCode; //집배 코드
    private String retYn; //반송 여부
    private String pdfYn; //pdf 존재 여부
    private String docDataNm; //가변 데이터 파일명
    private String docDataPath; //가변 데이터 저장 위치
    private String pdfNm; //pdf 파일명
    private String pdfPath; //pdf 저장 위치

    private String docData; //가변 데이터
    private MultipartFile pdf; //첨부 파일
    
    private String docDataStatus = "접수완료"; // docData 처리상태 : 접수완료 / 처리중 / 처리완료 등

}
