package com.daishin.pdf.mapper;

import com.daishin.pdf.dto.Master;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@SuppressWarnings("rawtypes")
public interface MasterMapper {

    void save(Master master);

    Master findByTrKey(String trKey);
}
