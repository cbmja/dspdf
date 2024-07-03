package com.daishin.pdf.mapper;

import com.daishin.pdf.dto.Status;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatusMapper {


    List<Status> selectAll();

    Status selectByStatusCode(int statusCode);

}
