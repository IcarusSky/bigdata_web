package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.FormWriter;
import com.sunmnet.bigdata.web.zntb.model.po.SecUserExt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FormWriterDao {

    void saveBatch(@Param("formWriters") List<FormWriter> formWriters);

    List<FormWriter> getListByFormId(@Param("id") Integer id);

    void deleteByFormId(@Param("formId")Integer formId);
    
    List<SecUserExt> getWriterListByCondition(@Param("formId")Integer formId,
    		@Param("departmentId")Integer departmentId,
    		@Param("positionId")Integer positionId,
    		@Param("academyCode")Integer academyCode,
    		@Param("academyName")String academyName, 
    		@Param("majorCode")Integer majorCode,
    		@Param("majorName")String majorName
    		);
}
