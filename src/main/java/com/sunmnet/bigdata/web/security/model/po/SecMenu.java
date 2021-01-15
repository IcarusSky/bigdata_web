package com.sunmnet.bigdata.web.security.model.po;

import java.util.Date;

public class SecMenu {
    private Integer id;

    private Integer parentId;

    private Integer seq;

    private String name;

    private String url;

    private String iconUrl;

    private String iconSelectedUrl;

    private Byte status;

    private Byte publishStatus;

    private Byte type;

    private String remark;

    private Date createTime;

    private Date updateTime;

    /**
     * 菜单状态
     */
    public enum Status {
        OPEN("打开", (byte) 1), CLOSE("关闭", (byte) 2);

        Status(String name, Byte value) {
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
     * 菜单发布状态
     */
    public enum PublishStatus {
        PUBLISHED("已发布", (byte) 1), UNPUBLISHED("未发布", (byte) 2);

        PublishStatus(String name, Byte value) {
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
     * 菜单类型
     */
    public enum Type {
        COMMON("普通类型", (byte) 1), BOARD("看板类型", (byte) 2);

        Type(String name, Byte value) {
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconSelectedUrl() {
        return iconSelectedUrl;
    }

    public void setIconSelectedUrl(String iconSelectedUrl) {
        this.iconSelectedUrl = iconSelectedUrl;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Byte publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}