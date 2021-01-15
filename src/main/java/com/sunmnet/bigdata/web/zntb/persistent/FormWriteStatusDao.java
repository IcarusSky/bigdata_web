package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.FormWriteStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FormWriteStatusDao {

    void save(@Param("formId") Integer formId, @Param("userId") Integer userId, @Param("dataId") Long dataId, @Param("auditorStatus")Integer auditorStatus);

    void saveBatch(@Param("records") List<Map<String, Object>> records);

    void update(@Param("userId") Integer userId, @Param("formId") Integer formId, @Param("dataId") Integer dataId, @Param("auditorStatus")Integer auditorStatus, @Param("auditorDesc")String auditorDesc);

    List<Map<String, Object>> getWriterUserCount(@Param("idList") List<Integer> idList);

    List<FormWriteStatus> getWriterUserList(@Param("formId") Integer formId, @Param("writerName") String writerName, @Param("userId") Integer userId, @Param("auditorStatus") Integer auditorStatus, @Param("writerDeptName")String writerDeptName, @Param("startDate")String startDate, @Param("endDate")String endDate,@Param("datadetail") Integer datadetail);

    FormWriteStatus getFormWriteStatus(@Param("formId")Integer formId, @Param("dataId")Integer dataId);
    
    int delete(@Param("formId") Integer formId, @Param("dataId") Integer dataId, @Param("userId") Integer userId);
}
