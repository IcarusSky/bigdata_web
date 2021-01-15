package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.model.po.StandardSet;
import com.sunmnet.bigdata.web.zntb.service.StandardService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName StandardController
 * @Description: 标准设置
 * @Author wanggq
 * @Date 2018/6/10 10:53
 * @since 1.0.0
 */

@RestController
@RequestMapping("/standard")
public class StandardController extends BaseController {

    @Autowired
    private StandardService standardService;

    final  String SUCC_NAME="success";

    @PostMapping("/save")
    public Object saveStandard(@RequestParam(value = "json") String json) {
        StandardSet standard = StandardSet.translateJson(json);

        Pair<Boolean, String> validateResult = standard.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        standardService.save(standard);
        return buildSuccJson(SUCC_NAME);
    }

    @PostMapping("/update")
    public Object update(@RequestParam(value = "json") String json) {
        StandardSet standard = StandardSet.translateJson(json);

        Pair<Boolean, String> validateResult = standard.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        standardService.update(standard);
        return buildSuccJson(SUCC_NAME);
    }

    @PostMapping("/delete")
    public Object delete(@RequestParam(value = "id") Integer id) {
        standardService.delete(id);
        return buildSuccJson(SUCC_NAME);

    }

    @PostMapping("/batchDelete")
    public Object batchDelete(@RequestParam(value = "ids") String ids) {
        if (ids.contains(",")) {

            standardService.batchDelete(ids);
        } else {
            standardService.delete(Integer.valueOf(ids));
        }
        return buildSuccJson(SUCC_NAME);
    }

    @ResponseBody
    @PostMapping("/getList")
    public Object getList(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return buildSuccJson(standardService.getstandardList(pageNum, pageSize));
    }


    @PostMapping("/getListByCondition")
    public Object getListByCondition(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                     @RequestParam("standardName") String standardName, @RequestParam("standardValue") String standardValue) {
        if ("".equals(standardName) & "".equals(standardValue)) {
            return buildSuccJson(standardService.getstandardList(pageNum, pageSize));
        } else {
            return buildSuccJson(standardService.getListByCondition(pageNum, pageSize, standardName, standardValue));
        }
    }

    @ResponseBody
    @PostMapping("/getListByCategoryCondition")
    public Object getListByCategoryCondition(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                             @RequestParam(value = "categoryId") int categoryId,
                                             @RequestParam("standardName") String standardName,
                                             @RequestParam("standardValue") String standardValue) {
        if ("".equals(standardName) & "".equals(standardValue)) {
            return buildSuccJson(standardService.getListByCategoryId(pageNum, pageSize, categoryId));
        } else {
            return buildSuccJson(standardService.getListByCategoryCondition(pageNum, pageSize, categoryId, standardName, standardValue));
        }
    }
}