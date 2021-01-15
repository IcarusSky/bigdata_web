package com.sunmnet.bigdata.web.zntb.enums;

/**
 * 文件类型
 */
public enum FileType {
    /**
     * 微软Excel，.xls后缀
     */
    MS_EXCEL_XLS("application/vnd.ms-excel"),

    /**
     * 微软Excel，.xlsx后缀
     */
    MS_EXCEL_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private String contentType;

    FileType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
