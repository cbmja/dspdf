package com.daishin.pdf.mapper;


import com.daishin.pdf.dto.Error;
import com.daishin.pdf.page.ErrorSearch;
import com.daishin.pdf.page.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErrorsMapper {

    void save(Error error);

    int countSearch(ErrorSearch errorSearch);

    List<Error> selectErrorsByPage(Page page);

    Error selectById(String id);
}
