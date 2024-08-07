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

    public List<Master> selectStatusBetween300AndLast(int statusCode){
        return masterRepository.selectStatusBetween300AndLast(statusCode);
    }

    public List<Master> selectMastersByPage(Page page){
        return masterRepository.selectMastersByPage(page);
    }

    public int countSearch(Search search){
        search.setSearch(search.getSearch().trim());
        return masterRepository.countSearch(search);
    }

    public List<Master> selectStatusBetween2_7(){return masterRepository.selectStatusBetween2_7();}

    public List<Master> selectByStatus(int status){
        return masterRepository.selectByStatus(status);
    }
}
