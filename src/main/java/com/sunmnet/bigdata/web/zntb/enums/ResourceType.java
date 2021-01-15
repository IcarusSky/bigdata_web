package com.sunmnet.bigdata.web.zntb.enums;

/**
 * 资源类型
 */
public enum ResourceType {
    DATASOURCE("数据源", "datasource"),
    DATASET("数据集", "dataset"),
    WIDGET("页面组件", "widget"),
    BOARD("看板", "board");

    ResourceType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
