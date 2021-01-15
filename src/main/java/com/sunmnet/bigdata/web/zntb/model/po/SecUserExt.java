package com.sunmnet.bigdata.web.zntb.model.po;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户扩展信息
 */
public class SecUserExt implements Serializable {
    private static final long serialVersionUID = -2573799087338000718L;
    private Integer id; // 自增ID
    private Integer userId; // 关联用户ID
    private String userName;//用户名
    private String name; // 姓名
    private String accountCode; // 账号编码，学号、教工号等
    private String academyCode; // 学院编码
    private String academyName; // 学院名称
    private String majorCode; // 专业编码
    private String majorName; // 专业名称
    private Integer departmentId; // 部门ID
    private String departmentName; // 部门名称
    private Integer positionId; // 职位ID
    private String positionName; // 职位名称
    private Date createTime; // 创建时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAcademyName() {
        return academyName;
    }

    public void setAcademyName(String academyName) {
        this.academyName = academyName;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecUserExt)) return false;

        SecUserExt that = (SecUserExt) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SecUserExt{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", accountCode='" + accountCode + '\'' +
                ", academyCode='" + academyCode + '\'' +
                ", academyName='" + academyName + '\'' +
                ", majorCode='" + majorCode + '\'' +
                ", majorName='" + majorName + '\'' +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", positionId=" + positionId +
                ", positionName='" + positionName + '\'' +
                ", createTime=" + createTime +
                '}';
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
    
    
}
