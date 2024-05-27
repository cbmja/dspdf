package com.daishin.pdf.service;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.repository.ReqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReqInfoService {

    private final ReqRepository repository;


    public int countGroup (ReqParam reqParam){

        return repository.countGroup(reqParam);
    }

    public List<ReqParam> getTrGroup(ReqParam reqParam){
        return repository.getTrGroup(reqParam);
    }

    public ReqParam findReq(ReqParam reqParam){
        return repository.findReq(reqParam);
    }

    public List<ReqParam> getMasterGroup(String MASTER){
        return repository.getMasterGroup(MASTER);
    }

    public int countMaster(String MASTER){
        return repository.countMaster(MASTER);
    }

}
