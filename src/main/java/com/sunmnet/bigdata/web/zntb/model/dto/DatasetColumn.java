package com.sunmnet.bigdata.web.zntb.model.dto;

public class DatasetColumn {
    private String name;
    private String alias;

    public DatasetColumn() {
    }

    public DatasetColumn(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatasetColumn)) return false;

        DatasetColumn that = (DatasetColumn) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DatasetColumn{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
