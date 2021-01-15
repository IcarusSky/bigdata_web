package com.sunmnet.bigdata.web.zntb.model.po;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DataItem {
	private Integer id;

	private String chineseField;

	private String englishField;
	
	private String dataSource;

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	private Integer userId;
	
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private Date createTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private Date updateTime;
	
	private String userName;

	public static DataItem translateJson(String json) {
		JSONObject jsonObject = JSON.parseObject(json);
		DataItem dataItem = new DataItem();
		if(jsonObject.containsKey("id")){
			dataItem.setId(jsonObject.getInteger("id"));
		}
		dataItem.setChineseField(jsonObject.getString("chineseField"));
		dataItem.setEnglishField(jsonObject.getString("englishField"));
		dataItem.setDataSource(jsonObject.getString("dataSource"));
		return  dataItem;
	}
	
	public Pair<Boolean, String> validate() {
        if (StringUtils.isEmpty(chineseField)) {
            return Pair.of(false, "字段中文名称不能为空");
        }
        if (chineseField.length() > 100) {
            return Pair.of(false, "字段中文名称长度不能大于100");
        }
        if (StringUtils.isEmpty(englishField)) {
            return Pair.of(false, "字段英文名称不能为空");
        }
        if (englishField.length() > 100) {
            return Pair.of(false, "字段英文名称长度不能大于100");
        }
        
        return Pair.of(true, "成功");
    }

	public DataItem() {
		
	}


	public DataItem(Integer id, String chineseField, String englishField, String dataSource, Integer userId,
			Date createTime, Date updateTime, String userName) {
		super();
		this.id = id;
		this.chineseField = chineseField;
		this.englishField = englishField;
		this.dataSource = dataSource;
		this.userId = userId;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.userName = userName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChineseField() {
		return chineseField;
	}

	public void setChineseField(String chineseField) {
		this.chineseField = chineseField == null ? null : chineseField.trim();
	}

	public String getEnglishField() {
		return englishField;
	}

	public void setEnglishField(String englishField) {
		this.englishField = englishField == null ? null : englishField.trim();
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}