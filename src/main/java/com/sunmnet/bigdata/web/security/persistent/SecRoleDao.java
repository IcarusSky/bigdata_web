package com.sunmnet.bigdata.web.security.persistent;

import com.sunmnet.bigdata.web.security.model.po.SecRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecRoleDao {
    int deleteById(Integer id);

    int insert(SecRole record);

    int insertSelective(SecRole record);

    SecRole selectById(Integer id);

    SecRole selectByName(@Param("roleName") String roleName);

    List<SecRole> selectAll();

    int updateByIdSelective(SecRole record);

    int updateById(SecRole record);
}