package com.daishin.pdf.repository;

import com.daishin.pdf.dto.ReqParam;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReqRepository {

    private final SqlSessionTemplate sql;

    public int save(ReqParam reqParam){
        return sql.insert("com.daishin.pdf.mapper.ReqMapper.save" , reqParam);
    }

    public int countGroup(ReqParam reqParam){
        return sql.selectOne("com.daishin.pdf.mapper.ReqMapper.countGroup" , reqParam);
    }

    public List getTrGroup(ReqParam reqParam) {
        return sql.selectList("com.daishin.pdf.mapper.ReqMapper.getTrGroup" , reqParam);
    }

    public ReqParam findReq(ReqParam reqParam){
        return sql.selectOne("com.daishin.pdf.mapper.ReqMapper.findReq" , reqParam);
    }

    public List getMasterGroup(String MASTER) {
        return sql.selectList("com.daishin.pdf.mapper.ReqMapper.getMasterGroup" , MASTER);
    }

    public int countMaster(String MASTER){
        return sql.selectOne("com.daishin.pdf.mapper.ReqMapper.countMaster" , MASTER);
    }
}
