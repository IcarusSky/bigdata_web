package com.sunmnet.bigdata.web.zntb.model.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.sunmnet.bigdata.web.core.util.DateUtils;
import com.sunmnet.bigdata.web.zntb.model.po.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewForm{

    private Integer formId;
    private String formName;
    private String formDesc;
    private String dataTable;
    private Map<String, Object> layoutJson;
    private Map<String, Object> operationJson;
    private Integer auditorId;
    private Integer dataAuditorId;
    private Integer auditorStatus;
    private String auditorDesc;
    private Integer publishStatus;
    private Integer required;
    private Integer categoryId;
    private Integer userId;
    private String createTime;
    private String updateTime;
    private String beginTime;
    private String endTime;


    private String categoryName;    //分类名称
    private String userName;    //发布人名称
    private Integer writeStatus;  //填报状态
    private Integer writeOperation = 0; //是否可填报
    private String writeCount; //填报数量
    private Long auditorCount; //审核通过数量
    private List<FormWriter> formWriter; //填报人设置列表
    private List<FormDataAuditor> formDataAuditor; //数据审核人设置列表
    private SecUserExt auditorUser; //表单审核人信息
    private SecUserExt dataAuditorUser; //数据审核人信息

    @SuppressWarnings("rawtypes")
	public static final Function TO = new Function<Form, ViewForm>() {
        @Nullable
        @Override
        public ViewForm apply(@Nullable Form input) {
            return new ViewForm(input);
        }
    };

    private ViewForm(Form form) {
        this.formId = form.getFormId();
        this.formName = form.getFormName();
        this.formDesc = form.getFormDesc();
        this.dataTable = form.getDataTable();
        this.layoutJson = JSONObject.parseObject(form.getLayoutJson());
        this.operationJson = JSONObject.parseObject(form.getOperationJson());
        this.auditorId = form.getAuditorId();
        this.dataAuditorId = form.getDataAuditorId();
        this.auditorStatus = form.getAuditorStatus();
        this.publishStatus = form.getPublishStatus();
        this.required = form.getRequired();
        this.categoryId = form.getCategoryId();
        this.userId = form.getUserId();
        this.auditorDesc = form.getAuditorDesc();
        this.beginTime = DateUtils.formatDateTime(form.getBeginTime());
        this.endTime = DateUtils.formatDateTime(form.getEndTime());
        //this.createTime = DateUtils.formatDateTime(form.getBeginTime());//Update By 李斌  2019-07-30
        this.createTime = DateUtils.formatDateTime(form.getCreateTime());
        
        this.updateTime = DateUtils.formatDateTime(form.getUpdateTime());


        this.categoryName = form.getCategoryName();
        this.userName = form.getUserName();
        this.writeStatus = form.getWriteStatus();
        Date now = new Date();
        if (now.getTime() < form.getBeginTime().getTime()) {
            this.writeOperation = 1;//未开始
        } else if (now.getTime() > form.getEndTime().getTime()) {
            this.writeOperation = 2;//已结束
        }
    }

    public static ViewForm handleFormWidget(ViewForm form, Map<String, FormWidget> formWidgetMap) {
        Map<String, Object> layoutJson = form.getLayoutJson();
        if (MapUtils.isEmpty(layoutJson)) {
            return form;
        }

        JSONArray rows = (JSONArray)layoutJson.get("rows");
        if (CollectionUtils.isEmpty(rows)) {
            return form;
        }


        List<Object> rowsList = rows.stream().peek(o -> {
            JSONArray columns = (JSONArray) ((JSONObject) o).get("columns");

            List<Object> collect = columns.stream().peek(column -> {
                String columnName = (String) ((JSONObject) column).get("column_name");
                FormWidget formWidget = formWidgetMap.get(columnName);
                if (formWidget != null) {
                    Map<String, Object> widgetJson = JSONObject.parseObject(formWidget.getWidgetJson());
                    Map<String, Object> prefillJson = JSONObject.parseObject(formWidget.getPrefillJson());
                    JSONObject formWidgetJson = JSONObject.parseObject(JSONObject.toJSONString(formWidget));
                    formWidgetJson.put("useForPrefill", formWidgetMap.values().stream().anyMatch(widget -> {
                        String json = widget.getPrefillJson();
                        if (StringUtils.isEmpty(json)) {
                            return false;
                        }

                        JSONObject jsonObject = JSON.parseObject(json);
                        if (jsonObject == null) {
                            return false;
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray("query");
                        if (CollectionUtils.isEmpty(jsonArray)) {
                            return false;
                        }

                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            if (columnName.equals(item.getString("form_column"))) {
                                return true;
                            }
                        }

                        return false;
                    }));
                    formWidgetJson.put("widgetJson", widgetJson);
                    formWidgetJson.put("prefillJson", prefillJson);
                    ((JSONObject) column).put("widget", formWidgetJson);
                }
            }).collect(Collectors.toList());

            ((JSONObject) o).put("columns", collect);
        }).collect(Collectors.toList());

        layoutJson.put("rows", rowsList);

        return form;
    }


    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormDesc() {
        return formDesc;
    }

    public void setFormDesc(String formDesc) {
        this.formDesc = formDesc;
    }

    public String getDataTable() {
        return dataTable;
    }

    public void setDataTable(String dataTable) {
        this.dataTable = dataTable;
    }

    public Map<String, Object> getLayoutJson() {
        return layoutJson;
    }

    public void setLayoutJson(Map<String, Object> layoutJson) {
        this.layoutJson = layoutJson;
    }

    public Map<String, Object> getOperationJson() {
        return operationJson;
    }

    public void setOperationJson(Map<String, Object> operationJson) {
        this.operationJson = operationJson;
    }

    public Integer getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Integer auditorId) {
        this.auditorId = auditorId;
    }

    public Integer getAuditorStatus() {
        return auditorStatus;
    }

    public void setAuditorStatus(Integer auditorStatus) {
        this.auditorStatus = auditorStatus;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getWriteStatus() {
        return writeStatus;
    }

    public void setWriteStatus(Integer writeStatus) {
        this.writeStatus = writeStatus;
    }

    public String getAuditorDesc() {
        return auditorDesc;
    }

    public void setAuditorDesc(String auditorDesc) {
        this.auditorDesc = auditorDesc;
    }

    public String getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(String writeCount) {
        this.writeCount = writeCount;
    }

    public Integer getWriteOperation() {
        return writeOperation;
    }

    public void setWriteOperation(Integer writeOperation) {
        this.writeOperation = writeOperation;
    }

    public List<FormWriter> getFormWriter() {
        return formWriter;
    }

    public SecUserExt getAuditorUser() {
        return auditorUser;
    }

    public Integer getDataAuditorId() {
        return dataAuditorId;
    }

    public void setDataAuditorId(Integer dataAuditorId) {
        this.dataAuditorId = dataAuditorId;
    }

    public SecUserExt getDataAuditorUser() {
        return dataAuditorUser;
    }

    public void setDataAuditorUser(SecUserExt dataAuditorUser) {
        this.dataAuditorUser = dataAuditorUser;
    }

    public Long getAuditorCount() {
        return auditorCount;
    }

    public void setAuditorCount(Long auditorCount) {
        this.auditorCount = auditorCount;
    }

    public void setAuditorUser(SecUserExt auditorUser) {
        this.auditorUser = auditorUser;
    }

    public void setFormWriter(List<FormWriter> formWriter) {
        this.formWriter = formWriter;
    }

    public List<FormDataAuditor> getFormDataAuditor() {
        return formDataAuditor;
    }

    public void setFormDataAuditor(List<FormDataAuditor> formDataAuditor) {
        this.formDataAuditor = formDataAuditor;
    }
}
