package com.sunmnet.bigdata.web.zntb.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.sunmnet.bigdata.web.core.util.DateUtils;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardDataset;

import javax.annotation.Nullable;
import java.util.Map;

public class ViewDashboardDataset {
    private Integer id;
    private Integer userId;
    private String name;
    private String type;
    private Integer datasourceId;
    private Integer categoryId;
    private String categoryName;
    private Map<String, Object> data;
    private String createTime;
    private String updateTime;

    @SuppressWarnings("rawtypes")
	public static final Function TO = new Function<DashboardDataset, ViewDashboardDataset>() {
        @Nullable
        @Override
        public ViewDashboardDataset apply(@Nullable DashboardDataset input) {
            return new ViewDashboardDataset(input);
        }
    };

    public ViewDashboardDataset(DashboardDataset dataset) {
        this.id = dataset.getId();
        this.userId = dataset.getUserId();
        this.name = dataset.getName();
        this.type = dataset.getType();
        this.categoryId = dataset.getCategoryId();
        this.categoryName = dataset.getCategoryName();
        this.createTime = DateUtils.formatDateTime(dataset.getCreateTime());
        this.updateTime = DateUtils.formatDateTime(dataset.getUpdateTime());
        this.data = JSONObject.parseObject(dataset.getData());
        this.datasourceId = dataset.getDatasourceId();
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViewDashboardDataset)) return false;

        ViewDashboardDataset that = (ViewDashboardDataset) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ViewDashboardDataset{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", datasourceId=" + datasourceId +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", data=" + data +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
