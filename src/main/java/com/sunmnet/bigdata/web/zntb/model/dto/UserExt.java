package com.sunmnet.bigdata.web.zntb.model.dto;

import java.io.Serializable;

/**
 * 用户扩展信息DTO
 */
public class UserExt implements Serializable {
    private static final long serialVersionUID = 5405494426123342123L;
    private Integer userId; // 关联用户ID
    private Integer accountType; // 用户类型，0：未知、1：学生、2：老师
    private String accountCode; // 用户编码，如：学号、教工号等
    private String academyCode; // 学院编码
    private String majorCode; // 专业编码
    private Integer departmentId; // 部门ID
    private Integer positionId; // 职位ID
    private String createTime; // 创建时间

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAcademyCode() {
        return academyCode;
    }

    public void setAcademyCode(String academyCode) {
        this.academyCode = academyCode;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserExt)) return false;

        UserExt userExt = (UserExt) o;

        return userId != null ? userId.equals(userExt.userId) : userExt.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserExt{" +
                "userId=" + userId +
                ", accountType=" + accountType +
                ", accountCode='" + accountCode + '\'' +
                ", academyCode='" + academyCode + '\'' +
                ", majorCode='" + majorCode + '\'' +
                ", departmentId=" + departmentId +
                ", positionId=" + positionId +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
