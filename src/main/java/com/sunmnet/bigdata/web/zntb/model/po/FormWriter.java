package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import static com.sunmnet.bigdata.web.zntb.model.po.FormWriter.WriterType.ACCOUNTCODE;
import static com.sunmnet.bigdata.web.zntb.model.po.FormWriter.WriterType.ROLE;

public class FormWriter {

    private Integer id;
    private Integer formId;
    private String writerType;
    private String writerId;
    private Integer accountType;


    private String name;
    private Integer leavl;

    public static List<FormWriter> translateJson(String json) throws Exception {
        List<FormWriter> list = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONObject formWriter = jsonObject.getJSONObject("formWriter");
        JSONArray writers = (JSONArray) formWriter.get("writers");
        for (Object writer : writers) {
            String writerType = ((JSONObject) writer).getString("writerType");
            String writerId = ((JSONObject) writer).getString("writerId");
            if (StringUtils.isBlank(writerId)) {
            	throw new ServerException("上送报文中存在writerId为空的错误情况");
            }
            Integer accountType = ((JSONObject) writer).getInteger("accountType");
            if (writerType.equals(ACCOUNTCODE.getValue()) || writerType.equals(ROLE.getValue())) {
                accountType = 0;
            }
            list.add(new FormWriter(writerType, writerId, accountType));
        }
        return list;
    }

    public FormWriter() {
    }

    public FormWriter(String writerType, String writerId, Integer accountType) {
        this.writerType = writerType;
        this.writerId = writerId;
        this.accountType = accountType;
    }

    /**
     * 填写人类型
     */
    public enum WriterType {

        ACCOUNTCODE("学号/教工号", "account_code"),
        ACADEMY("学院", "academy"),
        MAJOR("专业", "major"),
        DEPARTMENT("部门", "department"),
        POSITION("职位", "position"),
        ROLE("角色", "role");

        WriterType(String name, String value) {
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

    public String getWriterType() {
        return writerType;
    }

    public void setWriterType(String writerType) {
        this.writerType = writerType;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
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
