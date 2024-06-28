package com.daishin.pdf.service;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.repository.DetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetailInfoService {

    private final DetailRepository repository;


    public int countGroup (Detail detail){

        return repository.countGroup(detail);
    }

    public List<Detail> getTrGroup(Detail detail){
        return repository.getTrGroup(detail);
    }

    public Detail findDetail(Detail detail){
        return repository.findDetail(detail);
    }

    public List<Detail> getMasterGroup(String MASTER){
        return repository.getMasterGroup(MASTER);
    }

    public int countMaster(String MASTER){
        return repository.countMaster(MASTER);
    }

}
