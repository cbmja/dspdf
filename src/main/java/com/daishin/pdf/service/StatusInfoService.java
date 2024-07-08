package com.daishin.pdf.service;

import com.daishin.pdf.dto.Status;
import com.daishin.pdf.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusInfoService {


    private final StatusRepository statusRepository;


    public List<Status> selectAll(){
        return statusRepository.selectAll();
    }

    public Status selectByStatusCode(int statusCode){
        return statusRepository.selectByStatusCode(statusCode);
    }

}
