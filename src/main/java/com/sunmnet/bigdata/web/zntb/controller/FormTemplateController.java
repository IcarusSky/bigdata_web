package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewFormTemplate;
import com.sunmnet.bigdata.web.zntb.model.po.FormTemplate;
import com.sunmnet.bigdata.web.zntb.model.po.FormWidget;
import com.sunmnet.bigdata.web.zntb.service.FormTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 表单模板管理
 */
@RestController
@RequestMapping("/formTemplate")
public class FormTemplateController extends BaseController {

	@Autowired
	private FormTemplateService formTemplateService;

	@RequestMapping(value = "/getlist")
	public Object getList(
			@RequestParam(name = "formName",defaultValue = "") String formName,
			@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		Integer userId = authenticationHolder.getAuthenticatedUser().getId();
		return buildSuccJson(formTemplateService.getList(formName, userId, pageNum, pageSize));
	}
	
	// 新增表单和编辑表单都调用该接口
    @RequestMapping(value = "/save")
    public Object save(@RequestParam(name = "json") String json) {
        FormTemplate formTemplate = FormTemplate.translateJson(json);
        formTemplate.setUserId(authenticationHolder.getAuthenticatedUser().getId());

        List<FormWidget> formWidgets = null;
		try {
			formWidgets = FormWidget.translateJson(json);
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("格式布局信息[layoutJson中的内容]解析失败");
		}
        
		if(formTemplate.getFormId() == null) {
			formTemplateService.save(formTemplate, formWidgets);
		} else {
			formTemplateService.update(formTemplate, formWidgets);
		}
        return buildSuccJson("success");
    }
    
    @RequestMapping(value = "/delete")
    public Object delete(@RequestParam(name = "id") Integer id) {
    	formTemplateService.delete(id);
        return buildSuccJson("success");
    }

    @RequestMapping(value = "/info")
    public Object info(@RequestParam(name = "id") Integer id) {
        ViewFormTemplate form = formTemplateService.info(id);
        return buildSuccJson(form);
    }
    
    @RequestMapping(value = "/getAllList")
	public Object getAllList() {
		return buildSuccJson(formTemplateService.getAllList());
	}

}