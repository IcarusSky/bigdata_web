package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.FormWidget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FormWidgetDao {

    List<FormWidget> getListByFormId(@Param("formId") Integer formId);

    List<FormWidget> getListByDatasetId(@Param("datasetId") Integer datasetId);

    void saveBatch(@Param("formWidgets")List<FormWidget> formWidgets);

    void deleteByFormId(@Param("formId")Integer formId);
}
