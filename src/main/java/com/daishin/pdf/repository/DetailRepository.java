package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Error;
import com.daishin.pdf.log.LogCode;
import com.daishin.pdf.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DetailRepository {


    private final ErrorRepository errorsRepository;
    private final SqlSessionTemplate sql;
    private final Logger logger = LoggerFactory.getLogger("daishin");

    //////////////////////////////////////OK        //////////////////////////////////////OK
    public int save(Detail detail){
        int result = -1;
        try{
            result = sql.insert("com.daishin.pdf.mapper.DetailMapper.save" , detail);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }
        return result;
    }
    //////////////////////////////////////OK        //////////////////////////////////////OK


    //////////////////////////////////////OK        //////////////////////////////////////OK
    public int countGroup(Detail detail){
        int result = -1;
        try{
            result = sql.selectOne("com.daishin.pdf.mapper.DetailMapper.countGroup" , detail);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }
        return result;
    }
    //////////////////////////////////////OK        //////////////////////////////////////OK



/*
    public List getTrGroup(Detail detail) {
        return sql.selectList("com.daishin.pdf.mapper.DetailMapper.getTrGroup" , detail);
    }
*/


    //////////////////////////////////////OK        //////////////////////////////////////OK
    public Detail findDetail(Detail detail){

        Detail _detail = null;
        try{
            _detail = sql.selectOne("com.daishin.pdf.mapper.DetailMapper.findDetail" , detail);
            if(_detail != null){
                logger.error(LogCode.DUPLICATE_VALUE+" : "+detail);
            }
        }catch (Exception e){
            _detail = new Detail();
            _detail.setError(ResponseCode.SQL_ERROR);
            logger.error(LogCode.SQL_ERROR/*+" : "+e.getMessage()*/);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }
        return _detail;
    }
    //////////////////////////////////////OK        //////////////////////////////////////OK


    //////////////////////////////////////OK        //////////////////////////////////////OK
    public List<Detail> getMasterGroup(String MASTER) {
        List<Detail> list = new ArrayList<>();
        try{
            list = sql.selectList("com.daishin.pdf.mapper.DetailMapper.getMasterGroup" , MASTER);
        }catch (Exception e){
            /*Detail detail = new Detail();
            detail.setError(LogCode.SQL_ERROR);
            list.add(detail);*/
            logger.error(LogCode.SQL_ERROR/*+" : "+e.getMessage()*/);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }
        return list;
    }
    //////////////////////////////////////OK        //////////////////////////////////////OK


    //////////////////////////////////////OK        //////////////////////////////////////OK
    public int countMaster(String MASTER){
        int result = -1;
        try{
            result = sql.selectOne("com.daishin.pdf.mapper.DetailMapper.countMaster" , MASTER);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }
        return result;
    }
    //////////////////////////////////////OK        //////////////////////////////////////OK
}
