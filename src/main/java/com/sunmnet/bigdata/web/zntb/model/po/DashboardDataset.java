package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Timestamp;

public class DashboardDataset {

    private Integer id;
    private Integer userId;
    private Integer datasourceId;
    private String name;
    private String type;
    private String data;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer categoryId;
    private String categoryName;

    public DashboardDataset(Integer id, Integer userId, String name) {
        this.setId(id);
        this.setUserId(userId);
        this.setName(name);
    }

    public DashboardDataset() {
    }

    public static DashboardDataset translateJson(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardDataset dataset = new DashboardDataset();
        dataset.setId(jsonObject.getInteger("id"));
        dataset.setDatasourceId(jsonObject.getInteger("datasourceId"));
        dataset.setCategoryId(jsonObject.getInteger("categoryId"));
        dataset.setUserId(jsonObject.getInteger("userId"));
        dataset.setName(jsonObject.getString("name"));
        dataset.setType(jsonObject.getString("type"));
        JSONObject data = jsonObject.getJSONObject("data");
        dataset.setData(data.toJSONString());
        return dataset;
    }

    public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(name)) {
            return Pair.of(false, "数据集名称不能为空");
        }
        if (name.length() > 20) {
            return Pair.of(false, "数据集名称不能超过20个字");
        }
        return Pair.of(true, "成功");
    }

    public enum Type {
        SQL, TABLE;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DashboardDataset)) return false;

        DashboardDataset that = (DashboardDataset) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DashboardDataset{" +
                "id=" + id +
                ", userId=" + userId +
                ", datasourceId=" + datasourceId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
