package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.po.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PositionDao {


    int save(@Param("list") List<Position> list);

    int update(Position pos);

    Position getPosByDepId(@Param("id") int id);

    int delete(@Param("id") int id);

    int countByDepId(@Param("depId") int depId);

    List<Position> getListByDepId(@Param("id") int id);
}