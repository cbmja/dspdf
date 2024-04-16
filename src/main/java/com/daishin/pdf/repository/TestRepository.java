package com.daishin.pdf.repository;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestRepository {

    private final SqlSessionTemplate sql;


    public int insertTest(String test) {

        return sql.insert("com.daishin.pdf.mapper.TestMapper.insertTest" , test);
    }

}
