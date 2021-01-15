package com.sunmnet.bigdata.web.zntb.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.zntb.dataprovider.DataProvider;
import com.sunmnet.bigdata.web.zntb.dataprovider.DataProviderManager;
import com.sunmnet.bigdata.web.zntb.dataprovider.DataProviderViewManager;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.AggConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.provider.jdbc.JdbcDataProvider;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.AggregateResult;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.ColumnMetaData;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.TableMetaData;
import com.sunmnet.bigdata.web.zntb.enums.ResourceType;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardDataset;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DataProviderService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private DatasetService datasetService;

    public String[] getDatasetColumns(DashboardDataset dataset) {
        try {
            DataProvider dataProvider = getDataProvider(null, null, new Dataset(dataset));
            return dataProvider.getColumn(false);
        } catch (Exception e) {
            LOG.error("", e);
            throw new ServiceException(e.getMessage());
        }
    }

    public String[] getDatasetColumns(Integer datasourceId, Map<String, String> query) {
        try {
            DataProvider dataProvider = getDataProvider(datasourceId, query, null);
            return dataProvider.getColumn(false);
        } catch (Exception e) {
            LOG.error("", e);
            throw new ServiceException(e.getMessage());
        }
    }

    public AggregateResult queryAggData(Integer datasetId, AggConfig config) {
        try {
            Dataset dataset = getDataset(datasetId);
            DataProvider dataProvider = getDataProvider(null, null, dataset);
            return dataProvider.getAggData(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new ServiceException(e.getMessage());
        }
    }

    public void test(JSONObject dataSource, Map<String, String> query) {
        try {
            DataProvider dataProvider = DataProviderManager.getDataProvider(
                    dataSource.getString("type"),
                    Maps.transformValues(dataSource.getJSONObject("config"), Functions.toStringFunction()),
                    query, true);
            dataProvider.test();
        } catch (Exception e) {
            LOG.error("数据源连接测试失败", e);
            throw new ServiceException("数据源连通性异常，请检查参数是否正确！");
        }
    }

    public List<Map<String, Object>> getDatasourceParams(String type) {
        return DataProviderViewManager.getDatasourceParams(type);
    }

    public List<Map<String, Object>> getProviderList() {
        return DataProviderManager.getProviderList();
    }


    public List<Map<String, Object>> getQueryParams(String type, String page) {
        return DataProviderViewManager.getQueryParams(type, page);
    }

    public boolean checkSameNameRes(String resName, ResourceType resType, Integer excludeResId, Integer userId) {
        switch (resType) {
            case DATASOURCE:
                return datasourceService.existDatasourceName(resName, excludeResId, userId);
            case DATASET:
                return datasetService.countExistDatasetName(resName, excludeResId, userId);
            default:
                return false;
        }
    }

    public List<String> getDatabases(Integer datasourceId) {
        try {
            DataProvider dataProvider = getDataProvider(datasourceId, null, null);
            if (!(dataProvider instanceof JdbcDataProvider)) {
                return Collections.emptyList();
            }
            return ((JdbcDataProvider) dataProvider).getDatabases();
        } catch (Exception ex) {
            LOG.error("获取数据库信息出错", ex);
            return Collections.emptyList();
        }
    }

    public List<TableMetaData> getTables(Integer datasourceId) {
        try {
            DataProvider dataProvider = getDataProvider(datasourceId, null, null);
            if (!(dataProvider instanceof JdbcDataProvider)) {
                return Collections.emptyList();
            }
            return ((JdbcDataProvider) dataProvider).getTables();
        } catch (Exception ex) {
            LOG.error("获取表信息出错", ex);
            return Collections.emptyList();
        }
    }

    public List<ColumnMetaData> getColumns(Integer datasourceId) {
        try {
            DataProvider dataProvider = getDataProvider(datasourceId, null, null);
            if (!(dataProvider instanceof JdbcDataProvider)) {
                return Collections.emptyList();
            }
            return ((JdbcDataProvider) dataProvider).getColumns();
        } catch (Exception ex) {
            LOG.error("获取列信息出错", ex);
            return Collections.emptyList();
        }
    }

    protected Dataset getDataset(Integer datasetId) {
        if (datasetId == null) {
            return null;
        }
        return new Dataset(datasetService.getDatasetById(datasetId));
    }

    private DataProvider getDataProvider(Integer datasourceId, Map<String, String> query, Dataset dataset) throws Exception {
        if (dataset != null) {
            datasourceId = dataset.getDatasourceId();
            query = dataset.getQuery();
        }
        DashboardDatasource datasource = datasourceService.getDatasourceById(datasourceId);
        if (datasource == null) {
            throw new ServiceException("数据源不存在或已删除!");
        }
        JSONObject datasourceConfig = JSONObject.parseObject(datasource.getConfig());
        Map<String, String> dataSource = Maps.transformValues(datasourceConfig, Functions.toStringFunction());
        DataProvider dataProvider = DataProviderManager.getDataProvider(datasource.getType(), dataSource, query);
        if (dataset != null && dataset.getInterval() != null && dataset.getInterval() > 0) {
            dataProvider.setInterval(dataset.getInterval());
        }
        return dataProvider;
    }

    protected class Dataset {
        private Integer datasourceId;
        private Map<String, String> query;
        private Long interval;
        private JSONObject schema;

        public Dataset(DashboardDataset dataset) {
            JSONObject data = JSONObject.parseObject(dataset.getData());
            this.query = Maps.transformValues(data.getJSONObject("query"), Functions.toStringFunction());
            this.datasourceId = dataset.getDatasourceId();
            this.interval = data.getLong("interval");
            this.schema = data.getJSONObject("schema");
        }

        public JSONObject getSchema() {
            return schema;
        }

        public void setSchema(JSONObject schema) {
            this.schema = schema;
        }

        public Integer getDatasourceId() {
            return datasourceId;
        }

        public void setDatasourceId(Integer datasourceId) {
            this.datasourceId = datasourceId;
        }

        public Map<String, String> getQuery() {
            return query;
        }

        public void setQuery(Map<String, String> query) {
            this.query = query;
        }

        public Long getInterval() {
            return interval;
        }

        public void setInterval(Long interval) {
            this.interval = interval;
        }
    }
}
