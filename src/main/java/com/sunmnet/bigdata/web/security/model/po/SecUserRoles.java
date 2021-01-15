package com.sunmnet.bigdata.web.security.model.po;

public class SecUserRoles {
    private Integer userId; // 用户ID
    private String roles; // 逗号分隔角色名称

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}