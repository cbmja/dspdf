package com.daishin.pdf.service;

import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.repository.ReqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReqSaveService {

    private final ReqRepository repository;


    public int save (ReqParam reqParam){

        //reqParam.setSAVE_DATE(LocalDateTime.now()+"");

        String filePath = reqParam.getPDF_PATH();
        int firstIndex = filePath.indexOf('\\');
        int secondIndex = filePath.indexOf('\\', firstIndex + 1);
        int thirdIndex = filePath.indexOf('\\', secondIndex + 1);
        String master =filePath.substring(secondIndex + 1, thirdIndex);
        reqParam.setMASTER(master);
        return repository.save(reqParam);
    }



}
