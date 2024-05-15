package com.daishin.pdf.service;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.repository.ReqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReqSaveService {

    private final ReqRepository repository;


    public int save (ReqParam reqParam){
        reqParam.setSAVE_DATE(LocalDateTime.now());
        reqParam.setSTATUS("대기중");
        return repository.save(reqParam);
    }



}
