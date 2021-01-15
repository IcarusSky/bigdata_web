package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.Academy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AcademyDao {

    /**
     * 查询所有学院信息
     *
     * @return 学院信息列表
     */
    List<Academy> findAll();

    int save(Academy academy);

    int update(Academy academy);

    int delete(@Param("code") String code);

    int countByAcademyCode(@Param("academyCode") String academyCode);

    int countByAcademyName(@Param("academyName") String academyName);

    Academy findByCode(@Param("academyCode") String academyCode);
}
