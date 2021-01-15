package com.sunmnet.bigdata.web.zntb.model.dto;

import com.google.common.base.Function;
import com.sunmnet.bigdata.web.core.util.DateUtils;
import com.sunmnet.bigdata.web.zntb.model.po.FormWriteStatus;

import javax.annotation.Nullable;

public class ViewFormWriteStatus {

    private Integer id;
    private Integer formId;
    private Integer userId;
    private Integer dataId;
    private String writeDate;
    private Integer auditorStatus;
    private String auditorDesc;

    private String writerName;
    private String writerDetpId;  //部门编号
    private String writerDetpName;//部门名称
    @SuppressWarnings("rawtypes")
	public static final Function TO = new Function<FormWriteStatus, ViewFormWriteStatus>() {
        @Nullable
        @Override
        public ViewFormWriteStatus apply(@Nullable FormWriteStatus input) {
            return new ViewFormWriteStatus(input);
        }
    };

    public ViewFormWriteStatus(FormWriteStatus input) {
        this.id = input.getId();
        this.formId = input.getFormId();
        this.userId = input.getUserId();
        this.dataId = input.getDataId();
        this.auditorDesc = input.getAuditorDesc();
        this.auditorStatus = input.getAuditorStatus();
        this.writeDate = DateUtils.formatDateTime(input.getWriteDate());
        this.writerName = input.getWriterName();
        this.writerDetpId = input.getWriterDetpId();
        this.writerDetpName = input.getWriterDetpName();

    }

    public Integer getAuditorStatus() {
        return auditorStatus;
    }

    public void setAuditorStatus(Integer auditorStatus) {
        this.auditorStatus = auditorStatus;
    }

    public String getAuditorDesc() {
        return auditorDesc;
    }

    public void setAuditorDesc(String auditorDesc) {
        this.auditorDesc = auditorDesc;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getWriterDetpId() {
        return writerDetpId;
    }

    public void setWriterDetpId(String writerDetpId) {
        this.writerDetpId = writerDetpId;
    }

    public String getWriterDetpName() {
        return writerDetpName;
    }

    public void setWriterDetpName(String writerDetpName) {
        this.writerDetpName = writerDetpName;
    }
}
