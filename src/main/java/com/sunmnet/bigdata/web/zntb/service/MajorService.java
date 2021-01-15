package com.sunmnet.bigdata.web.zntb.service;

import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.zntb.model.po.Major;
import com.sunmnet.bigdata.web.zntb.persistent.MajorDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MajorService {


    @Resource
    private MajorDao majorDao;

    public Object save(Major major) {

        /*if (majorDao.countByMajorCode(major.getCode()) > 0)
            throw new ServiceException("专业编码已存在,不允许添加");
            
        if (majorDao.countByMajorName(major.getName()) > 0)
            throw new ServiceException("专业名称已存在,不允许添加");
            
        else*/
            majorDao.save(major);
        return "success";
    }

    public void update(Major major) {

        Major old = majorDao.findByCode(major.getCode());
        if (!major.getName().trim().equals(old.getName().trim()))
            if (majorDao.countByMajorName(major.getName()) > 0)
                throw new ServiceException("专业名称已存在,不允许修改");
            else
                majorDao.update(major);
    }

    public void delete(String majorCode) {
        majorDao.delete(majorCode);
    }

}