package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.List;

public class Academy implements Serializable {
	
	private static final long serialVersionUID = -535133877193884713L;
	
	private String code; // 编码
    private String name; // 名称

    private List<Major> majorList;

    public static Academy translateJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        Academy academy = new Academy();
        academy.setCode(jsonObject.getString("code"));
        academy.setName(jsonObject.getString("name"));
        return academy;
    }

    public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(code)) {
            return Pair.of(false, "院系编码不能为空");
        }

        if (StringUtils.isEmpty(name)) {
            return Pair.of(false, "院系名称不能为空");
        }
        return Pair.of(true, "成功");
    }

    public Academy() {
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

    public List<Major> getMajorList() {
        return majorList;
    }

    public void setMajorList(List<Major> majorList) {
        this.majorList = majorList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Academy academy = (Academy) o;

        return new EqualsBuilder()
                .append(code, academy.code)
                .append(name, academy.name)
                .append(majorList, academy.majorList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(name)
                .append(majorList)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Academy{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", majorList=" + majorList +
                '}';
    }
}
