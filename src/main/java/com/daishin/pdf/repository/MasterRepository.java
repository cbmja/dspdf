package com.daishin.pdf.repository;

import com.daishin.pdf.dto.Error;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.log.LogCode;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
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
public class MasterRepository {

    private final ErrorRepository errorsRepository;

    private final SqlSessionTemplate sql;
    private final Logger logger = LoggerFactory.getLogger("daishin");


    public List<Master> selectAll(){ //////////////////////////////////////OK
        List<Master> list = new ArrayList<>();
        try{
            list = sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectAll");
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }
        return list;
    }



    public int save(Master master){ //////////////////////////////////////OK
        int result = -1;
        try{
            result = sql.insert("com.daishin.pdf.mapper.MasterMapper.save" , master);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+master);
            errorsRepository.save(errors);
        }
        return result;
    }



    public Master findMaster(String MASTER_KEY){ //////////////////////////////////////OK
        Master master = null;
        try{
            master = sql.selectOne("com.daishin.pdf.mapper.MasterMapper.findMaster",MASTER_KEY);
        }catch (Exception e){
            master = new Master();
            master.setError(ResponseCode.SQL_ERROR);
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+MASTER_KEY);
            errorsRepository.save(errors);
        }
        return master;
    }




    public int updateSendCnt(Master master){ //////////////////////////////////////OK
        int result = -1;
        try{
            result = sql.update("com.daishin.pdf.mapper.MasterMapper.updateSendCnt",master);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+master);
            errorsRepository.save(errors);
        }
        return result;
    }




    public int updateStatus(Master master){ //////////////////////////////////////OK
        int result = -1;
        try{
            result = sql.update("com.daishin.pdf.mapper.MasterMapper.updateStatus",master);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+master);
            errorsRepository.save(errors);
        }
        return result;
    }




    public int updateStatusAndTotalCnt(Master master){ //////////////////////////////////////OK
        int result = -1;
        try{
            result = sql.update("com.daishin.pdf.mapper.MasterMapper.updateStatusAndTotalCnt",master);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+master);
            errorsRepository.save(errors);
        }
        return result;
    }




/*    public List<Master> selectAll(){
        return sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectAll");
    }*/



    public List<Master> selectMastersByPage(Page page){ //////////////////////////////////////OK
        List<Master> list = new ArrayList<>();
        try{
            list = sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectMastersByPage",page);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+page);
            errorsRepository.save(errors);
        }

        return list;
    }




    public int countSearch(Search search){ //////////////////////////////////////OK
        int result = 0;
        try{
            result = sql.selectOne("com.daishin.pdf.mapper.MasterMapper.countSearch" , search);
        }catch (Exception e){
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage()+"\n param : "+search);
            errorsRepository.save(errors);
        }
        return result;
    }




    public List<Master> selectStatusBetween2_7(){
        List<Master> list = new ArrayList<>();
        try {
            list = sql.selectList("com.daishin.pdf.mapper.MasterMapper.selectStatusBetween2_7");
        }catch (Exception e) {
            logger.error(LogCode.SQL_ERROR);
            e.printStackTrace();
            Error errors = new Error();
            errors.setERROR_MESSAGE(e.getMessage());
            errorsRepository.save(errors);
        }

        return list;
    }

}
