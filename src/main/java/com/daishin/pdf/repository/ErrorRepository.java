package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Error;
import com.daishin.pdf.log.LogCode;
import com.daishin.pdf.page.ErrorSearch;
import com.daishin.pdf.page.Page;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ErrorRepository {

    private final SqlSessionTemplate sql;
    private final Logger logger = LoggerFactory.getLogger("daishin");

    public int save(Error error){
        int result = -1;
        try{
            result = sql.insert("com.daishin.pdf.mapper.ErrorMapper.save" , error);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
        }
        return result;

    }

    public int countSearch(ErrorSearch errorSearch){
        int result = 0;
        try{
            result = sql.selectOne("com.daishin.pdf.mapper.ErrorMapper.countSearch" , errorSearch);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
        }
        return result;
    }

    public List<Error> selectErrorsByPage(Page page){
        List<Error> errors = new ArrayList<>();
        try{
            errors = sql.selectList("com.daishin.pdf.mapper.ErrorMapper.selectErrorsByPage" , page);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
        }
        return errors;
    }

}


