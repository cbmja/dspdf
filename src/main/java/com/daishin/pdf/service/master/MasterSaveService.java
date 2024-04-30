package com.daishin.pdf.service.master;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.repository.MasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasterSaveService {

    private final MasterRepository masterRepository;

    public void save(Master master){

        masterRepository.save(master);
    }



}
