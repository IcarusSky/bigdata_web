package com.sunmnet.bigdata.web.zntb.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.sunmnet.bigdata.web.core.util.DateUtils;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardDatasource;

import javax.annotation.Nullable;
import java.util.Map;

public class ViewDashboardDatasource {

    private Integer id;
    private Integer userId;
    private String name;
    private String type;
    private Map<String, Object> config;
    private boolean edit = true;
    private boolean delete = true;
    private boolean own = true;
    private String createTime;
    private String updateTime;

    @SuppressWarnings("rawtypes")
	public static final Function TO = new Function<DashboardDatasource, ViewDashboardDatasource>() {
        @Nullable
        @Override
        public ViewDashboardDatasource apply(@Nullable DashboardDatasource input) {
            return new ViewDashboardDatasource(input);
        }
    };

    private ViewDashboardDatasource(DashboardDatasource datasource) {
        this.id = datasource.getId();
        this.userId = datasource.getUserId();
        this.name = datasource.getName();
        this.type = datasource.getType();
        this.config = JSONObject.parseObject(datasource.getConfig());
        this.createTime = DateUtils.formatDateTime(datasource.getCreateTime());
        this.updateTime = DateUtils.formatDateTime(datasource.getUpdateTime());
    }

    public boolean getOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
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

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
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

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViewDashboardDatasource)) return false;

        ViewDashboardDatasource that = (ViewDashboardDatasource) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ViewDashboardDatasource{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", config=" + config +
                ", edit=" + edit +
                ", delete=" + delete +
                ", own=" + own +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
