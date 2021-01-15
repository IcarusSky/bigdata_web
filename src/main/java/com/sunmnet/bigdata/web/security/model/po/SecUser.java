package com.sunmnet.bigdata.web.security.model.po;

import com.sunmnet.bigdata.web.security.model.dto.User;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Date;

public class SecUser {
    private Integer id;

    @NotEmpty(message = "用户名不能为空")
    private String userName;

    private String userPassword;

    private Byte accountType;

    @NotEmpty(message = "账户编码不能为空")
    private String accountCode;

    @NotEmpty(message = "姓名不能为空")
    private String name;

    private Byte sex;

    private String cellPhone;

    private Date createTime;

    private Date updateTime;

    public User convertToDTO() {
        User dto = new User(userName, userPassword, true, true, true, true, AuthorityUtils.NO_AUTHORITIES);
        dto.setId(id);
        dto.setAccountCode(accountCode);
        dto.setName(name);
        dto.setSex(sex);
        return dto;
    }

    /**
     * 用户账户类型
     */
    public enum AccountType {
        UNKNOWN("未知", (byte) 0), STUDENT("学生", (byte) 1), TEACHER("老师", (byte) 2);

        AccountType(String name, Byte value) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private Byte value;

        public String getName() {
            return name;
        }

        public Byte getValue() {
            return value;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Byte getAccountType() {
        return accountType;
    }

    public void setAccountType(Byte accountType) {
        this.accountType = accountType;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getSex() {
        return sex;
    }

    public void setSex(Byte sex) {
        this.sex = sex;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
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

    @Override
    public String toString() {
        return "SecUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", accountType=" + accountType +
                ", accountCode='" + accountCode + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", cellPhone='" + cellPhone + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}