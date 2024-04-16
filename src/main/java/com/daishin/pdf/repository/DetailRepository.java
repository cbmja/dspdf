package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Detail;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DetailRepository {

    private final SqlSessionTemplate sql;

    public int save(Detail detail){

        return sql.insert("com.daishin.pdf.mapper.DetailMapper.save" , detail);
    }
}
