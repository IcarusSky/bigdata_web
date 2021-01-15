package com.sunmnet.bigdata.web.security.model.po;

public class SecRes {
    private Integer id;

    private Byte ownerType;

    private Integer ownerId;

    private String resType;

    private Long resId;

    private Integer permission;

    /**
     * 资源拥有者类型
     */
    public enum OwnerType {
        USER("用户", (byte) 1), ROLE("角色", (byte) 2);

        OwnerType(String name, Byte value) {
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

    /**
     * 资源类型
     */
    public enum Type {
        DATASOURCE("数据源", "datasource"), DATASET("数据集", "dataset"), WIDGET("页面组件", "widget"), BOARD("看板", "board"), MENU("菜单", "menu");

        Type(String name, String value) {
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

    public enum Permission {
        READ("只读", 1), ALL("读写",  2);

        Permission(String name, Integer value) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(Byte ownerType) {
        this.ownerType = ownerType;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission.getValue();
    }
}