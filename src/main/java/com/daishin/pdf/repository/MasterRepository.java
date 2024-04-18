package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Master;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MasterRepository {

    private final SqlSessionTemplate sql;

    public int save(Master master){

        return sql.insert("com.daishin.pdf.mapper.MasterMapper.save" , master);
    }

    public Master findByTrKey(String trKey){

        return sql.selectOne("com.daishin.pdf.mapper.MasterMapper.findByTrKey" , trKey);
    }

}
