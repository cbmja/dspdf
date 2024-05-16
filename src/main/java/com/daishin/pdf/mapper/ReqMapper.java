package com.daishin.pdf.mapper;

import com.daishin.pdf.dto.ReqParam;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReqMapper {

    void save(ReqParam reqParam);

    void countGroup(ReqParam reqParam);

    void getTrGroup(ReqParam reqParam);
}
