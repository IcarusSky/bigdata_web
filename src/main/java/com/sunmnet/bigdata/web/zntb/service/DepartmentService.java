package com.sunmnet.bigdata.web.zntb.service;

import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.zntb.model.po.Department;
import com.sunmnet.bigdata.web.zntb.model.po.Position;
import com.sunmnet.bigdata.web.zntb.persistent.DepartmentDao;
import com.sunmnet.bigdata.web.zntb.persistent.PositionDao;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {

    @Resource
    private DepartmentDao departmentDao;

    @Resource
    private PositionDao positionDao;

    @SuppressWarnings("unused")
	public void saveDepartment(Department dep) {
        dep.setParentId(-1);
        int parentId = departmentDao.saveDepartment(dep);
        if (!CollectionUtils.isEmpty(dep.getDepList()))
            departmentDao.batchSave(dep.getId(), dep.getUserId(), dep.getDepList());
    }


    public Object updateDepartment(Department dep) {
        departmentDao.updateById(dep);

        List<Department> list = dep.getDepList();
        if (!CollectionUtils.isEmpty(list)) {

            for (Department department : list) {

                if (department.getId() == null) {
                    department.setUserId(dep.getUserId());
                    department.setParentId(dep.getId());
                    departmentDao.saveDepartment(department);
                } else {
                    departmentDao.updateById(department);
                }
            }
        }
        return "seccess";
    }


    public Object deleteDepartment(int id) {

        if (departmentDao.countByParentId(id) > 0 || positionDao.countByDepId(id) > 0)

            throw new ServiceException("该部门不允许删除");
        else
            departmentDao.delete(id);
        return "sueecss";
    }

    public Object findDepById(int id) {

        Department dep = departmentDao.findDepById(id);
        dep.setDepList(departmentDao.findListByParetId(id));
        return  dep;
    }

    public Object findAllDepList() {
        List<Department> list = departmentDao.findListByParetId(-1);

        if (CollectionUtils.isEmpty(list))

            return new ArrayList<>(1);
        else
            for (Department dep : list) {

                List<Department> sonList = departmentDao.findListByParetId(dep.getId());
                dep.setDepList(sonList);
                for (Department sondep : sonList){
                    List<Position> posList =  positionDao.getListByDepId(sondep.getId());
                    if (CollectionUtils.isEmpty(posList))
                        sondep.setPosList(new ArrayList<>(1));
                    else
                        sondep.setPosList(new ArrayList<>(posList));
                }
            }

        return list;
    }


}