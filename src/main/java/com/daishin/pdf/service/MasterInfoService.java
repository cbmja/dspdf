package com.daishin.pdf.service;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
import com.daishin.pdf.repository.MasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterInfoService {

    private final MasterRepository masterRepository;

    public Master findMaster(String MASTER_KEY){
        return masterRepository.findMaster(MASTER_KEY);
    }

    public List<Master> selectAll(){
        return masterRepository.selectAll();
    }

    public List<Master> selectMastersByPage(Page page){
        return masterRepository.selectMastersByPage(page);
    }

    public int countSearch(Search search){
        return masterRepository.countSearch(search);
    }

    public List<Master> selectExcept1(){return masterRepository.selectExcept1();}
}
