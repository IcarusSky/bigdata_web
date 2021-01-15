package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.ColumnMetaData;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewDashboardDatasource;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardDatasource;
import com.sunmnet.bigdata.web.zntb.service.DataProviderService;
import com.sunmnet.bigdata.web.zntb.service.DatasourceService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmnet.bigdata.web.zntb.model.po.DashboardDatasource.translateJson;

@RestController
@RequestMapping("/datasource")
public class DatasourceController extends BaseController {

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private DataProviderService dataProviderService;

    @RequestMapping(value = "/getlist")
    public Object getDatasourceList() {
        List<ViewDashboardDatasource> viewDatasourceList = datasourceService
                .getViewDatasourceList(authenticationHolder.getAuthenticatedUser().getId());
        return buildSuccJson(viewDatasourceList);
    }

    @RequestMapping(value = "/save")
    public Object saveNewDatasource(@RequestParam(name = "json") String json) {
        DashboardDatasource datasource = translateJson(json);
        datasource.setUserId(authenticationHolder.getAuthenticatedUser().getId());

        Pair<Boolean, String> validateResult = datasource.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        return buildSuccJson(datasourceService.save(datasource));
    }

    @RequestMapping(value = "/update")
    public Object updateDatasource(@RequestParam(name = "json") String json) {
        DashboardDatasource datasource = translateJson(json);
        datasource.setUserId(authenticationHolder.getAuthenticatedUser().getId());
        datasource.setUpdateTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        Pair<Boolean, String> validateResult = datasource.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        datasourceService.update(datasource);
        return buildSuccJson();
    }

    @RequestMapping(value = "/delete")
    public Object deleteDatasource(@RequestParam(name = "id") Integer id) {
        datasourceService.delete(id);
        return buildSuccJson();
    }

    @GetMapping("/metadata")
    public Object getMetadata(@RequestParam(name = "id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        List<ColumnMetaData> columns = dataProviderService.getColumns(id);

        result.put("databases", columns.stream().map(ColumnMetaData::getDatabase).distinct().sorted()
                .collect(Collectors.toList()));
        Map<String, List<String>> sortedTables = new HashMap<>();
        columns.stream().collect(Collectors.groupingBy(ColumnMetaData::getDatabase,
                Collectors.mapping(ColumnMetaData::getTable, Collectors.toSet())))
                .forEach((k, v) -> sortedTables.put(k, new ArrayList<>(v).stream().sorted()
                        .collect(Collectors.toList())));
        result.put("tables", sortedTables);

        result.put("columns", columns.stream().collect(Collectors.groupingBy(i -> i.getDatabase() + "@" + i.getTable())));

        return buildSuccJson(result);
    }
}
