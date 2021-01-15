package com.sunmnet.bigdata.web.zntb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.security.authentication.AuthenticationHolder;
import com.sunmnet.bigdata.web.zntb.model.dto.DatasetColumn;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewDashboardDataset;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardDataset;
import com.sunmnet.bigdata.web.zntb.model.po.FormWidget;
import com.sunmnet.bigdata.web.zntb.persistent.DatasetDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasetService {
    @Autowired
    private DatasetDao datasetDao;

    @Resource
    protected AuthenticationHolder authenticationHolder;

    @Resource
    private FormWidgetService formWidgetService;

    @Resource
    private DataProviderService dataProviderService;

    public void update(DashboardDataset dataset) {
        if (countExistDatasetName(dataset.getName(), dataset.getId(), dataset.getUserId())) {
            throw new ServiceException("数据集名称已存在");
        }
        datasetDao.update(dataset);
    }

    public void changeName(DashboardDataset dataset) {
        if (countExistDatasetName(dataset.getName(), dataset.getId(), dataset.getUserId())) {
            throw new ServiceException("数据集名称已存在");
        }
        datasetDao.updateName(dataset);
    }

    public void delete(Integer id) {
        if (id == null || id <= 0) {
            return;
        }

        List<FormWidget> widgets = formWidgetService.getListByDatasetId(id);
        if (CollectionUtils.isNotEmpty(widgets)) {
            throw new ServiceException("有表单组件使用该数据集，请勿删除");
        }
        datasetDao.delete(id);
    }

    @SuppressWarnings("unchecked")
	public List<ViewDashboardDataset> getDatasetList(Integer userId) {
        return Lists.transform(datasetDao.getDatasetList(userId), ViewDashboardDataset.TO);
    }

    @SuppressWarnings("unchecked")
	public List<ViewDashboardDataset> getDatasetListByCategoryId(Integer userId, int categoryId) {
        return Lists.transform(datasetDao.getDatasetListByCategoryId(userId, categoryId), ViewDashboardDataset.TO);
    }

    public boolean checkDatasource(Integer datasourceId) {
        return datasetDao.getDatasetCountByDatasourceId(datasourceId) == 0;
    }

    public Integer save(DashboardDataset dataset) {
        if (countExistDatasetName(dataset.getName(), null, dataset.getUserId())) {
            throw new ServiceException("数据集名称已存在");
        }
        datasetDao.save(dataset);
        return dataset.getId();
    }

    public boolean countExistDatasetName(String name, Integer id, Integer userId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("dataset_name", name);
        paramMap.put("dataset_id", id);
        paramMap.put("user_id", userId);
        return datasetDao.countExistDatasetName(paramMap) > 0;
    }

    public DashboardDataset getDatasetById(Integer datasetId) {
        return datasetDao.getDataset(datasetId);
    }

    @SuppressWarnings("unchecked")
	public ViewDashboardDataset info(Integer datasetId) {
        return (ViewDashboardDataset) ViewDashboardDataset.TO.apply(datasetDao.getDataset(datasetId));
    }

    public Integer copy(DashboardDataset dataset) {
        return save(dataset);
    }

    public int getDatasetCountByCategoryId(Integer categoryId) {
        return datasetDao.getDatasetCountByCategoryId(categoryId);
    }

    public List<DatasetColumn> getColumns(Integer id) {
        if (id == null || id <= 0) {
            return Collections.emptyList();
        }

        DashboardDataset dataset = this.getDatasetById(id);
        if (dataset == null) {
            return Collections.emptyList();
        }

        if (DashboardDataset.Type.SQL.name().equalsIgnoreCase(dataset.getType())) {
            String[] columns = dataProviderService.getDatasetColumns(dataset);
            if (ArrayUtils.isEmpty(columns)) {
                return Collections.emptyList();
            }
            return Arrays.stream(columns).map(i -> new DatasetColumn(i, i)).collect(Collectors.toList());
        }

        if (DashboardDataset.Type.TABLE.name().equalsIgnoreCase(dataset.getType())) {
            JSONObject data = JSON.parseObject(dataset.getData());

            JSONArray columns = data.getJSONArray("selects");
            if (CollectionUtils.isEmpty(columns)) {
                return Collections.emptyList();
            }

            List<DatasetColumn> result = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                JSONObject column = columns.getJSONObject(i);
                String name = column.getString("name");
                String alias = StringUtils.isEmpty(column.getString("alias")) ? name : column.getString("alias");
                result.add(new DatasetColumn(name, alias));
            }
            return result;
        }

        return Collections.emptyList();
    }

    public void changeCategory(Integer id, Integer categoryId) {
        datasetDao.changeCategory(id,categoryId);
    }
}
