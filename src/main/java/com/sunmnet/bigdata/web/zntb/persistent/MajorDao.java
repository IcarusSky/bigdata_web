package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.Major;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MajorDao {

    int countByAcademyCode(@Param("academyCode") String academyCode);

    int save(Major major);

    int update(Major major);

    int delete(@Param("code") String code);

    int countByMajorCode(@Param("majorCode") String majorCode);

    int countByMajorName(@Param("majorName") String majorName);

    Major findByCode(@Param("majorCode") String majorCode);

    List<Major> findListByAcademyCode(@Param("academyCode") String academyCode);
}