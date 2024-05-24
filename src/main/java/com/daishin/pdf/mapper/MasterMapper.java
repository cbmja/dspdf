package com.daishin.pdf.mapper;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.page.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MasterMapper {

    void save(Master Master);

    void findMaster(Master master);

    void updateSendCnt(Master master);

    void updateStatus(Master master);

    void selectAll();

    void selectMastersByPage(Page page);

}
