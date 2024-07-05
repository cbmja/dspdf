package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Error;
import com.daishin.pdf.dto.Status;
import com.daishin.pdf.log.LogCode;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatusRepository {

    private final SqlSessionTemplate sql;

    private final ErrorRepository errorsRepository;
    private final Logger logger = LoggerFactory.getLogger("daishin");

    public List<Status> selectAll(){

        List<Status> list = new ArrayList<>();

        try{
            list = sql.selectList("com.daishin.pdf.mapper.StatusMapper.selectAll");
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setREPOSITORY("StatusRepository");
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }

        return list;
    }

    public Status selectByStatusCode(int statusCode){

        Status result = new Status();

        try{
            result = sql.selectOne("com.daishin.pdf.mapper.StatusMapper.selectByStatusCode", statusCode);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setREPOSITORY("StatusRepository");
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }

        return result;
    }


}
