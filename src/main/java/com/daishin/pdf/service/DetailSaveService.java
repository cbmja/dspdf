package com.daishin.pdf.service;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.repository.DetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DetailSaveService {

    private final DetailRepository repository;


    public int save (Detail detail){

        return repository.save(detail);
    }

    public int updatePdfPath(Detail detail){
        return repository.updatePdfPath(detail);
    }



}
