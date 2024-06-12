package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
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

    public Master findMaster(String MASTER_KEY){
        return sql.selectOne("com.daishin.pdf.mapper.MasterMapper.findMaster",MASTER_KEY);
    }

    public int updateSendCnt(Master master){
        return sql.update("com.daishin.pdf.mapper.MasterMapper.updateSendCnt",master);
    }

    public int updateStatus(Master master){
        return sql.update("com.daishin.pdf.mapper.MasterMapper.updateStatus",master);
    }

    public int updateStatusAndTotalCnt(Master master){
        return sql.update("com.daishin.pdf.mapper.MasterMapper.updateStatusAndTotalCnt",master);
    }

    public List<Master> selectAll(){
        return sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectAll");
    }

    public List<Master> selectMastersByPage(Page page){
        return sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectMastersByPage",page);
    }

    public int countSearch(Search search){
        return sql.selectOne("com.daishin.pdf.mapper.MasterMapper.countSearch" , search);
    }

    public List<Master> selectStatusBetween1_5(){return sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectStatusBetween1_5");}

}
