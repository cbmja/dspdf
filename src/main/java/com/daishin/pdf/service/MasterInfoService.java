package com.daishin.pdf.service;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.repository.MasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasterInfoService {

    private final MasterRepository masterRepository;

    public Master findMaster(Master master){
        return masterRepository.findMaster(master);
    }


}
