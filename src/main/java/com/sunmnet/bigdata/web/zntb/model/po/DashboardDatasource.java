package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Timestamp;

public class DashboardDatasource {

    private Integer id;
    private Integer userId;
    private String name;
    private String type;
    private String config;
    private Timestamp createTime;
    private Timestamp updateTime;

    public static DashboardDatasource translateJson(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardDatasource datasource = new DashboardDatasource();
        datasource.setUserId(jsonObject.getInteger("userId"));
        datasource.setId(jsonObject.getInteger("id"));
        datasource.setName(jsonObject.getString("name"));
        datasource.setType(jsonObject.getString("type"));
        datasource.setConfig(jsonObject.getString("config"));
        return datasource;
    }

    public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(name)) {
            return Pair.of(false, "数据源名称不能为空");
        }
        if (name.length() > 20) {
            return Pair.of(false, "数据源名称不能超过20个字");
        }
        return Pair.of(true, "成功");
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

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
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

    public void setDatasource_id(Integer datasource_id) {
        this.id = datasource_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DashboardDatasource)) return false;

        DashboardDatasource that = (DashboardDatasource) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DashboardDatasource{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", config='" + config + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
