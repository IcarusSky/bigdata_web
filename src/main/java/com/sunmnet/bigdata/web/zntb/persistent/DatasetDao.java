package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.DashboardDataset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DatasetDao {

    List<DashboardDataset> getDatasetList(@Param("userId") Integer userId);

    int delete(Integer id);

    int save(DashboardDataset dataset);

    int countExistDatasetName(Map<String, Object> map);

    int update(DashboardDataset dataset);

    DashboardDataset getDataset(Integer id);

    int updateName(DashboardDataset dataset);

    List<DashboardDataset> getDatasetListByCategoryId(@Param("userId") Integer userId, @Param("categoryId") int categoryId);

    int getDatasetCountByDatasourceId(@Param("datasourceId") Integer datasourceId);

    int getDatasetCountByCategoryId(@Param("categoryId") Integer categoryId);

    void changeCategory(@Param("id")Integer id, @Param("categoryId")Integer categoryId);
}
