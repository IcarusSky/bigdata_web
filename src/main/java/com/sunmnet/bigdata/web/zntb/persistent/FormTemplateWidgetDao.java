package com.sunmnet.bigdata.web.zntb.persistent;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sunmnet.bigdata.web.zntb.model.po.FormWidget;

@Mapper
public interface FormTemplateWidgetDao {

	
	void saveBatch(@Param("formWidgets")List<FormWidget> formWidgets);

	void deleteByFormId(Integer formId);

	List<FormWidget> getListByFormId(Integer id);
}
