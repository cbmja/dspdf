package com.daishin.pdf.service.detail;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.repository.DetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetailSaveService {

    private final DetailRepository detailRepository;

    public void save(Detail detail){
        detailRepository.save(detail);
    }
}
