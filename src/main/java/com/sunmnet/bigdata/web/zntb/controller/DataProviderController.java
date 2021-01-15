package com.sunmnet.bigdata.web.zntb.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.enums.ResourceType;
import com.sunmnet.bigdata.web.zntb.service.DataProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/provider")
public class DataProviderController extends BaseController {

    @Autowired
    private DataProviderService dataProviderService;

    @RequestMapping(value = "/test")
    public Object test(@RequestParam(name = "datasource", required = false) String datasource) {
        dataProviderService.test(JSONObject.parseObject(datasource), null);
        return buildSuccJson();
    }

    @RequestMapping(value = "/getDatasourceParams")
    public Object getDatasourceParams(@RequestParam(name = "type") String type) {
        return buildSuccJson(dataProviderService.getDatasourceParams(type));
    }

    @RequestMapping(value = "/getProviderList")
    public Object getProviderList() {
        return buildSuccJson(dataProviderService.getProviderList());
    }

    @RequestMapping(value = "/getConfigParams")
    public Object getConfigParams(@RequestParam(name = "type") String type, @RequestParam(name = "page") String page) {
        return buildSuccJson(dataProviderService.getQueryParams(type, page));
    }

    @RequestMapping(value = "/getColumns")
    public Object getColumns(@RequestParam(name = "datasourceId", required = false) Integer datasourceId,
                             @RequestParam(name = "query", required = false, defaultValue = "{}") String query) {
        Map<String, String> queryMap = Maps.transformValues(JSONObject.parseObject(query), Functions.toStringFunction());
        String[] columns = dataProviderService.getDatasetColumns(datasourceId, queryMap);
        return buildSuccJson(columns);
    }

    @RequestMapping(value = "/checkSameNameRes")
    public Object checkSameNameRes(@RequestParam(name = "resName") String resName,
                                   @RequestParam(name = "resType") ResourceType resType,
                                   @RequestParam(name = "excludeResId", required = false) Integer excludeResId) {
        return buildSuccJson(dataProviderService.checkSameNameRes(resName, resType, excludeResId,
                authenticationHolder.getAuthenticatedUser().getId()) ? "1" : "0");
    }
}
