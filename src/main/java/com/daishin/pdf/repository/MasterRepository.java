package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.dto.ReqParam;
import com.daishin.pdf.page.Page;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MasterRepository {

    private final SqlSessionTemplate sql;

    public int save(Master master){
        return sql.insert("com.daishin.pdf.mapper.MasterMapper.save" , master);
    }

    public Master findMaster(Master master){
        return sql.selectOne("com.daishin.pdf.mapper.MasterMapper.findMaster",master);
    }

    public int updateSendCnt(Master master){
        return sql.update("com.daishin.pdf.mapper.MasterMapper.updateSendCnt",master);
    }

    public int updateStatus(Master master){
        return sql.update("com.daishin.pdf.mapper.MasterMapper.updateStatus",master);
    }

    public List<Master> selectAll(){
        return sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectAll");
    }

    public List<Master> selectMastersByPage(Page page){
        return sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectMastersByPage",page);
    }

}
