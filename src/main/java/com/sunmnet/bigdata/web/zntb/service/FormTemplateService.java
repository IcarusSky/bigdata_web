package com.sunmnet.bigdata.web.zntb.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewFormTemplate;
import com.sunmnet.bigdata.web.zntb.model.po.FormTemplate;
import com.sunmnet.bigdata.web.zntb.model.po.FormWidget;
import com.sunmnet.bigdata.web.zntb.persistent.FormTemplateDao;
import com.sunmnet.bigdata.web.zntb.persistent.FormTemplateWidgetDao;

/**
 * 表单模板管理
 */
@Service
public class FormTemplateService {

	@Autowired
	private FormTemplateDao formTemplateDao;
	@Autowired
	private FormTemplateWidgetDao formTemplateWidgetDao;
	
	/**
	 * 查询模板列表
	 */
	@SuppressWarnings("unchecked")
	public PageResult<ViewFormTemplate> getList(String formName, Integer userId, int pageNum, int pageSize) {
		PageInfo<FormTemplate> pageInfo = PageHelper.startPage(pageNum, pageSize)
				.doSelectPageInfo(() -> formTemplateDao.getFormTemplateList(formName, userId));
		List<ViewFormTemplate> transform = Lists.transform(pageInfo.getList(), ViewFormTemplate.TO);
		return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
				CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : transform);
	}
	
	/**
	 * 保存表单模板
	 */
	@Transactional("webTxManager")
	public void save(FormTemplate formTemplate, List<FormWidget> formWidgets) {
		formTemplateDao.save(formTemplate);

		for (FormWidget formWidget : formWidgets) {
			formWidget.setFormId(formTemplate.getFormId());
		}
		formTemplateWidgetDao.saveBatch(formWidgets);
	}

	/**
	 * 修改表单模板
	 */
	@Transactional("webTxManager")
	public void update(FormTemplate formTemplate, List<FormWidget> formWidgets) {
		FormTemplate oldFormTemplate = formTemplateDao.getFormTemplate(formTemplate.getFormId());
		delete(oldFormTemplate);
		save(formTemplate,formWidgets);
	}
	
	/**
	 * 删除表单模板
	 */
	@Transactional("webTxManager")
	public void delete(Integer id) {
		FormTemplate oldForm = formTemplateDao.getFormTemplate(id);
		if(oldForm == null) {
			throw new ServiceException("表单信息不存在");
		}
		delete(oldForm);		
	}
	
	/**
	 * 预览表单模板
	 */
	@SuppressWarnings("unchecked")
	public ViewFormTemplate info(Integer id) {
		FormTemplate form = formTemplateDao.getFormTemplate(id);
		ViewFormTemplate apply = (ViewFormTemplate)ViewFormTemplate.TO.apply(form);
		assert apply != null;

		List<FormWidget> formWidgetList = formTemplateWidgetDao.getListByFormId(id);
		Map<String, FormWidget> formWidgetMap = formWidgetList.stream().collect(Collectors.toMap(FormWidget::getColumnName, o -> o));

		return ViewFormTemplate.handleFormWidget(apply, formWidgetMap);
	}
	
	private void delete(FormTemplate oldFormTemplate) {
		formTemplateDao.delete(oldFormTemplate.getFormId());
		formTemplateWidgetDao.deleteByFormId(oldFormTemplate.getFormId());
	}

	/**
	 * 查询表单模板下拉列表
	 */
	@SuppressWarnings("unchecked")
	public List<ViewFormTemplate> getAllList() {
		List<FormTemplate> list = formTemplateDao.getFormTemplateList(null, null);
		List<ViewFormTemplate> transform = Lists.transform(list, ViewFormTemplate.TO);
		return transform;
	}

}
