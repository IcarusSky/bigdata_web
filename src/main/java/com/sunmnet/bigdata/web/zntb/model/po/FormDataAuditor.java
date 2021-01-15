package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

import static com.sunmnet.bigdata.web.zntb.model.po.FormDataAuditor.auditorType.ACCOUNTCODE;
import static com.sunmnet.bigdata.web.zntb.model.po.FormDataAuditor.auditorType.ROLE;

public class FormDataAuditor {

    private Integer id;
    private Integer formId;
    private String auditorType;
    private String auditorId;
    private Integer accountType;


    private String name;
    private Integer leavl;

    public static List<FormDataAuditor> translateJson(String json) throws Exception {
        List<FormDataAuditor> list = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONObject formauditor = jsonObject.getJSONObject("formDataAuditor");
        JSONArray auditors = (JSONArray) formauditor.get("auditors");
        for (Object auditor : auditors) {
            String auditorType = ((JSONObject) auditor).getString("auditorType");
            String auditorId = ((JSONObject) auditor).getString("auditorId");
            if (StringUtils.isBlank(auditorId)) {
            	throw new ServerException("上送报文中存在auditorId为空的错误情况");
            }
            Integer accountType = ((JSONObject) auditor).getInteger("accountType");
            if (auditorType.equals(ACCOUNTCODE.getValue()) || auditorType.equals(ROLE.getValue())) {
                accountType = 0;
            }
            list.add(new FormDataAuditor(auditorType, auditorId, accountType));
        }
        return list;
    }

    public FormDataAuditor() {
    }

    public FormDataAuditor(String auditorType, String auditorId, Integer accountType) {
        this.auditorType = auditorType;
        this.auditorId = auditorId;
        this.accountType = accountType;
    }

    /**
     * 填写人类型
     */
    public enum auditorType {

        ACCOUNTCODE("学号/教工号", "account_code"),
        ACADEMY("学院", "academy"),
        MAJOR("专业", "major"),
        DEPARTMENT("部门", "department"),
        POSITION("职位", "position"),
        ROLE("角色", "role");

        auditorType(String name, String value) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getAuditorType() {
        return auditorType;
    }

    public void setAuditorType(String auditorType) {
        this.auditorType = auditorType;
    }

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLeavl() {
        return leavl;
    }

    public void setLeavl(Integer leavl) {
        this.leavl = leavl;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }
}
