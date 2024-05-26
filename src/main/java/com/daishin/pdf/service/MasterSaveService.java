package com.daishin.pdf.service;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.repository.MasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MasterSaveService {

    private final MasterRepository masterRepository;

    public int save (Master master){
        //master.setRECEIVED_TIME(LocalDateTime.now()+"");
        return masterRepository.save(master);
    }

    public int updateSendCnt(Master master){
        return masterRepository.updateSendCnt(master);
    }

    public int updateStatus(Master master){
        return masterRepository.updateStatus(master);
    }

}
