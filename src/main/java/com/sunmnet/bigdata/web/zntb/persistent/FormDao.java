package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.Form;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FormDao {

    List<Form> getFormList(@Param("publishStatus") Integer publishStatus, @Param("auditorStatus") Integer auditorStatus, @Param("categoryId")Integer categoryId, @Param("formName") String formName, @Param("userId") Integer userId);
    
    List<Form> getFormAllList(@Param("publishStatus") Integer publishStatus,  @Param("categoryId")Integer categoryId, @Param("formName") String formName);

    Form getForm(@Param("id")Integer id);

    List<Form> getWriteList(Map<String, Object> query);

    List<Form> getWriteTop(Map<String, Object> query);

    void publish(@Param("formId")Integer formId);

    void unPublish(@Param("formId")Integer formId);

    List<Form> getAuditorList(@Param("auditorStatus")Integer auditorStatus, @Param("formName")String formName, @Param("userId")Integer userId);

    List<Form> getDataAuditorList(@Param("formName")String formName, @Param("userId")Integer userId);

    void auditor(@Param("formId")Integer formId, @Param("auditorStatus")Integer auditorStatus, @Param("auditorDesc")String auditorDesc);

    int countByCategoryId(@Param("categoryId") Integer categoryId);

    int save(Form form);

    void update(Form form);
    
    void updateDataTable(Form form);

    int existDataTableName(@Param("dataTable")String dataTable);

    void delete(@Param("formId")Integer formId);
    
    void changeTime(Form form);
}
