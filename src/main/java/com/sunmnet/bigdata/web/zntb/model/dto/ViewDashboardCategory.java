package com.sunmnet.bigdata.web.zntb.model.dto;

import com.google.common.base.Function;
import com.sunmnet.bigdata.web.core.util.DateUtils;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardCategory;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;

public class ViewDashboardCategory {
    private String categoryType = "category";
    private Integer id;
    private Integer parentId;
    private Integer userId;
    private String name;
    private String type;
    private String createTime;
    private String updateTime;

    public static ViewDashboardCategory getDefault(){
        ViewDashboardCategory category = new ViewDashboardCategory();
        category.setId(-1);
        category.setParentId(-1);
        category.setName("默认分类");
        return category;
    }



    private static ViewDashboardCategory parseToCategoryDto(DashboardCategory category) {
        ViewDashboardCategory categoryDto = new ViewDashboardCategory();
        BeanUtils.copyProperties(category, categoryDto);
        categoryDto.setCreateTime(DateUtils.formatDateTime(category.getCreateTime()));
        categoryDto.setUpdateTime(DateUtils.formatDateTime(category.getUpdateTime()));
        return categoryDto;
    }

    @SuppressWarnings("rawtypes")
	public static final Function TO = new Function<DashboardCategory, ViewDashboardCategory>() {
        @Nullable
        @Override
        public ViewDashboardCategory apply(@Nullable DashboardCategory input) {
            return parseToCategoryDto(input);
        }
    };

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
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
        if (!(o instanceof ViewDashboardCategory)) return false;

        ViewDashboardCategory that = (ViewDashboardCategory) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ViewDashboardCategory{" +
                "categoryType='" + categoryType + '\'' +
                ", id=" + id +
                ", parentId=" + parentId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
