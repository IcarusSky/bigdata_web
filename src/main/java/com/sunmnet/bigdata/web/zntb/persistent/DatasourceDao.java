package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.DashboardDatasource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DatasourceDao {

    List<DashboardDatasource> getDatasourceList(Integer userId);

    long save(DashboardDatasource dashboardDatasource);

    long countExistDatasourceName(Map<String, Object> map);

    int update(DashboardDatasource dashboardDatasource);

    int delete(Integer id);

    DashboardDatasource getDatasource(Integer datasourceId);

}
