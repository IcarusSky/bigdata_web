package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Timestamp;

public class DashboardCategory {

    private Integer id;
    private Integer parentId;
    private Integer userId;
    private String name;
    private String type;
    private Timestamp createTime;
    private Timestamp updateTime;

    public static DashboardCategory translateJson(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardCategory category = new DashboardCategory();
        category.setUserId(jsonObject.getInteger("userId"));
        category.setId(jsonObject.getInteger("id"));
        category.setParentId(jsonObject.getInteger("parentId"));
        category.setName(jsonObject.getString("name"));
        category.setType(jsonObject.getString("type"));
        return category;
    }

    public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(name)) {
            return Pair.of(false, "名称不能为空");
        }
        if (name.length() > 20) {
            return Pair.of(false, "名称不能超过20个字");
        }
        return Pair.of(true, "成功");
    }

    public DashboardCategory() {
    }

    public DashboardCategory(Integer id, Integer userId, String name, String type) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    /**
     * 资源类型
     */
    public enum Type {
        DATASET("数据集", "dataset"), FORM("表单组件", "form"), DATA_STANDARD("数据标准", "data_standard");

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DashboardCategory)) return false;

        DashboardCategory that = (DashboardCategory) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DashboardCategory{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
