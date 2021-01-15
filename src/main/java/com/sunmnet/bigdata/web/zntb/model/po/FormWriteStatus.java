package com.sunmnet.bigdata.web.zntb.model.po;

import java.util.Date;

public class FormWriteStatus {

    private Integer id;
    private Integer formId;
    private Integer userId;
    private Integer dataId;
    private Date writeDate;
    private Integer auditorStatus;
    private String auditorDesc;

    private String writerName;
    private String writerDetpId;  //部门编号
    private String writerDetpName;//部门名称

    public static String auditorStatusKey = "cfa50195c4a5aae53caff2209e527d02";

    /**
     * 审核状态类型
     */
    public enum AUDITORSTATUS {

        SAVE("已保存", 0),
        UNAUDITOR("已提交", 1),
        AUDITORNO("审核未通过", 2),
        REAUDITOR("重新提交审核", 3),
        AUDITORY("审核通过", 4);

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

    public String getAuditorDesc() {
        return auditorDesc;
    }

    public void setAuditorDesc(String auditorDesc) {
        this.auditorDesc = auditorDesc;
    }

    public Integer getAuditorStatus() {

        return auditorStatus;
    }

    public void setAuditorStatus(Integer auditorStatus) {
        this.auditorStatus = auditorStatus;
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

    public Date getWriteDate() {
        return writeDate;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public void setWriteDate(Date writeDate) {
        this.writeDate = writeDate;
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
