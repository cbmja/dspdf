package com.daishin.pdf.service;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.repository.DetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetailDeleteService {

    private final DetailRepository detailRepository;


    public int deleteById(Detail detail){
        return detailRepository.deleteById(detail);
    }
}
