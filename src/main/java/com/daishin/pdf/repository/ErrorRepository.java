package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Error;
import com.daishin.pdf.log.LogCode;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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

}


