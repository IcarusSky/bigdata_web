package com.sunmnet.bigdata.web.zntb.dataprovider.result;

import java.sql.JDBCType;

/**
 * JDBC列信息
 */
public class ColumnMetaData {
    private String name; // 名称
    private JDBCType type; // 类型
    private String database; // 所属数据库
    private String table; // 所属表
    private String remarks; // 注释

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JDBCType getType() {
        return type;
    }

    public void setType(JDBCType type) {
        this.type = type;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "ColumnMetaData{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
