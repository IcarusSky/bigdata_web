package com.sunmnet.bigdata.web.security.persistent;

import com.sunmnet.bigdata.web.security.model.po.SecUserRelRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecUserRelRoleDao {
    int deleteById(Integer id);

    int deleteByUserId(Integer userId);

    int deleteByRoleId(Integer roleId);

    int insert(SecUserRelRole record);

    int insertSelective(SecUserRelRole record);

    void insertBatch(@Param("records") List<SecUserRelRole> records);

    SecUserRelRole selectById(Integer id);

    List<SecUserRelRole> selectAll();

    int countByRoleId(@Param("roleId") Integer roleId);

    int updateByIdSelective(SecUserRelRole record);

    int updateById(SecUserRelRole record);
}