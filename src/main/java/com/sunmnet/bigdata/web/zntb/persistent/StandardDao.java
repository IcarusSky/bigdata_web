package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.StandardSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface StandardDao {

    int save(StandardSet standard);

    int update(StandardSet standard);

    int delete(@Param("id") Integer id);
    
    StandardSet getStandardById(@Param("id") Integer id);

    int batchDelete(@Param("ids") int[] ids);

    List<StandardSet> getStandardList();

    List<StandardSet> getlistByCondition(@Param("standardName") String standardName, @Param("standardValue") String standardValue);

    List<StandardSet> getListByCategoryId(@Param("categoryId") int categoryId);

    List<StandardSet> getListByCategoryCondition(@Param("categoryId") int categoryId, @Param("standardName") String standardName, @Param("standardValue") String standardValue);

    int countByCategoryId(@Param("categoryId") int categoryId);
}
