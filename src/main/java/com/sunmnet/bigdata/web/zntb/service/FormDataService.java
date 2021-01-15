package com.sunmnet.bigdata.web.zntb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.core.security.authentication.AuthenticationHolder;
import com.sunmnet.bigdata.web.zntb.dataprovider.DataProviderManager;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.AggConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.ConfigComponent;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.DimensionConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.ValueConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.provider.jdbc.JdbcDataProvider;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.AggregateResult;
import com.sunmnet.bigdata.web.zntb.dataprovider.util.SqlColumn;
import com.sunmnet.bigdata.web.zntb.dataprovider.util.SqlHandle;
import com.sunmnet.bigdata.web.zntb.model.dto.UserExt;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewForm;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewFormWriteStatus;
import com.sunmnet.bigdata.web.zntb.model.po.*;
import com.sunmnet.bigdata.web.zntb.persistent.FormDao;
import com.sunmnet.bigdata.web.zntb.persistent.FormWriteStatusDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FormDataService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FormDataService.class);

	@Autowired
	private FormDao formDao;
	@Autowired
	private SecUserExtService userExtService;
	@Autowired
	private DatasourceService datasourceService;
	@Autowired
	private DatasetService datasetService;
	@Autowired
	private DataProviderService dataProviderService;
	@Autowired
	private AuthenticationHolder authenticationHolder;
	@Autowired
	private FormWidgetService formWidgetService;
	@Autowired
	private FormWriteStatusService formWriteStatusService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private FormService formService;
	@Autowired
	private FormWriteStatusDao formWriteStatusDao;
	public Map<String, Object> getData(Integer formId, Integer dataId, Integer userId) {

		List<FormWidget> formWidgetList = formWidgetService.getListByFormId(formId);

		if (dataId == null) {
			Map<String, Object> result = new HashMap<>();
			formWidgetList.stream().filter(formWidget -> formWidget.getDataPrefill() == 1).forEach(formWidget -> {
				result.put(formWidget.getColumnName(), prefill(formWidget));
			});
			return result;
		} else {
			FormWriteStatus formWriteStatus = formWriteStatusService.getFormWriteStatus(formId, dataId, userId);
			String dataTable = formDao.getForm(formId).getDataTable();
			SqlHandle sqlHandle = new SqlHandle(dataTable).CONDITION("id", "=", dataId);
			Map<String, Object> objectMap = jdbcTemplate.queryForMap(sqlHandle.toString());
			formWidgetList.forEach(formWidget -> {
				if (formWidget.getLabelType().contains("date")) {
					objectMap.put(formWidget.getColumnName(), FormWidget.valueTrans(formWidget, objectMap.get(formWidget.getColumnName())));
				}
			});
			objectMap.put(FormWriteStatus.auditorStatusKey,formWriteStatus.getAuditorStatus());

			Map<String, Object> result = new HashMap<>();
			objectMap.keySet().stream().filter(key -> objectMap.get(key) != null).forEach(key -> {
				result.put(key, objectMap.get(key));
			});
			return result;
		}

	}

	public Map<String, Object> getData(Integer formId, String columnNameForPrefill, String columnValueForPrefill) {
		if (formId == null || formId <= 0) {
			return Collections.emptyMap();
		}

		if (StringUtils.isEmpty(columnNameForPrefill)) {
			return Collections.emptyMap();
		}

		if (StringUtils.isEmpty(columnValueForPrefill)) {
			return Collections.emptyMap();
		}

		List<FormWidget> widgets = formWidgetService.getListByFormId(formId);
		if (CollectionUtils.isEmpty(widgets)) {
			return Collections.emptyMap();
		}

		FormWidget widgetForPrefill = widgets.stream().filter(widget -> widget.getColumnName()
				.equals(columnNameForPrefill)).findFirst().orElse(null);
		if (widgetForPrefill == null || !new Integer(0).equals(widgetForPrefill.getDataPrefill())) {
			return Collections.emptyMap();
		}

		List<FormWidget> widgetsToPrefill = widgets.stream().filter(widget -> {
			String prefillJson = widget.getPrefillJson();
			if (StringUtils.isEmpty(prefillJson)) {
				return false;
			}

			JSONObject prefillJsonObj = JSON.parseObject(prefillJson);
			if (prefillJsonObj == null) {
				return false;
			}

			JSONArray filterColumnConfigs = prefillJsonObj.getJSONArray("query");
			if (CollectionUtils.isEmpty(filterColumnConfigs)) {
				return false;
			}

			for (int i = 0; i < filterColumnConfigs.size(); i++) {
				JSONObject filterColumnConfig = filterColumnConfigs.getJSONObject(i);
				if (columnNameForPrefill.equals(filterColumnConfig.getString("form_column"))) {
					return true;
				}
			}

			return false;
		}).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(widgetsToPrefill)) {
			return Collections.emptyMap();
		}

		return widgetsToPrefill.stream().collect(Collectors.toMap(FormWidget::getColumnName,
				j -> this.prefill(j, columnNameForPrefill, columnValueForPrefill)));
	}

	public Map<String,? extends Object> getData(Integer formId, String json) {
		if (formId == null || formId <= 0) {
			return Collections.emptyMap();
		}

		List<FormWidget> widgets = formWidgetService.getListByFormId(formId);
		if (CollectionUtils.isEmpty(widgets)) {
			return Collections.emptyMap();
		}

		JSONObject jsonObject = JSONObject.parseObject(json);
		JSONArray  jsonArray = jsonObject.getJSONArray("query");
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject object = (JSONObject) jsonArray.get(i);
			String columnNameForPrefill = object.getString("columnName");
			FormWidget widgetForPrefill = widgets.stream().filter(widget -> widget.getColumnName()
					.equals(columnNameForPrefill)).findFirst().orElse(null);
			//if (widgetForPrefill == null || !new Integer(0).equals(widgetForPrefill.getDataPrefill())) {
			//	return Collections.emptyMap();
			//}
		}

		List<FormWidget> widgetsToPrefill = widgets.stream().filter(widget -> {
			String prefillJson = widget.getPrefillJson();
			if (StringUtils.isEmpty(prefillJson)) {
				return false;
			}

			JSONObject prefillJsonObj = JSON.parseObject(prefillJson);
			if (prefillJsonObj == null) {
				return false;
			}

			JSONArray filterColumnConfigs = prefillJsonObj.getJSONArray("query");
			if (CollectionUtils.isEmpty(filterColumnConfigs)) {
				return false;
			}
			// 查询条件中的字段数
			int inputSize = jsonArray.size();
			// 匹配数
			int matchingNumber = 0;
			int filterSize = filterColumnConfigs.size();
			for (int i = 0; i < inputSize; i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);
				String columnNameForPrefill = object.getString("columnName");
				for (int j = 0; j < filterSize; j++) {
					JSONObject filterColumnConfig = filterColumnConfigs.getJSONObject(j);
					if (columnNameForPrefill.equals(filterColumnConfig.getString("form_column"))) {
						String columnValueForPrefill = object.getString("columnValue");
						filterColumnConfig.put("filter_value", columnValueForPrefill);
						if (StringUtils.isEmpty(columnValueForPrefill)){
							continue;
						}
						matchingNumber++;
					}
				}
			}
			if (inputSize >= matchingNumber && filterSize <= matchingNumber) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(widgetsToPrefill)) {
			return Collections.emptyMap();
		}

		Map<String, String> collect = widgetsToPrefill.stream().collect(Collectors.toMap(FormWidget::getColumnName,
				j -> this.prefill(j, jsonArray)));
		//保留输入条件
		for (Object o : jsonArray) {
			JSONObject object = (JSONObject) o;
			String columnName = object.getString("columnName");
			String columnValue = object.getString("columnValue");
			if (StringUtils.isEmpty(collect.get(columnName))){
				collect.put(columnName,columnValue);
			}

		}
		return collect;
	}

	public void saveData(Integer formId, Integer userId, String json, boolean saveType) {
		Map<String, Object> data = JSONObject.parseObject(json);
		if (CollectionUtils.isEmpty(data.keySet())){
			throw new ServiceException("未填写任何数据，请重新提交！");
		}
		data.remove(FormWriteStatus.auditorStatusKey);
		Form form = formDao.getForm(formId);
		String dataTable = form.getDataTable();
		if (data.get("id") == null) {
			String sql = new SqlHandle(dataTable).OPERATE(SqlHandle.INSERT).OPERATEFIELD(data).toString();
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection ->
			connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
			, keyHolder);
			Integer auditorStatus = saveType ? FormWriteStatus.AUDITORSTATUS.UNAUDITOR.getValue() : FormWriteStatus.AUDITORSTATUS.SAVE.getValue();
			formWriteStatusService.save(formId, userId, auditorStatus, keyHolder.getKey().longValue());
			return;
		}

		int dataId = Integer.parseInt(String.valueOf(data.get("id")));
		String sql = new SqlHandle(dataTable).OPERATE(SqlHandle.UPDATE).OPERATEFIELD(data).CONDITION("id", "=", dataId).toString();
		jdbcTemplate.execute(sql);
		FormWriteStatus formWriteStatus = formWriteStatusService.getFormWriteStatus(formId, dataId, userId);
		Integer auditorStatus = formWriteStatus.getAuditorStatus();
		if (saveType && formWriteStatus.getAuditorStatus() == FormWriteStatus.AUDITORSTATUS.SAVE.getValue().intValue()) {
			auditorStatus = FormWriteStatus.AUDITORSTATUS.UNAUDITOR.getValue();
		}
		if (saveType && formWriteStatus.getAuditorStatus() == FormWriteStatus.AUDITORSTATUS.AUDITORNO.getValue().intValue()) {
			auditorStatus = FormWriteStatus.AUDITORSTATUS.REAUDITOR.getValue();
		}
		formWriteStatusService.update(userId, formId, dataId, auditorStatus, formWriteStatus.getAuditorDesc());
	}
	
	//管理员暴力修改表单数据
	public void updateData(String json,Integer formId){
		Map<String, Object> data = JSONObject.parseObject(json);
		int dataId = Integer.parseInt(String.valueOf(data.get("id")));
		Form form = formDao.getForm(formId);
		String dataTable = form.getDataTable();
		String sql = new SqlHandle(dataTable).OPERATE(SqlHandle.UPDATE).OPERATEFIELD(data).CONDITION("id", "=", dataId).toString();
		jdbcTemplate.execute(sql);
	}
	
	public String prefill(FormWidget widget) {
		if (widget == null) {
			return "";
		}
		if (widget.getDatasetId() == null || widget.getDatasetId() <= 0) {
			return "";
		}
		if (StringUtils.isEmpty(widget.getPrefillJson())) {
			return "";
		}

		// 数据集
		DashboardDataset dataset = datasetService.getDatasetById(widget.getDatasetId().intValue());
		if (dataset == null) {
			return "";
		}

		// 预填配置
		JSONObject prefillConfig = JSON.parseObject(widget.getPrefillJson());
		if (prefillConfig == null) {
			return "";
		}

		// 预填列配置
		String prefillColumn = prefillConfig.getString("read_column");
		if (StringUtils.isEmpty(prefillColumn)) {
			return "";
		}

		// 过滤条件列配置
		JSONArray filterColumnConfigs = prefillConfig.getJSONArray("query");
		if (CollectionUtils.isEmpty(filterColumnConfigs)) {
			return "";
		}

		// 用户扩展信息
		UserExt userExt = userExtService.selectByUserId(authenticationHolder.getAuthenticatedUser().getId());
		if (userExt == null) {
			return "";
		}

		for (int i = 0; i < filterColumnConfigs.size(); i++) {
			JSONObject filterColumnConfig = filterColumnConfigs.getJSONObject(i);

			// 如果该组件预填需要关联其他组件动态填写的值
			if (StringUtils.isNotEmpty(filterColumnConfig.getString("form_column"))) {
				return "";
			}

			String filterColumn = filterColumnConfig.getString("account_column");
			if (StringUtils.isEmpty(filterColumn)) {
				continue;
			}

			String filterValue;
			switch (filterColumn) {
			case "academy":
				filterValue = userExt.getAcademyCode();
				break;
			case "major":
				filterValue = userExt.getMajorCode();
				break;
			case "department":
				filterValue = userExt.getDepartmentId().toString();
				break;
			case "position":
				filterValue = userExt.getPositionId().toString();
				break;
			case "account_code":
				filterValue = userExt.getAccountCode();
				break;
			default:
				filterValue = "";
			}

			filterColumnConfig.put("filter_value", filterValue);
		}

		return this.doPrefill(dataset, prefillColumn, filterColumnConfigs);
	}

	public String prefill(FormWidget widget, JSONArray jsonArray) {
		if (widget == null) {
			return "";
		}
		if (widget.getDatasetId() == null || widget.getDatasetId() <= 0) {
			return "";
		}
		if (StringUtils.isEmpty(widget.getPrefillJson())) {
			return "";
		}
		if (jsonArray.isEmpty()) {
			return "";
		}

		// 数据集
		DashboardDataset dataset = datasetService.getDatasetById(widget.getDatasetId().intValue());
		if (dataset == null) {
			return "";
		}

		// 预填配置
		JSONObject prefillConfig = JSON.parseObject(widget.getPrefillJson());
		if (prefillConfig == null) {
			return "";
		}

		// 预填列配置
		String prefillColumn = prefillConfig.getString("read_column");
		if (StringUtils.isEmpty(prefillColumn)) {
			return "";
		}

		// 过滤条件列配置
		JSONArray filterColumnConfigs = prefillConfig.getJSONArray("query");
		if (CollectionUtils.isEmpty(filterColumnConfigs)) {
			return "";
		}

		// 查询条件中的字段数
		int inputSize = jsonArray.size();
		// 匹配数
		int matchingNumber = 0;
		int filterSize = filterColumnConfigs.size();
		for (int i = 0; i < inputSize; i++) {
			JSONObject object = (JSONObject) jsonArray.get(i);
			String columnNameForPrefill = object.getString("columnName");

			for (int j = 0; j < filterSize; j++) {
				JSONObject filterColumnConfig = filterColumnConfigs.getJSONObject(j);
				String filterColumn = filterColumnConfig.getString("form_column");
				if (columnNameForPrefill.equals(filterColumn)) {
					String columnValueForPrefill = object.getString("columnValue");
					filterColumnConfig.put("filter_value", columnValueForPrefill);
					if (StringUtils.isEmpty(columnValueForPrefill)){
						continue;
					}
					matchingNumber++;
				} 
			}
		}
		if (matchingNumber == 0 || inputSize < matchingNumber || matchingNumber < filterSize) {
			return "";
		}

		return this.doPrefill(dataset, prefillColumn, filterColumnConfigs);
	}


	public String prefill(FormWidget widget, String columnNameForPrefill, String columnValueForPrefill) {
		if (widget == null) {
			return "";
		}
		if (widget.getDatasetId() == null || widget.getDatasetId() <= 0) {
			return "";
		}
		if (StringUtils.isEmpty(widget.getPrefillJson())) {
			return "";
		}
		if (StringUtils.isEmpty(columnNameForPrefill) || StringUtils.isEmpty(columnValueForPrefill)) {
			return "";
		}

		// 数据集
		DashboardDataset dataset = datasetService.getDatasetById(widget.getDatasetId().intValue());
		if (dataset == null) {
			return "";
		}

		// 预填配置
		JSONObject prefillConfig = JSON.parseObject(widget.getPrefillJson());
		if (prefillConfig == null) {
			return "";
		}

		// 预填列配置
		String prefillColumn = prefillConfig.getString("read_column");
		if (StringUtils.isEmpty(prefillColumn)) {
			return "";
		}

		// 过滤条件列配置
		JSONArray filterColumnConfigs = prefillConfig.getJSONArray("query");
		if (CollectionUtils.isEmpty(filterColumnConfigs) || filterColumnConfigs.size() != 1) {
			return "";
		}

		JSONObject filterColumnConfig = filterColumnConfigs.getJSONObject(0);
		String filterColumn = filterColumnConfig.getString("form_column");
		if (!columnNameForPrefill.equals(filterColumn)) {
			return "";
		}

		filterColumnConfig.put("filter_value", columnValueForPrefill);
		return this.doPrefill(dataset, prefillColumn, filterColumnConfigs);
	}

	private String doPrefill(DashboardDataset dataset, String prefillColumn, JSONArray filterColumnConfigs) {
		// SQL类型数据集
		if (DashboardDataset.Type.SQL.name().equalsIgnoreCase(dataset.getType())) {
			// 查询列
			List<ValueConfig> valueConfigs = new ArrayList<>();
			ValueConfig valueConfig = new ValueConfig();
			valueConfig.setColumn(prefillColumn);
			valueConfig.setAggType("");
			valueConfigs.add(valueConfig);

			AggConfig config = new AggConfig();
			config.setRows(Collections.emptyList());
			config.setColumns(Collections.emptyList());
			config.setValues(valueConfigs);
			config.setFilters(this.getFormWidgetFilterConfigs(filterColumnConfigs));

			AggregateResult result = dataProviderService.queryAggData(dataset.getId(), config);
			if (result == null) {
				return "";
			}

			String[][] resultData = result.getData();
			if (ArrayUtils.isEmpty(resultData) || ArrayUtils.isEmpty(resultData[0]) || StringUtils.isEmpty(resultData[0][0])) {
				return "";
			}
			return resultData[0][0];
		}

		// 单表类型数据集
		if (DashboardDataset.Type.TABLE.name().equalsIgnoreCase(dataset.getType())) {
			JSONObject datasetConfig = JSON.parseObject(dataset.getData());
			String database = datasetConfig.getString("databasesSelect");
			String table = datasetConfig.getString("tablesSelect");

			String sqlCondition = "";
			List<ConfigComponent> filterConfigs = this.getFormWidgetFilterConfigs(filterColumnConfigs);
			for (ConfigComponent filterConfig : filterConfigs) {
				DimensionConfig config = (DimensionConfig) filterConfig;
				sqlCondition += "and " + config.getColumnName() + config.getFilterType() + "'" +
						config.getValues().get(0) + "' ";
			}

			String sql = String.format("select %s from %s where 1 = 1 %s limit 1", prefillColumn,
					database + "." + table, sqlCondition);
			LOGGER.info("查询表单组件预读数据SQL：{}", sql);

			DashboardDatasource datasource = datasourceService.getDatasourceById(dataset.getDatasourceId());
			JSONObject datasourceConfig = JSONObject.parseObject(datasource.getConfig());
			Map<String, String> dataSource = Maps.transformValues(datasourceConfig, Functions.toStringFunction());
			JdbcDataProvider dataProvider;
			try {
				dataProvider = (JdbcDataProvider) DataProviderManager.getDataProvider(datasource.getType(), dataSource, null);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}

			try (Connection connection = dataProvider.getConnection();
					Statement stat = connection.createStatement();
					ResultSet rs = stat.executeQuery(sql)) {
				boolean hasNext = rs.next();
				if (hasNext) {
					return rs.getString(1);
				}
			} catch (Exception e) {
				LOGGER.error("查询表单组件预读数据出错", e);
			}
		}

		return "";
	}

	private List<ConfigComponent> getFormWidgetFilterConfigs(JSONArray filterColumnConfigs) {
		List<ConfigComponent> filterConfigs = new ArrayList<>();
		for (int i = 0; i < filterColumnConfigs.size(); i++) {
			JSONObject filterColumnConfig = filterColumnConfigs.getJSONObject(i);

			// 过滤条件值
			String filterValue = filterColumnConfig.getString("filter_value");
			if (StringUtils.isEmpty(filterValue)) {
				continue;
			}

			DimensionConfig filterConfig = new DimensionConfig();
			filterConfig.setColumnName(filterColumnConfig.getString("dataset_column"));
			filterConfig.setFilterType(filterColumnConfig.getString("operation"));
			filterConfig.setValues(Lists.newArrayList(filterValue));

			filterConfigs.add(filterConfig);
		}
		return filterConfigs;
	}

	public PageResult<ViewForm> auditorList(String formName, Integer userId, int pageNum, int pageSize) {
		PageResult<ViewForm> pageResult = formService.getDataAuditorList(
				formName,
				userId,
				pageNum,
				pageSize);


		return handleFormList(pageResult);
	}

	private PageResult<ViewForm> handleFormList(PageResult<ViewForm> pageResult) {
		List<Integer> idList = pageResult.getRows().stream().map(ViewForm::getFormId).collect(Collectors.toList());

		List<Map<String, Object>> writerUserCount = formWriteStatusService.getWriterUserCount(idList);

		List<ViewForm> collect = pageResult.getRows().stream().peek(
				viewForm -> {
					long count = writerUserCount.stream().filter(o ->
					(int) o.get("form_id") == viewForm.getFormId() && (int) o.get("auditor_status") != FormWriteStatus.AUDITORSTATUS.SAVE.getValue()
							).count();
					viewForm.setWriteCount(String.valueOf(count));
					long auditorCount = writerUserCount.stream().filter(o ->
					(int) o.get("form_id") == viewForm.getFormId() && (int) o.get("auditor_status") == FormWriteStatus.AUDITORSTATUS.AUDITORY.getValue()
							).count();
					viewForm.setAuditorCount(auditorCount);
				}
				).collect(Collectors.toList());
		pageResult.setRows(collect);

		return pageResult;
	}

	public PageResult<ViewForm> dataList(String formName, Integer userId, int pageNum, int pageSize) {

		PageResult<ViewForm> pageResult = formService.getList(
				Form.PUBLISHSTATUS.PUBLISHED.getValue(),
				Form.AUDITORSTATUS.AUDITORY.getValue(),
				null,
				formName,
				userId,
				pageNum,
				pageSize);

		return handleFormList(pageResult);

	}
	
	
	public PageResult<ViewForm> dataAllList(String formName, int pageNum, int pageSize) {
		
		PageResult<ViewForm> pageResult = formService.getAllList(
				Form.PUBLISHSTATUS.PUBLISHED.getValue(),
				null,
				formName,
				pageNum,
				pageSize);
		
		return handleFormList(pageResult);
		
	}

	public List<String> getIdsByFromId(Integer formId, Integer userId, Integer auditorStatus, String writerName, String writerDeptName, String startDate, String endDate){
		List<FormWriteStatus> writerUserList = formWriteStatusDao.getWriterUserList(formId, writerName, userId, auditorStatus, writerDeptName, startDate, endDate,null);
		List<ViewFormWriteStatus> transform = Lists.transform(writerUserList, ViewFormWriteStatus.TO);
		List<String> idList = new ArrayList<>();
		for (ViewFormWriteStatus writeStatus : transform) {
			idList.add(writeStatus.getDataId().toString());
		}
		return idList;
	}
	
	public Map<String, Object> dataDetail(Integer formId, Integer userId, Integer auditorStatus,String jgh, String writerName, String writerDeptName, String startDate, String endDate, int pageNum, int pageSize) {
		ViewForm form = formService.info(formId);

		//标题栏
		List<FormWidget> formWidgetList = formWidgetService.getListByFormId(formId);
		List<String> title = formWidgetList.stream().map(FormWidget::getLabelName).collect(Collectors.toList());
		if (userId == null ) {
			title.add(0, "填报人");
			title.add(1, "填报人部门");
		}
		title.add(title.size(), "填报时间");

		//数据部分
		PageResult<ViewFormWriteStatus> byFormId = formWriteStatusService.getWriterUserList(auditorStatus ,userId, formId, writerName,writerDeptName,startDate,endDate, pageNum, pageSize,1);
		List<ViewFormWriteStatus> writerStatusList = byFormId.getRows();
		Map<Integer, ViewFormWriteStatus> writerStatusMap = writerStatusList.stream().collect(Collectors.toMap(ViewFormWriteStatus::getDataId, o -> o));
		List<Integer> dataIdList = writerStatusList.stream().map(ViewFormWriteStatus::getDataId).collect(Collectors.toList());
		dataIdList.add(0);
		SqlHandle sqlHandle = new SqlHandle(form.getDataTable()).CONDITION("id", dataIdList);
		String sql = sqlHandle.toString();
		if(!StringUtils.isEmpty(jgh)){
			sql=sql+"and jgh='"+jgh+"'";
		}
		List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);
 		List<Map<String, Object>> result = dataList.stream().map(o -> {
			Integer id = (Integer) o.get("id");
			Map<String, Object> hashMap = new HashMap<>();
			if (userId == null) {
				hashMap.put("填报人", writerStatusMap.get(id).getWriterName());
				hashMap.put("填报人部门", writerStatusMap.get(id).getWriterDetpName());
			}
			hashMap.put("formId", writerStatusMap.get(id).getFormId());
			hashMap.put("userId", writerStatusMap.get(id).getUserId());
			hashMap.put("dataTable", form.getDataTable());
			hashMap.put("dataId", id);
			hashMap.put("dataAuditorStatus", writerStatusMap.get(id).getAuditorStatus());
			hashMap.put("dataAuditorDesc", writerStatusMap.get(id).getAuditorDesc());
			hashMap.put("dataWriteDate", writerStatusMap.get(id).getWriteDate());
			Map<String, Object> submitData = new HashMap<>();//提交json格式示例

			List jsonList = new ArrayList();

			formWidgetList.forEach(formWidget -> {
                Map<String, Object> jsonData = new HashMap<>();
				String labelName = formWidget.getLabelName();
				String columnName = formWidget.getColumnName();
				Object value = o.get(columnName);
				hashMap.put(labelName, FormWidget.valueTrans(formWidget, value));

				submitData.put(columnName, value);

				jsonData.put("label", labelName);
				jsonData.put("key", columnName);
				jsonData.put("value", value);
				jsonList.add(jsonData);
			});
			hashMap.put("填报时间", writerStatusMap.get(id).getWriteDate());

			submitData.put("id",id);
			hashMap.put("submitData", submitData);
			hashMap.put("jsonData", jsonList);
			return hashMap;
		}).collect(Collectors.toList());

		// 先按照审核状态由小到大排序再按时间由大到小排序
		Collections.sort(result, Comparator.comparing(FormDataService::comparingByAuditorStatus).thenComparing(FormDataService::comparingByWriteDate , Comparator.reverseOrder()));

		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("title", title);
		hashMap.put("data", new PageResult<>(byFormId.getPage(), byFormId.getPageSize(), byFormId.getTotal(),
				CollectionUtils.isEmpty(result) ? Collections.emptyList() : result));

		if (MapUtils.isEmpty(form.getOperationJson()) || result.size() == 0) {
			return hashMap;
		}

		//合计
		Map<String, Object> objectMap = dataTotal(form);
		JSONArray operation = (JSONArray) form.getOperationJson().get("operation");
		Map<String, String> formWidgetName = formWidgetList.stream().collect(Collectors.toMap(FormWidget::getColumnName, FormWidget::getLabelName));

		List<String> total = operation.stream().map(o -> {
			String column = ((JSONObject) o).get("column").toString();
			String opera = ((JSONObject) o).get("opera").toString();
			String labelName = formWidgetName.get(column);
			Object value = objectMap.get(labelName);
			Form.OPERATION formOperation = Form.OPERATION.valueOf(opera);
			return labelName + formOperation.getName() + ":" + value.toString();
		}).collect(Collectors.toList());
		hashMap.put("total", total);

		return hashMap;
	}

	public Map<String, Object> dataTotal(ViewForm form) {
		if (MapUtils.isEmpty(form.getOperationJson())) {
			return null;
		}

		JSONArray operation = (JSONArray) form.getOperationJson().get("operation");
		if (CollectionUtils.isEmpty(operation)) {
			return null;
		}

		SqlHandle sqlHandle = new SqlHandle(form.getDataTable());

		for (Object o : operation) {
			String column = ((JSONObject) o).get("column").toString();
			String opera = ((JSONObject) o).get("opera").toString();
			String field = opera + "(" + column + ")";
			sqlHandle.SELECTFIELD(field, column);
		}
		Map<String, Object> objectMap = jdbcTemplate.queryForMap(sqlHandle.toString());
		List<FormWidget> formWidgetList = formWidgetService.getListByFormId(form.getFormId());

		Map<String, String> formWidgetName = formWidgetList.stream().collect(Collectors.toMap(FormWidget::getColumnName, FormWidget::getLabelName));

		return operation.stream().collect(Collectors.toMap(
				o -> {
					String column = (String) ((JSONObject) o).get("column");
					return formWidgetName.get(column);
				},
				o -> {
					String column = (String) ((JSONObject) o).get("column");
					Object value = objectMap.get(column);
					return value.toString();
				}
				));
	}

	@SuppressWarnings("resource")
	public void importDataFromFile(int userId, int formId, String suffixFileName, InputStream fileStream) {
		try {
			if (formId <= 0) {
				return;
			}

			if (fileStream == null) {
				return;
			}

			Form form = formDao.getForm(formId);
			if (form == null) {
				return;
			}

			List<FormWidget> widgets = formWidgetService.getListByFormId(formId);
			if (CollectionUtils.isEmpty(widgets)) {
				return;
			}

			Map<String, List<FormWidget>> widgetMappingByLabelName = widgets.stream()
					.collect(Collectors.groupingBy(FormWidget::getLabelName));

			Workbook workbook;
			if ("xls".equals(suffixFileName)) {
				workbook = new HSSFWorkbook(fileStream);
			}else {
				workbook = new XSSFWorkbook(fileStream);
			}
			/*if (FileType.MS_EXCEL_XLS.getContentType().equals(fileType)) {
                workbook = new HSSFWorkbook(fileStream);
            } else if (FileType.MS_EXCEL_XLSX.getContentType().equals(fileType)) {
                workbook = new XSSFWorkbook(fileStream);
            } else {
                workbook = new HSSFWorkbook(fileStream);
            }*/

			if (workbook.getNumberOfSheets() == 0) {
				return;
			}

			Sheet sheet = workbook.getSheetAt(0);
			if (sheet.getPhysicalNumberOfRows() == 0) {
				return;
			}

			int firstRowNum = sheet.getFirstRowNum();
			int lastRowNum = sheet.getLastRowNum();
			if (firstRowNum == lastRowNum) {
				return;
			}

			// 根据第一行字段中文名称获取对应数据库表字段名称
			Row firstRow = sheet.getRow(firstRowNum);
			if (firstRow.getPhysicalNumberOfCells() == 0) {
				return;
			}

			short firstCellNum = firstRow.getFirstCellNum();
			short lastCellNum = firstRow.getLastCellNum();

			StringJoiner columns = new StringJoiner(",");
			List<Short> validCellIndexes = new ArrayList<>();
			for (short i = firstCellNum; i < lastCellNum; i++) {
				Cell cell = firstRow.getCell(i);
				String cellValue = StringUtils.trimToEmpty(cell.getStringCellValue());
				if (StringUtils.isEmpty(cellValue)) {
					continue;
				}

				List<FormWidget> widget = widgetMappingByLabelName.get(cellValue);
				if (CollectionUtils.isEmpty(widget)) {
					continue;
				}

				validCellIndexes.add(i);
				columns.add(widget.get(0).getColumnName());
			}

			if (StringUtils.isEmpty(columns.toString())) {
				return;
			}

			// 根据数据行拼凑SQL语句中values部分
			List<String> values = new ArrayList<>();
			DecimalFormat df = new DecimalFormat("0");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			for (int i = firstRowNum + 1; i <= lastRowNum; i++) {
				Row row = sheet.getRow(i);
				StringJoiner value = new StringJoiner(",", "(", ")");
				List<String> cellStrs = new ArrayList<>();
				for (short j = firstCellNum; j < lastCellNum; j++) {
					if (!validCellIndexes.contains(j)) {
						continue;
					}

					FormWidget widget = widgetMappingByLabelName.get(firstRow.getCell(j).getStringCellValue()).get(0);
					Cell cell = row.getCell(j);

					if(cell==null){
						cell=row.createCell(j); 	    			
						cell.setCellValue("");
					}

					CellType cellType = cell.getCellTypeEnum();

					String cellValue;
					if (CellType.NUMERIC.equals(cellType) && widget.getLabelType().contains("date")) {
						cellValue = sdf.format(cell.getDateCellValue());
					} else if(CellType.NUMERIC.equals(cellType)) {
						cellValue = df.format(cell.getNumericCellValue());
					} else if (CellType.STRING.equals(cellType)) {
						cellValue = StringUtils.trimToEmpty(cell.getStringCellValue());
					} else if (CellType.BOOLEAN.equals(cellType)) {
						cellValue = String.valueOf(cell.getBooleanCellValue());
					} else {
						cellValue = "";
					}
					String keyTrans = FormWidget.keyTrans(widget, cellValue);
					cellStrs.add(keyTrans);
					value.add("'" + keyTrans + "'");
				}
				boolean flag = false;
				for (String cellStr : cellStrs) {
					if (StringUtils.isNotBlank(cellStr)) {
						flag = true;
						break;
					}
				}
				if (flag){
					values.add(value.toString());
				}
			}

			// 分批次批量插入数据
			int batchSize = 1000;
			if (values.size() <= batchSize) {
				this.batchSaveDataFromFile(userId, form, columns.toString(), values);
			} else {
				List<String> buffer = new ArrayList<>();
				Iterator<String> iterator = values.iterator();
				while (iterator.hasNext()) {
					buffer.add(iterator.next());

					if (!iterator.hasNext()) {
						this.batchSaveDataFromFile(userId, form, columns.toString(), buffer);
						break;
					}

					if (buffer.size() == batchSize) {
						this.batchSaveDataFromFile(userId, form, columns.toString(), buffer);
						buffer = new ArrayList<>();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void batchSaveDataFromFile(int userId, Form form, String columns, List<String> values) {
		String sqlTemplate = "insert into %s(%s) values %s";
		String sql = String.format(sqlTemplate, form.getDataTable(), columns, StringUtils.join(values.toArray(), ","));

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS), keyHolder);
		List<Integer> ids = keyHolder.getKeyList().stream()
				.map(i -> Integer.parseInt(i.values().iterator().next().toString())).collect(Collectors.toList());

		formWriteStatusService.saveBatch(ids.stream().map(id -> {
			Map<String, Object> record = new HashMap<>();
			record.put("formId", form.getFormId());
			record.put("userId", userId);
			record.put("dataId", id);
			return record;
		}).collect(Collectors.toList()));
	}

	public void createTable(Form form, List<FormWidget> formWidgets) {
		SqlHandle sqlHandle = new SqlHandle(SqlHandle.CREATE, form.getDataTable());
		sqlHandle.PRIMARYKEY("id").COMMENT(form.getFormName());
		sqlHandle.COLUMN(SqlColumn.defaultSqlColumn());
		List<SqlColumn> sqlColumns = formWidgets.stream().map(FormWidget::toSqlColumn).collect(Collectors.toList());
		sqlHandle.COLUMNLIST(sqlColumns);
		jdbcTemplate.execute(sqlHandle.toString());
	}

	public void dropTable(Form oldForm) {
		jdbcTemplate.execute("DROP TABLE " + oldForm.getDataTable() + ";");
	}

	//删除表单数据
	public void deleteData(Integer formId, Integer dataId, Integer userId){
		FormWriteStatus formWriteStatus = formWriteStatusService.getFormWriteStatus(formId, dataId, userId);
		Integer auditorStatus = formWriteStatus.getAuditorStatus();
		// 针对审核状态为已保存或者审核未通过的可进行删除，其他状态不允许删除操作
		if (auditorStatus == FormWriteStatus.AUDITORSTATUS.SAVE.getValue().intValue() 
				|| auditorStatus == FormWriteStatus.AUDITORSTATUS.AUDITORNO.getValue().intValue()) {

			Form form = formDao.getForm(formId);
			String dataTable = form.getDataTable();
			String sql = new SqlHandle(dataTable).OPERATE(SqlHandle.DELETE).CONDITION("id", "=", dataId).toString();
			jdbcTemplate.update(sql);
			formWriteStatusService.delete(formId, userId, dataId);
		}else{
			String value = "";
			if(auditorStatus == FormWriteStatus.AUDITORSTATUS.UNAUDITOR.getValue().intValue()){
				value = FormWriteStatus.AUDITORSTATUS.UNAUDITOR.getName();
			}
			if(auditorStatus == FormWriteStatus.AUDITORSTATUS.REAUDITOR.getValue().intValue()){
				value = FormWriteStatus.AUDITORSTATUS.REAUDITOR.getName();
			}
			if(auditorStatus == FormWriteStatus.AUDITORSTATUS.AUDITORY.getValue().intValue()){
				value = FormWriteStatus.AUDITORSTATUS.AUDITORY.getName();
			}
			throw new ServiceException("不能删除审核状态为"+value+"的数据");
		}        
	}
	
	
	//管理员删除表单数据
	public void adminDeleteData(Integer formId, Integer dataId, Integer userId){
		Form form = formDao.getForm(formId);
		String dataTable = form.getDataTable();
		String sql = new SqlHandle(dataTable).OPERATE(SqlHandle.DELETE).CONDITION("id", "=", dataId).toString();
		jdbcTemplate.update(sql);
		formWriteStatusService.delete(formId, userId, dataId);
	}
	
	
	private static Integer comparingByAuditorStatus(Map<String, Object> map){
		return (Integer) map.get("dataAuditorStatus");
	}

	private static String comparingByWriteDate(Map<String, Object> map){
		return (String) map.get("dataWriteDate");
	}

	public List<String> thinkQuery(Integer datasetId, String readColumn, String thinkQueryVluae) {
		// 数据集
		DashboardDataset dataset = datasetService.getDatasetById(datasetId);
		if (dataset == null ) {
			throw new ServiceException("没有获取到数据集信息");
		}
		String execSql="";// 最终执行查询的语句
		String sqlCondition = readColumn + " like " + "'" + thinkQueryVluae + "%' ";// 查询条件
		// SQL类型数据集
		if (DashboardDataset.Type.SQL.name().equalsIgnoreCase(dataset.getType())) {
			JSONObject data = JSONObject.parseObject(dataset.getData());
			data.getJSONObject("query");
			Map<String, String> query = Maps.transformValues(data.getJSONObject("query"), Functions.toStringFunction());
			String selectSql = getAsSubQuery(query.get("sql"));
			// 查询列
			String fsql = "\nSELECT DISTINCT %s \n FROM (\n%s\n) cb_view \n where %s limit 50";
			execSql = String.format(fsql, readColumn, selectSql, sqlCondition);

		}

		// 单表类型数据集
		if (DashboardDataset.Type.TABLE.name().equalsIgnoreCase(dataset.getType())) {
			JSONObject datasetConfig = JSON.parseObject(dataset.getData());
			String database = datasetConfig.getString("databasesSelect");
			String table = datasetConfig.getString("tablesSelect");
			execSql = String.format("\nSELECT DISTINCT %s \n FROM %s \n where %s limit 50", readColumn,
					database + "." + table, sqlCondition);
			LOGGER.info("查询表单组件预读数据SQL：{}", execSql);

		}
		DashboardDatasource datasource = datasourceService.getDatasourceById(dataset.getDatasourceId());
		JSONObject datasourceConfig = JSONObject.parseObject(datasource.getConfig());
		Map<String, String> dataSource = Maps.transformValues(datasourceConfig, Functions.toStringFunction());
		JdbcDataProvider dataProvider;
		try {
			dataProvider = (JdbcDataProvider) DataProviderManager.getDataProvider(datasource.getType(), dataSource, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		List<String> list = new LinkedList<>();
		try (Connection connection = dataProvider.getConnection();
				Statement stat = connection.createStatement();
				ResultSet rs = stat.executeQuery(execSql)) {
			while (rs.next()) {
				String row = rs.getString(1);
				list.add(row);
			}
		} catch (Exception e) {
			throw new ServiceException("查询表单组件预读数据出错");
		}
		return list;
	}

	private String getAsSubQuery(String rawQueryText) {
		String deletedBlankLine = rawQueryText.replaceAll("(?m)^[\\s\t]*\r?\n", "").trim();
		return deletedBlankLine.endsWith(";") ? deletedBlankLine.substring(0, deletedBlankLine.length() - 1) : deletedBlankLine;
	}
}
