package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewForm;
import com.sunmnet.bigdata.web.zntb.model.po.FormWriteStatus;
import com.sunmnet.bigdata.web.zntb.service.FormDataService;
import com.sunmnet.bigdata.web.zntb.service.FormWriteStatusService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/formdata")
public class FormDataController extends BaseController {

	@Autowired
	private FormDataService formDataService;
	@Autowired
	private FormWriteStatusService formWriteStatusService;

	@RequestMapping(value = "/data")
	public Object getData(@RequestParam(name = "id") Integer id,
			@RequestParam(name = "dataId", required = false) Integer dataId) {
		Integer userId = authenticationHolder.getAuthenticatedUser().getId();

		return buildSuccJson(formDataService.getData(id, dataId, userId));
	}

	@RequestMapping(value = "/dataByCopy")
	public Object dataByCopy(@RequestParam(name = "id") Integer id,
			@RequestParam(name = "dataId", required = false) Integer dataId) {
		Integer userId = authenticationHolder.getAuthenticatedUser().getId();

		Map<String, Object> data = formDataService.getData(id, dataId, userId);
		data.remove("id");
		data.put(FormWriteStatus.auditorStatusKey, FormWriteStatus.AUDITORSTATUS.SAVE.getValue());
		return buildSuccJson(data);
	}

	@RequestMapping(value = "/databycolumn")
	public Object prefill(@RequestParam(name = "id", required = false, defaultValue = "0") Integer id,
			@RequestParam(name = "columnName", required = false) String columnName,
			@RequestParam(name = "columnValue", required = false) String columnValue) {
		if (id <= 0) {
			return buildErrJson("ID必须大于零");
		}
		if (StringUtils.isEmpty(columnName)) {
			return buildErrJson("列名不能为空");
		}
		if (StringUtils.isEmpty(columnValue)) {
			return buildErrJson("列值不能为空");
		}

		return buildSuccJson(formDataService.getData(id, columnName, columnValue));
	}
	
	/**
	 * 	JSON示例：
	 * 	单个预读关联字段 {"query":[{"columnName":"form_id","columnValue":"2"}]}
	 *  多个预读关联字段 {"query":[{"columnName":"form_id","columnValue":"2"},{"columnName":"column_id","columnValue":"5"}]}
	 */
	@RequestMapping(value = "/dataByManyColumn")
	public Object prefill(@RequestParam(name = "id", required = false, defaultValue = "0") Integer id,
			@RequestParam(name = "json") String json) {
		if (id <= 0) {
			return buildErrJson("ID必须大于零");
		}
		if (StringUtils.isEmpty(json)) {
			return buildErrJson("json不能为空");
		}
		return buildSuccJson(formDataService.getData(id, json));
	}

	@RequestMapping(value = "/save")
	public Object saveData(@RequestParam(name = "json") String json,
			@RequestParam(name = "formId") Integer formId,
			@RequestParam(name = "saveType", defaultValue = "false") boolean saveType) {

		Integer userId = authenticationHolder.getAuthenticatedUser().getId();

		formDataService.saveData(formId, userId, json, saveType);

		return buildSuccJson("success");
	}
	
	
	@RequestMapping(value = "/updateData")
	public Object updateData(@RequestParam(name = "json") String json,
							 @RequestParam(name = "formId") Integer formId) {
		formDataService.updateData(json,formId);
		return buildSuccJson("success");
	}
	

	//批量提交审核
	@RequestMapping(value = "/batchsubmit")
	public Object batchsave(@RequestParam(name = "formId") Integer formId,
			@RequestParam(name = "ids") String ids,
			@RequestParam(name = "isAll") boolean isAll,
			@RequestParam(name = "single", defaultValue = "false") boolean single,
			@RequestParam(name = "auditor", defaultValue = "false") boolean auditor,
			@RequestParam(name = "writeStatus", defaultValue = "all") String writeStatus,
			@RequestParam(name = "writerName", defaultValue = "") String writerName,
			@RequestParam(name = "writerDeptName", defaultValue = "") String writerDeptName,
			@RequestParam(name = "startDate", defaultValue = "") String startDate,
			@RequestParam(name = "endDate", defaultValue = "") String endDate) {

		Integer userId = authenticationHolder.getAuthenticatedUser().getId();
		Integer auditorType = null;
		if (auditor) {
			auditorType = FormWriteStatus.AUDITORSTATUS.AUDITORY.getValue();
		}
		List<String> idList = new ArrayList<>();
		if (isAll){
			idList = formDataService.getIdsByFromId(formId,single?userId:null,auditorType,writerName,writerDeptName,startDate,endDate);
		}else {
			String[] split = ids.split(",");
			idList = Arrays.asList(split);
		}
		for (String sid : idList) {
			int id = Integer.parseInt(sid);
			formWriteStatusService.submit(userId, formId, id);
		}
		return buildSuccJson();
	}

