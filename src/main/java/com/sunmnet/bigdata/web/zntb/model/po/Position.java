package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Position {

    private Integer id;
    private Integer depId;
    private String name;
    private Integer userId;

    public static Position translateJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        Position pos = new Position();

        if(jsonObject.containsKey("id")){
            pos.setId(jsonObject.getInteger("id"));
        }
        pos.setDepId(jsonObject.getInteger("depId"));
        pos.setName(jsonObject.getString("name"));
        return pos;
    }

    public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(name)) {
            return Pair.of(false, "职位名称不能为空");
        }
        return Pair.of(true, "成功");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}