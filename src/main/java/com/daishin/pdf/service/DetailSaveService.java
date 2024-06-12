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

        String filePath = detail.getPDF_PATH();
        int firstIndex = filePath.indexOf('\\');
        int secondIndex = filePath.indexOf('\\', firstIndex + 1);
        int thirdIndex = filePath.indexOf('\\', secondIndex + 1);
        String master =filePath.substring(secondIndex + 1, thirdIndex);
        detail.setMASTER(master);
        return repository.save(detail);
    }



}