	//表单个人数据
	@RequestMapping(value = "/datalist")
	public Object dataList(
			@RequestParam(name = "formName", defaultValue = "") String formName,
			@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		
		Integer userId = authenticationHolder.getAuthenticatedUser().getId();
		PageResult<ViewForm> viewFormPageResult = formDataService.dataList(formName, userId, pageNum, pageSize);
		return buildSuccJson(viewFormPageResult);
	}
	
	//表单全部数据
	@RequestMapping(value = "/dataAllList")
	public Object dataAllList(
			@RequestParam(name = "formName", defaultValue = "") String formName,
			@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		
		PageResult<ViewForm> viewFormPageResult = formDataService.dataAllList(formName, pageNum, pageSize);
		return buildSuccJson(viewFormPageResult);
	}
	
	
	//数据审核列表
	@RequestMapping(value = "/auditorlist")
	public Object auditorList(
			@RequestParam(name = "formName", defaultValue = "") String formName,
			@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

		Integer userId = authenticationHolder.getAuthenticatedUser().getId();
		PageResult<ViewForm> viewFormPageResult = formDataService.auditorList(formName, userId, pageNum, pageSize);
		return buildSuccJson(viewFormPageResult);
	}

	//表单数据详情
	@RequestMapping(value = "/writerdata")
	public Object writerDataList(
			@RequestParam(name = "formId") Integer formId,
			@RequestParam(name = "writerName", defaultValue = "") String writerName,
			@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		return buildSuccJson(formWriteStatusService.getWriterUserList(FormWriteStatus.AUDITORSTATUS.AUDITORY.getValue(), null ,formId, writerName, null, null, null, pageNum, pageSize,null));
	}

	//数据预览
	@RequestMapping(value = "/datadetail")
	public Object dataDetail(
			@RequestParam(name = "formId") Integer formId,
			@RequestParam(name = "single", defaultValue = "false") boolean single,
			@RequestParam(name = "auditor", defaultValue = "false") boolean auditor,
			@RequestParam(name = "writeStatus", defaultValue = "all") String writeStatus,
			@RequestParam(name = "jgh", defaultValue = "") String jgh,
			@RequestParam(name = "writerName", defaultValue = "") String writerName,
			@RequestParam(name = "writerDeptName", defaultValue = "") String writerDeptName,
			@RequestParam(name = "startDate", defaultValue = "") String startDate,
			@RequestParam(name = "endDate", defaultValue = "") String endDate,
			@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		Integer userId = null;
		if (single) {
			userId = authenticationHolder.getAuthenticatedUser().getId();
		}
		Integer auditorType = null;
		if (auditor) {
			auditorType = FormWriteStatus.AUDITORSTATUS.AUDITORY.getValue();
		}
		if (!"all".equals(writeStatus)){
			auditorType = Integer.parseInt(writeStatus);
		}
		return buildSuccJson(formDataService.dataDetail(formId, userId, auditorType ,jgh,writerName,writerDeptName,startDate,endDate,pageNum, pageSize));
	}

	@PostMapping(value = "/importdata")
	public Object importData(@RequestParam(value = "formId", required = false, defaultValue = "0") int formId,
			@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

		if (formId <= 0) {
			return buildErrJson("表单ID必须大于零");
		}

		if (file == null || file.isEmpty()) {
			return buildErrJson("数据文件不能为空");
		}

		Integer userId = authenticationHolder.getAuthenticatedUser().getId();

		// 获取上传的文件的名称    
		String filename = file.getOriginalFilename();
		//获取后缀
		String suffixFileName =  filename.substring(filename.lastIndexOf(".") + 1);

		formDataService.importDataFromFile(userId, formId, suffixFileName, file.getInputStream());

		return buildSuccJson();
	}

