package com.sunmnet.bigdata.web.security.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单DTO
 */
public class Menu implements Serializable {
    private static final long serialVersionUID = -8212972104723981573L;

    private Integer id; // ID
    private Integer parentId; // 父ID
    private String name; // 名称
    private String url; // URL
    private String iconUrl; // 图标URL
    private String iconSelectedUrl; // 选中状态图标URL
    private Integer seq; // 排序序号
    private List<String> tags; // 菜单标签
    private List<Menu> subMenus; // 子菜单

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

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Menu> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(List<Menu> subMenus) {
        this.subMenus = subMenus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu)) return false;

        Menu menu = (Menu) o;

        return id != null ? id.equals(menu.id) : menu.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", iconSelectedUrl='" + iconSelectedUrl + '\'' +
                ", seq=" + seq +
                ", tags=" + tags +
                ", subMenus=" + subMenus +
                '}';
    }
}
