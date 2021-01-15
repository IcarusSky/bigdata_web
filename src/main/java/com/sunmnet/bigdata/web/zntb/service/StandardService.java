package com.sunmnet.bigdata.web.zntb.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.zntb.model.po.StandardSet;
import com.sunmnet.bigdata.web.zntb.persistent.StandardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;

@Service
public class StandardService {


    @Autowired
    private StandardDao standardDao;

    public void save(StandardSet standard) {
        standardDao.save(standard);
    }


    public void update(StandardSet standard) {
        standardDao.update(standard);
    }

    public void delete(Integer id) {
        standardDao.delete(id);
    }

    public void batchDelete(String ids) {
        String[] strArray = ids.split(",");

        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.valueOf(strArray[i]);
        }
        standardDao.batchDelete(intArray);
    }

//    private String setValue(StandardSet set) {
//        StringBuilder builder = new StringBuilder();
//
//        if(! set.getStandardValueJson().contains("options")){
//
//            return set.getStandardValueJson();
//        }
//
//        JSONArray array = JSON.parseObject(set.getStandardValueJson()).getJSONArray("options");
//        if (array.size() > 0) {
//            for (int i = 0; i < array.size(); i++) {
//
//                if (i == array.size()-1) {
//                    builder.append(array.getJSONObject(i).getString("key"));
//                } else {
//                    builder.append(array.getJSONObject(i).getString("key") + ",");
//                }
//            }
//            }
//        return builder.toString();
//    }

    public PageResult<StandardSet> getstandardList(int pageNum, int pageSize) {

        PageInfo<StandardSet> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> standardDao.getStandardList());

        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : pageInfo.getList());
    }

    public PageResult<StandardSet> getListByCondition(int pageNum, int pageSize, String standardName, String standardValue) {

        PageInfo<StandardSet> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> standardDao.getlistByCondition(standardName, standardValue));

        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : pageInfo.getList());
    }

    public PageResult<StandardSet> getListByCategoryId(int pageNum, int pageSize, int categoryId) {

        PageInfo<StandardSet> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> standardDao.getListByCategoryId(categoryId));

        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : pageInfo.getList());
    }

    public PageResult<StandardSet> getListByCategoryCondition(int pageNum, int pageSize, int categoryId, String standardName, String standardValue) {

        PageInfo<StandardSet> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> standardDao.getListByCategoryCondition(categoryId, standardName, standardValue));

        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : pageInfo.getList());
    }

    public int countByCategoryId(Integer categoryId) {
        return standardDao.countByCategoryId(categoryId);
    }
}