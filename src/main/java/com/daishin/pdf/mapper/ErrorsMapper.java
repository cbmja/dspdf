package com.daishin.pdf.mapper;


import com.daishin.pdf.dto.Error;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ErrorsMapper {

    void save(Error error);

}
