package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.DashboardCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryDao {

    List<DashboardCategory> getCategoryListByParentId(@Param("userId") Integer userId, @Param("type") String type, @Param("parentId") Integer parentId);

    int save(DashboardCategory dashboardCategory);

    long countExistCategoryName(Map<String, Object> map);

    int update(DashboardCategory dashboardCategory);

    int delete(Integer id);

    int updateParentId(@Param("id") Integer id, @Param("parentId") Integer parentId);

    int updateName(DashboardCategory category);

    List<DashboardCategory> getCategoryListByTypeAndUserId(@Param("userId")Integer userId, @Param("type")String type);
}
