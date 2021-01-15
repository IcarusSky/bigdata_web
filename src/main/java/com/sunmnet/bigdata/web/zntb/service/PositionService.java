package com.sunmnet.bigdata.web.zntb.service;

import com.sunmnet.bigdata.web.zntb.model.po.Position;
import com.sunmnet.bigdata.web.zntb.persistent.PositionDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class PositionService {


    @Resource
    private PositionDao positionDao;

    public void save(List<Position> list) {
        positionDao.save(list);
    }

    public void update(Position pos) {
        positionDao.update(pos);
    }

    public void delete(int id) {
        positionDao.delete(id);
    }


    public Position getPosByDepId(int id) {
        return positionDao.getPosByDepId(id);
    }

    public List<Position> getListByDepId(int id) {
        return positionDao.getListByDepId(id);
    }
}