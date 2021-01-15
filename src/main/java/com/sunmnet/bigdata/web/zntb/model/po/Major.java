package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class Major {

    private String code;
    private String name;
    private String academyCode;
    private String academyName;

    public static Major translateJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        Major major = new Major();
        major.setCode(jsonObject.getString("code"));
        major.setName(jsonObject.getString("name"));
        major.setAcademyCode(jsonObject.getString("academyCode"));
        major.setAcademyName(jsonObject.getString("academyName"));
        return major;
    }

    public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(code)) {
            return Pair.of(false, "专业编码不能为空");
        }

        if (StringUtils.isEmpty(name)) {
            return Pair.of(false, "专业名称不能为空");
        }
        if (StringUtils.isEmpty(academyCode)) {
            return Pair.of(false, "院系编码不能为空");
        }
        if (StringUtils.isEmpty(academyName)) {
            return Pair.of(false, "院系名称不能为空");
        }
        return Pair.of(true, "成功");
    }


    public Major() {
    }

    public Major(String code, String name, String academyCode, String academyName) {
        this.code = code;
        this.name = name;
        this.academyCode = academyCode;
        this.academyName = academyName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcademyCode() {
        return academyCode;
    }

    public void setAcademyCode(String academyCode) {
        this.academyCode = academyCode;
    }

    public String getAcademyName() {
        return academyName;
    }

    public void setAcademyName(String academyName) {
        this.academyName = academyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Major major = (Major) o;

        return new EqualsBuilder()
                .append(code, major.code)
                .append(name, major.name)
                .append(academyCode, major.academyCode)
                .append(academyName, major.academyName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(name)
                .append(academyCode)
                .append(academyName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Major{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", academyCode='" + academyCode + '\'' +
                ", academyName='" + academyName + '\'' +
                '}';
    }
}