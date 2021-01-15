package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Date;

public class FormTemplate {

    private Integer formId;
    private String formName;
    private String formDesc;
    private String layoutJson;
    private String operationJson;
    private Integer categoryId;
    private Integer userId;
    private Date createTime;
    private Date updateTime;
    private String categoryName;
    private String userName;
    private String departmentName;


    public static FormTemplate translateJson(String json) {
        FormTemplate formTemplate = new FormTemplate();
        JSONObject jsonObject = JSONObject.parseObject(json);
        formTemplate.setFormId(jsonObject.getInteger("formId"));
        formTemplate.setFormName(jsonObject.getString("formName"));// 表单名称
        formTemplate.setFormDesc(jsonObject.getString("formDesc")); // 表单描述
        JSONObject layoutJson = jsonObject.getJSONObject("layoutJson"); // 格式布局
        JSONArray rows = (JSONArray) layoutJson.get("rows"); // 格式布局中所有行
        for (Object row : rows) {
            JSONArray columns = (JSONArray)((JSONObject) row).get("columns");
            for (int i = 0; i < columns.size(); i++) {
                Object column = columns.get(i);
                if (((JSONObject) column).getString("type").equals("title")) {
                    continue;
                }
                ((JSONObject) column).put("column_name", ((JSONObject) column).getJSONObject("widget").getString("columnName"));
                ((JSONObject) column).remove("widget");
            }
        }
        layoutJson.put("rows", rows);
        formTemplate.setLayoutJson(layoutJson.toString());
        JSONObject operationJson = jsonObject.getJSONObject("operationJson");
        if (operationJson == null || operationJson.size() == 0) {
        	formTemplate.setOperationJson("{}");
        } else {
        	formTemplate.setOperationJson(operationJson.toString());
        }
        formTemplate.setCategoryId(jsonObject.getInteger("categoryId"));// 分类
        return formTemplate;
    }

   

    /**
     * 发布状态类型
     */
    public enum OPERATION {

        sum("总数", "sum"),
        avg("平均值", "avg"),
        count("总个数", "count");

        OPERATION(String name, String value) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
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

	public String getLayoutJson() {
		return layoutJson;
	}

	public void setLayoutJson(String layoutJson) {
		this.layoutJson = layoutJson;
	}

	public String getOperationJson() {
		return operationJson;
	}

	public void setOperationJson(String operationJson) {
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
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
