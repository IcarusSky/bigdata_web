package com.sunmnet.bigdata.web.security.persistent;

import com.sunmnet.bigdata.web.security.model.po.SecMenu;

import java.util.List;

public interface SecMenuDao {
    int deleteById(Integer id);

    int insert(SecMenu record);

    int insertSelective(SecMenu record);

    SecMenu selectById(Integer id);

    List<SecMenu> selectAll();

    List<SecMenu> selectAllAuthorizedMenuOfUser(Integer userId);

    Integer selectMaxSeq();

    int updateByIdSelective(SecMenu record);

    int updateById(SecMenu record);

    void disableMenuById(Integer id);

    void increSeq(Integer startSeq);
}