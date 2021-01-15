package com.sunmnet.bigdata.web.zntb.service;

import com.google.common.collect.Lists;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewDashboardDatasource;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardDatasource;
import com.sunmnet.bigdata.web.zntb.persistent.DatasourceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatasourceService {
    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DatasetService datasetService;

    @SuppressWarnings("unchecked")
	public List<ViewDashboardDatasource> getViewDatasourceList(Integer userId) {
        List<DashboardDatasource> list = datasourceDao.getDatasourceList(userId);
        return Lists.transform(list, ViewDashboardDatasource.TO);
    }

    public long save(DashboardDatasource datasource) {
        if (existDatasourceName(datasource.getName(), null, datasource.getUserId())) {
            throw new ServiceException("数据源名称已存在");
        }
        datasourceDao.save(datasource);
        return datasource.getId();
    }

    public void update(DashboardDatasource datasource) {
        if (existDatasourceName(datasource.getName(), datasource.getId(), datasource.getUserId())) {
            throw new ServiceException("数据源名称已存在");
        }
        datasourceDao.update(datasource);
    }

    public void delete(Integer id) {
        if (!datasetService.checkDatasource(id)) {
            throw new ServiceException("有数据集使用该数据源,请勿删除");
        }
        datasourceDao.delete(id);
    }

    public boolean existDatasourceName(String sourceName, Integer id, Integer userId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("source_name", sourceName);
        paramMap.put("datasource_id", id);
        paramMap.put("user_id", userId);
        return datasourceDao.countExistDatasourceName(paramMap) > 0;
    }

    public DashboardDatasource getDatasourceById(Integer datasourceId) {
        return datasourceDao.getDatasource(datasourceId);
    }
}
