package com.sunmnet.bigdata.web.zntb.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewFormWriteStatus;
import com.sunmnet.bigdata.web.zntb.model.po.FormWriteStatus;
import com.sunmnet.bigdata.web.zntb.persistent.FormWriteStatusDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FormWriteStatusService {

	@Autowired
	private FormWriteStatusDao formWriteStatusDao;

	public void save(Integer formId, Integer userId, Integer auditorStatus, Long dataId) {
		formWriteStatusDao.save(formId, userId, dataId, auditorStatus);
	}

	public void saveBatch(List<Map<String, Object>> records) {
		if (CollectionUtils.isEmpty(records)) {
			return;
		}
		formWriteStatusDao.saveBatch(records);
	}

	public void update(Integer userId, Integer formId, Integer dataId, Integer auditorStatus, String auditorDesc) {
		formWriteStatusDao.update(userId, formId, dataId, auditorStatus, auditorDesc);
	}

	//数据批量提交审核
	public void submit(Integer userId, Integer formId, Integer dataId){
		FormWriteStatus formWriteStatus = getFormWriteStatus(formId, dataId, userId);
		Integer auditorStatus = formWriteStatus.getAuditorStatus();
		// 针对已保存状态的数据提交审核
		if (auditorStatus == FormWriteStatus.AUDITORSTATUS.SAVE.getValue().intValue()) {
			formWriteStatusDao.update(userId, formId, dataId, FormWriteStatus.AUDITORSTATUS.UNAUDITOR.getValue(), "");
		}
		// 针对审核未通过状态的数据提交审核
		if (auditorStatus == FormWriteStatus.AUDITORSTATUS.AUDITORNO.getValue().intValue()) {
			formWriteStatusDao.update(userId, formId, dataId, FormWriteStatus.AUDITORSTATUS.REAUDITOR.getValue(), "");
		}
	}

	public List<Map<String, Object>> getWriterUserCount(List<Integer> idList) {
		return formWriteStatusDao.getWriterUserCount(idList);
	}

	@SuppressWarnings("unchecked")
	public PageResult<ViewFormWriteStatus> getWriterUserList(Integer auditorStatus, Integer userId, Integer formId, String writerName, String writerDeptName, String startDate, String endDate, int pageNum, int pageSize,Integer datadetail) {
		PageInfo<FormWriteStatus> pageInfo = PageHelper.startPage(pageNum, pageSize)
				.doSelectPageInfo(() -> formWriteStatusDao.getWriterUserList(formId, writerName, userId, auditorStatus,writerDeptName,startDate,endDate,datadetail));

		List<ViewFormWriteStatus> transform = Lists.transform(pageInfo.getList(), ViewFormWriteStatus.TO);

		return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
				CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : transform);
	}

	public FormWriteStatus getFormWriteStatus(Integer formId, Integer dataId, Integer userId) {
		return formWriteStatusDao.getFormWriteStatus(formId, dataId);
	}
	
	//删除表单数据关联的填写状态信息
	public int delete(Integer formId, Integer userId, Integer dataId) {
		return formWriteStatusDao.delete(formId, dataId, userId);
	}
}
