package com.sunmnet.bigdata.web.zntb.persistent;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.sunmnet.bigdata.web.zntb.model.po.DataItem;


@Mapper
public interface DataItemDao {
	
	int save(DataItem dataItem);
	
	DataItem getDataItem(@Param("id")Integer id);
	
	int update(DataItem dataItem);
	
	int delete(@Param("id") Integer id);
	
	int batchDelete(@Param("ids") int[] ids);

	List<DataItem> getDataItemList();
	
	List<DataItem> getListByCondition(@Param("chineseField") String chineseField, 
			@Param("englishField") String englishField, @Param("dataSource") String dataSource);
	
	//根据字段中文名称和字段英文名称组合查询
	List<DataItem> getListByCondition2(@Param("chineseField") String chineseField, 
			@Param("englishField") String englishField);

}