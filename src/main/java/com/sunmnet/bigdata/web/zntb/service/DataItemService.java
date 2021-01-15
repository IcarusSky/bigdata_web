package com.sunmnet.bigdata.web.zntb.service;

import java.util.Collections;import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.zntb.model.po.DataItem;
import com.sunmnet.bigdata.web.zntb.persistent.DataItemDao;

@Service
public class DataItemService {
	@Autowired 
	private DataItemDao dataItemDao;
	
	public void save(DataItem dataItem){
		// 该部分判断后续根据业务需求再做调整
		if (dataItem.getUserId() == null) {
			dataItem.setUserId(0);
		}
		if(StringUtils.isEmpty(dataItem.getDataSource())){
			dataItem.setDataSource("智能填报系统");
		}
		dataItemDao.save(dataItem);
	}
	
	public DataItem getDataItem(Integer id){
		return dataItemDao.getDataItem(id);
	}
	
	public void update(DataItem dataItem){
		if (dataItem.getUserId() == null) {
			dataItem.setUserId(0);
		}
		if(StringUtils.isEmpty(dataItem.getDataSource())){
			dataItem.setDataSource("智能填报系统");
		}
		dataItemDao.update(dataItem);
	}
	
	public void delete(Integer id){
		dataItemDao.delete(id);
	}
	
	public void batchDelete(String ids) {
        String[] strArray = ids.split(",");

        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.valueOf(strArray[i]);
        }
        dataItemDao.batchDelete(intArray);
    }
	
	public PageResult<DataItem> getDataItemList(int pageNum, int pageSize) {

        PageInfo<DataItem> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> dataItemDao.getDataItemList());

        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : pageInfo.getList());
    }
	
	public PageResult<DataItem> getListByCondition(int pageNum, int pageSize, String chineseField, String englishField, String dataSource) {

        PageInfo<DataItem> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> dataItemDao.getListByCondition(chineseField, englishField, dataSource));

        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : pageInfo.getList());
    }
	
	//判断字段中文名称和字段英文名称的组合是否唯一
	public boolean isUnique(String chineseField, String englishField){
		boolean flag = false;
		List<DataItem> dataItems = dataItemDao.getListByCondition2(chineseField, englishField);
		if(CollectionUtils.isEmpty(dataItems)){
			flag = true;
		}
		return flag;
	}
	

}