package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentDao {

    int saveDepartment(Department department);

    int countByParentId(@Param("id") int id);

    int delete(@Param("id") int id);

    int updateById(Department department);

    List<Department> findListByParetId(@Param("parentId") int parentId);

    Department findDepById(@Param("id") int id);

    int batchSave(@Param("parentId") int parentId, @Param("userId") int userId, @Param("depList") List<Department> depList);
}