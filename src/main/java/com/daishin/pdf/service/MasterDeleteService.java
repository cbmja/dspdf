package com.daishin.pdf.service;


import com.daishin.pdf.dto.Master;
import com.daishin.pdf.repository.MasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasterDeleteService {

    private final MasterRepository masterRepository;

    public int deleteById(Master master){
        return masterRepository.deleteById(master);
    }



}
