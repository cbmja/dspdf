package com.daishin.pdf.mapper;

import com.daishin.pdf.dto.Detail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DetailMapper {

    void save(Detail detail);

    void countGroup(Detail detail);

    void getTrGroup(Detail detail);

    void findReq(Detail detail);

    void getMasterGroup(String MASTER);

    void countMaster(String MASTER);
}
