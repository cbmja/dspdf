package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Detail;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DetailRepository {

    private final SqlSessionTemplate sql;

    public int save(Detail detail){
        return sql.insert("com.daishin.pdf.mapper.DetailMapper.save" , detail);
    }

    public int countGroup(Detail detail){
        return sql.selectOne("com.daishin.pdf.mapper.DetailMapper.countGroup" , detail);
    }

    public List getTrGroup(Detail detail) {
        return sql.selectList("com.daishin.pdf.mapper.DetailMapper.getTrGroup" , detail);
    }

    public Detail findReq(Detail detail){
        return sql.selectOne("com.daishin.pdf.mapper.DetailMapper.findReq" , detail);
    }

    public List getMasterGroup(String MASTER) {
        return sql.selectList("com.daishin.pdf.mapper.DetailMapper.getMasterGroup" , MASTER);
    }

    public int countMaster(String MASTER){
        return sql.selectOne("com.daishin.pdf.mapper.DetailMapper.countMaster" , MASTER);
    }
}
