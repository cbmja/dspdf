package com.daishin.pdf.service;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.repository.DetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetailSaveService {

    private final DetailRepository repository;


    public int save (Detail detail){

        return repository.save(detail);
    }



}
