package com.sunmnet.bigdata.web.security.model.dto;

public class UserQuery {
    private String username;
    private String name;
    private String accountCode;
    private Integer roleId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "UserQuery{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", accountCode='" + accountCode + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
