package com.daishin.pdf.repository;

import com.daishin.pdf.dto.ReqParam;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReqRepository {

    private final SqlSessionTemplate sql;

    public int save(ReqParam reqParam){
        return sql.insert("com.daishin.pdf.mapper.ReqMapper.save" , reqParam);

    }

}
