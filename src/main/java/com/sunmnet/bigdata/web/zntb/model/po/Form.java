package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmnet.bigdata.web.core.util.DateUtils;

import java.util.Date;

public class Form {

    private Integer formId;
    private String formName;
    private String formDesc;
    private String dataTable;
    private String layoutJson;
    private String operationJson;
    private Integer auditorId;
    private Integer dataAuditorId;
    private Integer auditorStatus;
    private String auditorDesc;
    private Integer publishStatus;
    private Integer required;
    private Integer categoryId;
    private Integer userId;
    private Date beginTime;
    private Date endTime;
    private Date createTime;
    private Date updateTime;

    private String categoryName;
    private String userName;
    private Integer writeStatus;


    public static Form translateJson(String json) {
        Form form = new Form();
        JSONObject jsonObject = JSONObject.parseObject(json);
        form.setFormId(jsonObject.getInteger("formId"));
        form.setFormName(jsonObject.getString("formName"));// 表单名称
        form.setFormDesc(jsonObject.getString("formDesc")); // 表单描述
        form.setDataTable(jsonObject.getString("dataTable"));// 数据表

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
        form.setLayoutJson(layoutJson.toString());
        JSONObject operationJson = jsonObject.getJSONObject("operationJson");
        if (operationJson == null || operationJson.size() == 0) {
            form.setOperationJson("{}");
        } else {
            form.setOperationJson(operationJson.toString());
        }



        form.setDataAuditorId(jsonObject.getInteger("dataAuditorId"));
        form.setAuditorId(jsonObject.getInteger("auditorId"));
        form.setRequired(jsonObject.getInteger("required"));
        form.setCategoryId(jsonObject.getInteger("categoryId"));
        form.setBeginTime(DateUtils.parseDateTime(jsonObject.getString("beginTime")));
        form.setEndTime(DateUtils.parseDateTime(jsonObject.getString("endTime")));


        form.setAuditorStatus(AUDITORSTATUS.UNAUDITOR.getValue());
        form.setPublishStatus(PUBLISHSTATUS.UNPUBLISHED.getValue());
        form.setAuditorDesc("");
        return form;
    }

    /**
     * 审核状态类型
     */
    public enum AUDITORSTATUS {

    	SAVE("待提交", 4), //Create By 李斌  2019-08-02
        //UNAUDITOR("提交审核", 0),
    	UNAUDITOR("提交审核中", 0),//Update By 李斌  2019-08-02
        AUDITORY("审核通过", 1),
        AUDITORNO("审核未通过", 2),
        //REAUDITOR("重新提交审核", 3);
    	REAUDITOR("重新审核中", 3);//Update By 李斌  2019-08-02

        AUDITORSTATUS(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private Integer value;

        public String getName() {
            return name;
        }

        public Integer getValue() {
            return value;
        }
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

    /**
     * 发布状态
     */
    public enum PUBLISHSTATUS {

        UNPUBLISHED("未发布", 0),
        PUBLISHED("已发布", 1);

        PUBLISHSTATUS(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private Integer value;

        public String getName() {
            return name;
        }

        public Integer getValue() {
            return value;
        }
    }

    /**
     * 控件类型
     */
    public enum WIDGETTYPE {

        TITLE("标题", "title"),
        INPUT("单行输入", "input"),
        TEXTAREA("多行输入", "textarea"),
        RADIO("单选按钮", "radio"),
        CHECKBOX("多选按钮", "checkbox"),
        SELECT("下拉菜单", "select"),
        PHONE("手机号", "phone"),
        EMAIL("邮箱", "email"),
        IDCARD("身份证号", "idcard"),
        FILE("附件", "file"),
        DATE("时间控件", "date");

        WIDGETTYPE(String name, String value) {
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

    public String getDataTable() {
        return dataTable;
    }

    public void setDataTable(String dataTable) {
        this.dataTable = dataTable;
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

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getWriteStatus() {
        return writeStatus;
    }

    public String getAuditorDesc() {
        return auditorDesc;
    }

    public void setAuditorDesc(String auditorDesc) {
        this.auditorDesc = auditorDesc;
    }

    public Integer getDataAuditorId() {

        return dataAuditorId;
    }

    public void setDataAuditorId(Integer dataAuditorId) {
        this.dataAuditorId = dataAuditorId;
    }

    public void setWriteStatus(Integer writeStatus) {
        this.writeStatus = writeStatus;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
