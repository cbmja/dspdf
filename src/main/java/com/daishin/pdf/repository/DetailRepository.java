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


    public int save(Detail detail){
        int result = -1;
        try{
            result = sql.insert("com.daishin.pdf.mapper.DetailMapper.save" , detail);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setMASTER_KEY(detail.getMASTER());
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+detail.toString());
            errors.setERROR_CODE(LogCode.SQL_ERROR);
            errorsRepository.save(errors);
        }
        return result;
    }




    public int countGroup(Detail detail){
        int result = -1;
        try{
            result = sql.selectOne("com.daishin.pdf.mapper.DetailMapper.countGroup" , detail);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setMASTER_KEY(detail.getTR_KEY());
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+detail);
            errors.setERROR_CODE(LogCode.SQL_ERROR);
            errorsRepository.save(errors);
        }
        return result;
    }




/*
    public List getTrGroup(Detail detail) {
        return sql.selectList("com.daishin.pdf.mapper.DetailMapper.getTrGroup" , detail);
    }
*/



    public Detail findDetail(Detail detail){

        Detail _detail = null;
        try{
            _detail = sql.selectOne("com.daishin.pdf.mapper.DetailMapper.findDetail" , detail);
        }catch (Exception e){
            _detail = new Detail();
            _detail.setError(ResponseCode.SQL_ERROR);
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+detail);
            errors.setERROR_CODE(LogCode.SQL_ERROR);
            errorsRepository.save(errors);
        }
        return _detail;
    }




    public List<Detail> getMasterGroup(String MASTER) {
        List<Detail> list = new ArrayList<>();
        try{
            list = sql.selectList("com.daishin.pdf.mapper.DetailMapper.getMasterGroup" , MASTER);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setMASTER_KEY(MASTER);
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+MASTER);
            errors.setERROR_CODE(LogCode.SQL_ERROR);
            errorsRepository.save(errors);
        }
        return list;
    }




    public int countMaster(String MASTER){
        int result = -1;
        try{
            result = sql.selectOne("com.daishin.pdf.mapper.DetailMapper.countMaster" , MASTER);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setMASTER_KEY(MASTER);
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+MASTER);
            errors.setERROR_CODE(LogCode.SQL_ERROR);
            errorsRepository.save(errors);
        }
        return result;
    }


    public int updatePdfPath(Detail detail){
        int result = -1;
        try{
            result = sql.update("com.daishin.pdf.mapper.DetailMapper.updatePdfPath" , detail);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setMASTER_KEY(detail.getMASTER());
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+detail.getMASTER());
            errors.setERROR_CODE(LogCode.SQL_ERROR);
            errorsRepository.save(errors);
        }
        return result;
    }

    public int deleteById(Detail detail){
        int result = -1;
        try{
            result = sql.delete("com.daishin.pdf.mapper.DetailMapper.deleteById" , detail);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setMASTER_KEY(detail.getMASTER());
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+detail.getMASTER());
            errors.setERROR_CODE(LogCode.SQL_ERROR);
            errorsRepository.save(errors);
        }

        return result;
    }

}
