package com.sunmnet.bigdata.web.zntb.service;

import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.zntb.model.po.Academy;
import com.sunmnet.bigdata.web.zntb.persistent.AcademyDao;
import com.sunmnet.bigdata.web.zntb.persistent.MajorDao;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AcademyService {
    @Resource
    private AcademyDao academyDao;

    @Resource
    private MajorDao majorDao;

    public Object findAllAcademies() {
        List<Academy> academies = academyDao.findAll();
        if (CollectionUtils.isEmpty(academies))
            return new ArrayList<>(1);
        else

            for (Academy academy : academies) {
                academy.setMajorList(majorDao.findListByAcademyCode(academy.getCode()));
            }
        return academies;
    }

    public Object save(Academy academy) {

        if (academyDao.countByAcademyCode(academy.getCode()) > 0)
            throw new ServiceException("编码使用,不允许添加");
        if (academyDao.countByAcademyName(academy.getName().trim()) > 0)
            throw new ServiceException("名称已使用,不允许添加");
        else
            academyDao.save(academy);
        return "success";

    }

    public void update(Academy academy) {

        Academy old = academyDao.findByCode(academy.getCode());
        if (!academy.getName().trim().equals(old.getName().trim()))

            if (academyDao.countByAcademyName(academy.getName().trim()) > 0)
                throw new ServiceException("名称已存在,不允许修改");
            else
                academyDao.update(academy);
    }

    public Object delete(String code) {

        if (majorDao.countByAcademyCode(code) > 0)
            throw new ServiceException("已被使用,不允许删除");
        else
            academyDao.delete(code);
        return "success";
    }

}
