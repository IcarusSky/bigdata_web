package com.sunmnet.bigdata.web.zntb.persistent;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.sunmnet.bigdata.web.zntb.model.po.FormTemplate;

@Mapper
public interface FormTemplateDao {

	int save(FormTemplate formTemplate);
	
	FormTemplate getFormTemplate(Integer formId);

	void delete(Integer formId);

	List<FormTemplate> getFormTemplateList(@Param("formName") String formName, @Param("userId") Integer userId);
    


}
