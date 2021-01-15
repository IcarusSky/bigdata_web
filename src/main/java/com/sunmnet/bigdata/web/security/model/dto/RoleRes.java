package com.sunmnet.bigdata.web.security.model.dto;

import java.util.List;

public class RoleRes {
    private Integer roleId;
    private List<Integer> menu;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public List<Integer> getMenu() {
        return menu;
    }

    public void setMenu(List<Integer> menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return "RoleRes{" +
                "roleId=" + roleId +
                ", menu=" + menu +
                '}';
    }
}
