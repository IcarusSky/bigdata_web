package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class StandardSet {

    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String standardName;
    private Integer standardType;
    private String standardValueJson;
    private String createTime;
    private String updateTime;

    public static StandardSet translateJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        StandardSet standardSet = new StandardSet();
        if(jsonObject.containsKey("id")){
            standardSet.setId(jsonObject.getInteger("id"));
        }
        standardSet.setCategoryId(jsonObject.getInteger("categoryId"));
        standardSet.setStandardName(jsonObject.getString("standardName"));
        standardSet.setStandardType(jsonObject.getInteger("standardType"));
        standardSet.setStandardValueJson(jsonObject.getString("standardValueJson"));
        return  standardSet;
    }

    public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(standardName)) {
            return Pair.of(false, "标准名称不能为空");
        }
        if (standardName.length() > 20) {
            return Pair.of(false, "标准名称不能超过30个字");
        }
        if (StringUtils.isEmpty(String.valueOf(categoryId))) {
            return Pair.of(false, "分类不能为空");
        }

        if (StringUtils.isEmpty(standardValueJson)) {
            return Pair.of(false, "设置标准的值不能为空");
        }
        return Pair.of(true, "成功");
    }

    public StandardSet() {
    }

    public StandardSet(Integer id, Integer categoryId, String categoryName, String standardName, Integer standardType, String standardValueJson, String createTime, String updateTime) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.standardName = standardName;
        this.standardType = standardType;
        this.standardValueJson = standardValueJson;
        this.createTime = createTime;
        this.updateTime = updateTime;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public Integer getStandardType() {
        return standardType;
    }

    public void setStandardType(Integer standardType) {
        this.standardType = standardType;
    }

    public String getStandardValueJson() {
        return standardValueJson;
    }

    public void setStandardValueJson(String standardValueJson) {
        this.standardValueJson = standardValueJson;
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

        if (o == null || getClass() != o.getClass()) return false;

        StandardSet that = (StandardSet) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(categoryId, that.categoryId)
                .append(categoryName, that.categoryName)
                .append(standardName, that.standardName)
                .append(standardType, that.standardType)
                .append(standardValueJson, that.standardValueJson)
                .append(createTime, that.createTime)
                .append(updateTime, that.updateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(categoryId)
                .append(categoryName)
                .append(standardName)
                .append(standardType)
                .append(standardValueJson)
                .append(createTime)
                .append(updateTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "StandardSet{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", standardName='" + standardName + '\'' +
                ", standardType=" + standardType +
                ", standardValueJson='" + standardValueJson + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}