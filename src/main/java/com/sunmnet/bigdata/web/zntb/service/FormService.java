package com.sunmnet.bigdata.web.zntb.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.core.util.DateUtils;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewForm;
import com.sunmnet.bigdata.web.zntb.model.po.*;
import com.sunmnet.bigdata.web.zntb.persistent.FormDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormService {

	@Autowired
	private FormDao formDao;
	@Autowired
	private SecUserExtService userExtService;
	@Autowired
	private FormWidgetService formWidgetService;
	@Autowired
	private FormDataService formDataService;
	@Autowired
	private FormWriterService formWriterService;
	@Autowired
	private FormDataAuditorService formDataAuditorService;

	@SuppressWarnings("unchecked")
	public PageResult<ViewForm> getList(Integer publishStatus, Integer auditorStatus, Integer categoryId, String formName, Integer userId, int pageNum, int pageSize) {

		PageInfo<Form> pageInfo = PageHelper.startPage(pageNum, pageSize)
				.doSelectPageInfo(() -> formDao.getFormList(publishStatus, auditorStatus, categoryId, formName, userId));

		List<ViewForm> transform = Lists.transform(pageInfo.getList(), ViewForm.TO);

		return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
				CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : transform);
	}
	
	public PageResult<ViewForm> getAllList(Integer publishStatus, Integer categoryId, String formName, int pageNum, int pageSize) {
		
		PageInfo<Form> pageInfo = PageHelper.startPage(pageNum, pageSize)
				.doSelectPageInfo(() -> formDao.getFormAllList(publishStatus, categoryId, formName));
		
		List<ViewForm> transform = Lists.transform(pageInfo.getList(), ViewForm.TO);
		
		return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
				CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : transform);
	}

	@SuppressWarnings("unchecked")
	public ViewForm info(Integer id) {
		Form form = formDao.getForm(id);
		ViewForm apply = (ViewForm)ViewForm.TO.apply(form);
		assert apply != null;

		List<FormWidget> formWidgetList = formWidgetService.getListByFormId(id);
		Map<String, FormWidget> formWidgetMap = formWidgetList.stream().collect(Collectors.toMap(FormWidget::getColumnName, o -> o));
		List<FormWriter> formWriterList = formWriterService.getListByFormId(id);
		apply.setFormWriter(formWriterList);
		SecUserExt auditorUser = userExtService.selectInfoByUserId(apply.getAuditorId());
		apply.setAuditorUser(auditorUser);
		List<FormDataAuditor> formDataAuditor = formDataAuditorService.getListByFormId(id);
		apply.setFormDataAuditor(formDataAuditor);
		//SecUserExt dataAuditorUser = userExtService.selectInfoByUserId(apply.getDataAuditorId());
		//apply.setDataAuditorUser(dataAuditorUser);

		return ViewForm.handleFormWidget(apply, formWidgetMap);
	}

	@SuppressWarnings("unchecked")
	public PageResult<ViewForm> getWriteList(Integer writeStatus, Integer required, String formName, int pageNum, int pageSize, Integer userId) {
		Map<String, Object> query = new HashMap<>();
		query.put("writeStatus", writeStatus);
		query.put("required", required);
		query.put("formName", formName);
		query.put("userId", userId);
		PageInfo<Form> pageInfo = PageHelper.startPage(pageNum, pageSize)
				.doSelectPageInfo(() -> formDao.getWriteList(query));

		List<ViewForm> transform = Lists.transform(pageInfo.getList(), ViewForm.TO);

		return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
				CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : transform);
	}

	@SuppressWarnings("unchecked")
	public List<ViewForm> getWriteTop(Integer userId) {
		Map<String, Object> query = new HashMap<>();
		query.put("userId", userId);
		List<Form> writeList = formDao.getWriteTop(query);
		return Lists.transform(writeList, ViewForm.TO);
	}


	public void publish(Integer formId) {
		formDao.publish(formId);
	}

	public void unPublish(Integer formId) {
		formDao.unPublish(formId);
	}
	@SuppressWarnings("unchecked")
	public PageResult<ViewForm> getAuditorList(Integer userId, Integer auditorStatus, String formName, int pageNum, int pageSize) {
		PageInfo<Form> pageInfo = PageHelper.startPage(pageNum, pageSize)
				.doSelectPageInfo(() -> formDao.getAuditorList(auditorStatus, formName, userId));

		List<ViewForm> transform = Lists.transform(pageInfo.getList(), ViewForm.TO);

		return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
				CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : transform);
	}

	public void auditor(Integer formId, Integer auditorStatus, String auditorDesc) {
		formDao.auditor(formId, auditorStatus, auditorDesc);

	}

	public int countByCategoryId(Integer categoryId) {
		return formDao.countByCategoryId(categoryId);
	}

	@Transactional("webTxManager")
	public void save(Form form, List<FormWidget> formWidgets, List<FormWriter> formWriters, List<FormDataAuditor> formDataAuditors, boolean saveType) {
		Integer auditorStatus = saveType ? Form.AUDITORSTATUS.UNAUDITOR.getValue() : Form.AUDITORSTATUS.SAVE.getValue();
		form.setAuditorStatus(auditorStatus);
		formDao.save(form);
		form.setDataTable("from_" + form.getFormId());
		formDao.updateDataTable(form);

		formDataService.createTable(form, formWidgets);

		for (FormWidget formWidget : formWidgets) {
			formWidget.setFormId(form.getFormId());
		}
		formWidgetService.saveBatch(formWidgets);

		for (FormWriter formWriter : formWriters) {
			formWriter.setFormId(form.getFormId());
		}
		formWriterService.saveBatch(formWriters);

		for (FormDataAuditor dataAuditor : formDataAuditors) {
			dataAuditor.setFormId(form.getFormId());
		}
		formDataAuditorService.saveBatch(formDataAuditors);
	}

	//现在只要求改
	@Transactional("webTxManager")
	public void updateAuditorSave(Form form, List<FormWidget> formWidgets, List<FormWriter> formWriters, List<FormDataAuditor> formDataAuditors, boolean saveType) {

		for (FormWriter formWriter : formWriters) {
			formWriter.setFormId(form.getFormId());
		}
		formWriterService.deleteByFormId(form.getFormId());
		formWriterService.saveBatch(formWriters);

		for (FormDataAuditor dataAuditor : formDataAuditors) {
			dataAuditor.setFormId(form.getFormId());
		}
		formDataAuditorService.deleteByFormId(form.getFormId());
		formDataAuditorService.saveBatch(formDataAuditors);
	}

	@SuppressWarnings("unused")
	private boolean existDataTableName(Form form) {
		return formDao.existDataTableName(form.getDataTable()) > 0;
	}

	@Transactional("webTxManager")
	public void update(Form form, List<FormWidget> formWidgets, List<FormWriter> formWriters, List<FormDataAuditor> formDataAuditors, boolean saveType)  {
		Form oldForm = formDao.getForm(form.getFormId());
		delete(oldForm);
		save(form,formWidgets,formWriters, formDataAuditors, saveType);
		// 针对审核未通过状态的编辑情况
		if (oldForm.getAuditorStatus() == Form.AUDITORSTATUS.AUDITORNO.getValue().intValue()) {
			// 提交审核，更新状态为重新审核中；保存，更新状态为待提交
			if (saveType) {
				auditor(form.getFormId(), Form.AUDITORSTATUS.REAUDITOR.getValue(), "");
			} else {
				auditor(form.getFormId(), Form.AUDITORSTATUS.SAVE.getValue(), "");
			}
		}
	}

	@Transactional("webTxManager")
	public void updateAuditor(Form form, List<FormWidget> formWidgets, List<FormWriter> formWriters, List<FormDataAuditor> formDataAuditors, boolean saveType)  {
		Form oldForm = formDao.getForm(form.getFormId());

		updateAuditorSave(oldForm,formWidgets,formWriters, formDataAuditors, saveType);

	}

	@Transactional("webTxManager")
	public void delete(Integer id) {
		Form oldForm = formDao.getForm(id);
		if(oldForm == null) {
			throw new ServiceException("表单信息不存在");
		}
		delete(oldForm);
	}

	public void delete(Form oldForm) {
		formDao.delete(oldForm.getFormId());
		formWidgetService.deleteByFormId(oldForm.getFormId());
		formWriterService.deleteByFormId(oldForm.getFormId());
		formDataAuditorService.deleteByFormId(oldForm.getFormId());
		formDataService.dropTable(oldForm);
	}

	@SuppressWarnings("unchecked")
	public PageResult<ViewForm> getDataAuditorList(String formName, Integer userId, int pageNum, int pageSize) {
		PageInfo<Form> pageInfo = PageHelper.startPage(pageNum, pageSize)
				.doSelectPageInfo(() -> formDao.getDataAuditorList(formName, userId));

		List<ViewForm> transform = Lists.transform(pageInfo.getList(), ViewForm.TO);

		return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
				CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : transform);
	}

	//针对"待提交"或者"审核未通过"状态的表单进行提交审核操作
	public void submit(Integer formId) {
		Form form = formDao.getForm(formId);
		if (form.getDataAuditorId() == null) {
			throw new ServiceException("未指定数据审核人");
		}
		if (form.getAuditorId() == null) {
			throw new ServiceException("未指定表单审核人");
		}
		Integer auditorStatus = form.getAuditorStatus();
		if (form.getAuditorStatus() == Form.AUDITORSTATUS.SAVE.getValue()) {
			// 待提交变为提交审核中
			auditorStatus = Form.AUDITORSTATUS.UNAUDITOR.getValue();
		}else if (form.getAuditorStatus() == Form.AUDITORSTATUS.AUDITORNO.getValue()) {
			// 审核未通过变为重新提交审核中
			auditorStatus = Form.AUDITORSTATUS.REAUDITOR.getValue();
		}else if(form.getAuditorStatus() == Form.AUDITORSTATUS.AUDITORY.getValue()){
			// 审核通过,不能再次提交
			throw new ServiceException("该表单已审核通过");
		}else if(form.getAuditorStatus() == Form.AUDITORSTATUS.UNAUDITOR.getValue()
				|| form.getAuditorStatus() == Form.AUDITORSTATUS.REAUDITOR.getValue()){
			// 审核中或者重新提交审核中,不能再次提交
			throw new ServiceException("该表单已提交审核");
		}
		formDao.auditor(formId, auditorStatus, "");
	}

	//针对"提交审核中"或者"重新提交审核中"状态的表单进行撤销提交操作
	public void revoke(Integer formId) {
		Form form = formDao.getForm(formId);
		Integer auditorStatus = form.getAuditorStatus();
		if (form.getAuditorStatus() == Form.AUDITORSTATUS.UNAUDITOR.getValue()) {
			// 审核中变为待提交
			auditorStatus = Form.AUDITORSTATUS.SAVE.getValue();
		}else if (form.getAuditorStatus() == Form.AUDITORSTATUS.REAUDITOR.getValue()) {
			// 重新提交审核中变为审核未通过
			auditorStatus = Form.AUDITORSTATUS.AUDITORNO.getValue();
		}else if(form.getAuditorStatus() == Form.AUDITORSTATUS.AUDITORY.getValue()){
			// 审核通过,不能撤销提交
			throw new ServiceException("该表单已审核通过");
		}else if(form.getAuditorStatus() == Form.AUDITORSTATUS.SAVE.getValue()
				|| form.getAuditorStatus() == Form.AUDITORSTATUS.AUDITORNO.getValue()){
			// 待提交或者审核未通过的,不能撤销提交
			throw new ServiceException("该表单未提交审核");
		}
		formDao.auditor(formId, auditorStatus, "");
	}

	//更新填写时间
	public void changeTime(Integer formId, String beginTime, String endTime){
		Form form = formDao.getForm(formId);
		if(form == null){
			throw new ServiceException("该表单信息不存在");
		}

		Date start = DateUtils.parseDateTime(beginTime);
		Date end = DateUtils.parseDateTime(endTime);
		if(start.getTime() >= end.getTime()){
			throw new ServiceException("截止填写时间必须大于开始填写时间");
		}
		Date current = new Date();
		if(current.getTime() >= end.getTime()){
			throw new ServiceException("截止填写时间必须大于当前时间");
		}
		form.setBeginTime(start);
		form.setEndTime(end);
		formDao.changeTime(form);
	}
}
