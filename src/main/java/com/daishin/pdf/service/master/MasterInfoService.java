package com.daishin.pdf.service.master;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.repository.MasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasterInfoService {

    private final MasterRepository masterRepository;

    public Master findByTrKey(String trKey){
        return masterRepository.findByTrKey(trKey);
    }

}
