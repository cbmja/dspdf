package com.daishin.pdf.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
@SuppressWarnings("rawtypes")
public interface TestMapper {

    void insertTest(String test);
}
