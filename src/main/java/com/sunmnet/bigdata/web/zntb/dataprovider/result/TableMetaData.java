package com.sunmnet.bigdata.web.zntb.dataprovider.result;

/**
 * JDBC表信息
 */
public class TableMetaData {
    private String name; // 名称
    private String type; // 类型，如：TABLE、VIEW、SYSTEM TABLE等
    private String database; // 所属数据库
    private String remarks; // 注释

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

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "TableMetaData{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", database='" + database + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
