package com.daishin.pdf.service;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.repository.ReqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReqSaveService {

    private final ReqRepository repository;


    public void save (ReqParam reqParam){

        repository.save(reqParam);
    }

}
