package com.sunmnet.bigdata.web.zntb.model.po;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Department {

    private Integer id;
    private String name;
    private Integer userId;
    private Integer parentId;
    private int isParent = -1;
    private List<Department> depList;
    private List<Position> posList;

    public static Department translateJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        Department dep = new Department();
        if(jsonObject.containsKey("id"))
            dep.setId(jsonObject.getInteger("id"));
        dep.setName(jsonObject.getString("name"));
        dep.setIsParent(jsonObject.getInteger("isSon"));
        if((int)jsonObject.getInteger("isSon") != -1)
            dep.setDepList(jsonObject.getJSONArray("depList").toJavaList(Department.class));
        return dep;
    }

    public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(name)) {
            return Pair.of(false, "部门名称不能为空");
        }
        return Pair.of(true, "成功");
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public int getIsParent() {
        return isParent;
    }

    public void setIsParent(int isParent) {
        this.isParent = isParent;
    }

    public List<Department> getDepList() {
        return depList;
    }

    public void setDepList(List<Department> depList) {
        this.depList = depList;
    }

    public List<Position> getPosList() {
        return posList;
    }

    public void setPosList(List<Position> posList) {
        this.posList = posList;
    }
}