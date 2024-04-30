package com.daishin.pdf.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class Detail {

    private String trKey; //전송단위그룹 식별키
    private String recvNum; //순번 (단위그룹 내의 순번인지 전체 순번인지)
    private String dmLinkKey; //연계 식별키 (회원마다 할당되는 고유 번호?)
    private String dlvCd; //집배코드
    private String recvNm; //이름
    private String recvPostCd; //우편번호
    private String recvAddr; //주소
    private String recvAddrDetail; //상세주소
}
