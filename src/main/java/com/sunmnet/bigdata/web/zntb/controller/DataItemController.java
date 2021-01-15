package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.model.po.DataItem;
import com.sunmnet.bigdata.web.zntb.service.DataItemService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName DataItemController
 * @Description: 数据项管理
 * @Author libin
 * @Date 2018/8/1 10:53
 * @since 1.0.0
 */

@RestController
@RequestMapping("/dataItem")
public class DataItemController extends BaseController {

    @Autowired
    private DataItemService dataItemService;

    final  String SUCC_NAME="success";

    @PostMapping("/save")
    public Object saveDataItem(@RequestParam(value = "json") String json) {
    	DataItem dataItem = DataItem.translateJson(json);
        Pair<Boolean, String> validateResult = dataItem.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }
        //中文名称和英文名称的组合唯一
    	if(!dataItemService.isUnique(dataItem.getChineseField(), dataItem.getEnglishField())){
    		 return buildErrJson("该数据项已存在");
    	}
        Integer userId = authenticationHolder.getAuthenticatedUser().getId();
        dataItem.setUserId(userId);
        dataItemService.save(dataItem);
        return buildSuccJson(SUCC_NAME);
    }
    
    @RequestMapping("/getDataItem")
    public Object getDataItem(@RequestParam(name="id") Integer id){
    	DataItem dataItem = dataItemService.getDataItem(id);
    	return buildSuccJson(dataItem);
    }
    
    @PostMapping("/update")
    public Object update(@RequestParam(name = "json") String json){
    	DataItem dataItem = DataItem.translateJson(json);
    	Pair<Boolean, String> validateResult = dataItem.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }
        //中文名称和英文名称的组合唯一
    	if(!dataItemService.isUnique(dataItem.getChineseField(), dataItem.getEnglishField())){
    		 return buildErrJson("该数据项已存在");
    	}
        dataItem.setUserId(authenticationHolder.getAuthenticatedUser().getId());
        dataItemService.update(dataItem);
        return buildSuccJson(SUCC_NAME);
    }
    
    @RequestMapping("/delete")
    public Object delete(@RequestParam(name="id") Integer id){
    	dataItemService.delete(id);
    	return buildSuccJson(SUCC_NAME);
    }
    
    @RequestMapping("/batchDelete")
    public Object batchDelete(@RequestParam(name="ids") String ids){
    	dataItemService.batchDelete(ids);
    	return buildSuccJson(SUCC_NAME);
    }
    
    @RequestMapping("/getList")
    public Object getList(
    		@RequestParam(name = "chineseField",defaultValue = "") String chineseField,
    		@RequestParam(name = "englishField",defaultValue = "") String englishField,
    		@RequestParam(name = "dataSource",defaultValue = "") String dataSource,
    		@RequestParam(name="pageNum", defaultValue="1") Integer pageNum, 
    		@RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
    	return buildSuccJson(dataItemService.getListByCondition(pageNum, pageSize, chineseField, englishField, dataSource));
    }
    
}