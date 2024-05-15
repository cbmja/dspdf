package com.daishin.pdf.service;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.repository.ReqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReqInfoService {

    private final ReqRepository repository;


    public Integer countGroup (ReqParam reqParam){

        return repository.countGroup(reqParam);
    }

}
