package com.sunmnet.bigdata.web.zntb.controller;

import com.alibaba.fastjson.JSONObject;
import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.core.security.userdetails.UserDetails;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewDashboardCategory;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewDashboardDataset;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardCategory;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardDataset;
import com.sunmnet.bigdata.web.zntb.service.CategoryService;
import com.sunmnet.bigdata.web.zntb.service.DatasetService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.sunmnet.bigdata.web.zntb.model.po.DashboardDataset.translateJson;

@RestController
@RequestMapping("/dataset")
public class DatasetController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DatasetService datasetService;

    @RequestMapping(value = "/getAll")
    public Object getAllDataset() {
        return buildSuccJson(datasetService.getDatasetList(authenticationHolder.getAuthenticatedUser().getId()));
    }

    @RequestMapping(value = "/getlist")
    public Object getDatasetList(@RequestParam(name = "categoryId", defaultValue = "-1") int categoryId) {
        UserDetails user = authenticationHolder.getAuthenticatedUser();
        List<ViewDashboardCategory> categoryListByParentId = categoryService.getCategoryListByParentId(user.getId(),
                DashboardCategory.Type.DATASET.getValue(), categoryId);
        List<ViewDashboardDataset> datasetListByCategoryId = datasetService.getDatasetListByCategoryId(user.getId(), categoryId);
        ArrayList<Object> objects = new ArrayList<>();
        objects.addAll(categoryListByParentId);
        objects.addAll(datasetListByCategoryId);
        return buildSuccJson(objects);
    }

    @RequestMapping(value = "/delete")
    public Object deleteDataset(@RequestParam(name = "id") Integer id) {
        datasetService.delete(id);
        return buildSuccJson();
    }

    @RequestMapping(value = "/save")
    public Object saveNewDataset(@RequestParam(name = "json") String json) {
        DashboardDataset dashboardDataset = translateJson(json);
        dashboardDataset.setUserId(authenticationHolder.getAuthenticatedUser().getId());

        Pair<Boolean, String> validateResult = dashboardDataset.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        return buildSuccJson(datasetService.save(dashboardDataset));
    }

    @RequestMapping(value = "/update")
    public Object updateDataset(@RequestParam(name = "json") String json) {
        DashboardDataset dashboardDataset = translateJson(json);
        dashboardDataset.setUpdateTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        Pair<Boolean, String> validateResult = dashboardDataset.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        datasetService.update(dashboardDataset);
        return buildSuccJson();
    }

    @RequestMapping(value = "/info")
    public Object info(@RequestParam(name = "id") Integer id) {
        ViewDashboardDataset dataset = datasetService.info(id);
        return buildSuccJson(dataset);
    }

    @RequestMapping(value = "/copy")
    public Object copy(@RequestParam(name = "id") Integer id, @RequestParam(name = "name") String name) {
        ViewDashboardDataset viewDashboardDataset = datasetService.info(id);
        String jsonString = JSONObject.toJSONString(viewDashboardDataset);
        DashboardDataset dataset = translateJson(jsonString);
        dataset.setUserId(authenticationHolder.getAuthenticatedUser().getId());
        dataset.setName(name);
        datasetService.copy(dataset);
        return buildSuccJson();
    }

    @GetMapping(value = "/getcolumns")
    public Object getColumns(@RequestParam(name = "id") Integer id) {
        return buildSuccJson(datasetService.getColumns(id));
    }
}