	@RequestMapping(value = "/batchauditor")
	public Object batchauditor(@RequestParam(name = "formId") Integer formId,
			@RequestParam(name = "ids") String ids,
			@RequestParam(name = "status") Integer status,
			@RequestParam(name = "desc") String desc,
			@RequestParam(name = "isAll") boolean isAll,
		    @RequestParam(name = "single", defaultValue = "false") boolean single,
		    @RequestParam(name = "auditor", defaultValue = "false") boolean auditor,
		    @RequestParam(name = "writeStatus", defaultValue = "all") String writeStatus,
		    @RequestParam(name = "writerName", defaultValue = "") String writerName,
		    @RequestParam(name = "writerDeptName", defaultValue = "") String writerDeptName,
		    @RequestParam(name = "startDate", defaultValue = "") String startDate,
		    @RequestParam(name = "endDate", defaultValue = "") String endDate
	) {
		Integer userId = authenticationHolder.getAuthenticatedUser().getId();
		Integer auditorType = null;
		if (auditor) {
			auditorType = FormWriteStatus.AUDITORSTATUS.AUDITORY.getValue();
		}
		if (!"all".equals(writeStatus)){
			auditorType = Integer.parseInt(writeStatus);
		}
		List<String> idList = new ArrayList<>();
		if (isAll){
			idList = formDataService.getIdsByFromId(formId,single?userId:null,auditorType,writerName,writerDeptName,startDate,endDate);
		}else {
			String[] split = ids.split(",");
			idList = Arrays.asList(split);
		}
		for (String sid : idList) {
			if (StringUtils.isEmpty(sid)){
				continue;
			}
			int id = Integer.parseInt(sid);
			formWriteStatusService.update(userId, formId, id, status, desc);
		}
		return buildSuccJson();
	}
	
	@RequestMapping(value = "/delete")
	public Object deleteData(@RequestParam(name = "formId") Integer formId,
			@RequestParam(name = "dataId") Integer dataId) {
		Integer userId = authenticationHolder.getAuthenticatedUser().getId();
		formDataService.deleteData(formId, dataId, userId);
		return buildSuccJson("success");
	}
	
	
	//管理员删除表单数据
	@RequestMapping(value = "/adminDeleteData")
	public Object adminDeleteData(
			@RequestParam(name = "formId") Integer formId,
			@RequestParam(name = "dataId") Integer dataId,
			@RequestParam(name = "userId") Integer userId) {
		formDataService.adminDeleteData(formId, dataId, userId);
		return buildSuccJson("success");
	}
	
	
	@RequestMapping(value = "/batchdelete")
	public Object batchdelete(@RequestParam(name = "formId") Integer formId,
			@RequestParam(name = "dataIds") String dataIds) {
		Integer userId = authenticationHolder.getAuthenticatedUser().getId();
		String[] split = dataIds.split(",");
		for (String sid : split) {
			int id = Integer.parseInt(sid);
			formDataService.deleteData(formId, id, userId);
		}
		return buildSuccJson("success");
	}
	
	/**
	 * @Description 针对填写方式为数据集填写且没有预读关联的单行输入框，在进行输入时获取联想查询结果
	 * @Author libin
	 * @Date 2019年8月29日下午2:19:30
	 * @param datasetId 数据集id
	 * @param readColumn 数据集预读字段
	 * @param thinkQueryVluae 输入联想查询的值
	 * @return
	 */
	@RequestMapping(value = "/thinkQuery")
	public Object thinkQuery(@RequestParam(name = "datasetId") Integer datasetId,
			@RequestParam(name = "readColumn") String readColumn,
			@RequestParam(name = "thinkQueryVluae") String thinkQueryVluae) {

		return buildSuccJson(formDataService.thinkQuery(datasetId, readColumn, thinkQueryVluae));
	}
}