package com.sunmnet.bigdata.web.zntb.model.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.sunmnet.bigdata.web.core.util.DateUtils;
import com.sunmnet.bigdata.web.zntb.model.po.FormTemplate;
import com.sunmnet.bigdata.web.zntb.model.po.FormWidget;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewFormTemplate{

    private Integer formId;
    private String formName;
    private String formDesc;
    private Map<String, Object> layoutJson;
    private Map<String, Object> operationJson;
    private Integer categoryId;
    private Integer userId;
    private String createTime;
    private String updateTime;
    private String categoryName;    //分类名称
    private String userName;    //发布人名称
    private String departmentName;    //发布人一级部门


    @SuppressWarnings("rawtypes")
	public static final Function TO = new Function<FormTemplate, ViewFormTemplate>() {
        @Nullable
        @Override
        public ViewFormTemplate apply(@Nullable FormTemplate input) {
            return new ViewFormTemplate(input);
        }
    };

    private ViewFormTemplate(FormTemplate form) {
        this.formId = form.getFormId();
        this.formName = form.getFormName();
        this.formDesc = form.getFormDesc();
        this.layoutJson = JSONObject.parseObject(form.getLayoutJson());
        this.operationJson = JSONObject.parseObject(form.getOperationJson());
        this.categoryId = form.getCategoryId();
        this.userId = form.getUserId();
        this.createTime = DateUtils.formatDateTime(form.getCreateTime());
        this.updateTime = DateUtils.formatDateTime(form.getUpdateTime());
        this.categoryName = form.getCategoryName();
        this.userName = form.getUserName();
        this.departmentName = form.getDepartmentName();
    }

    public static ViewFormTemplate handleFormWidget(ViewFormTemplate form, Map<String, FormWidget> formWidgetMap) {
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

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

}
