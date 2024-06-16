package com.daishin.pdf.mapper;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MasterMapper {

    void save(Master Master);

    void findMaster(String MASTER_KEY);

    void updateSendCnt(Master master);

    void updateStatus(Master master);

    void updateStatusAndTotalCnt(Master master);

    void selectAll();

    List<Master> selectMastersByPage(Page page);

    void countSearch(Search search);

    List<Master> selectStatusBetween2_7();

}
