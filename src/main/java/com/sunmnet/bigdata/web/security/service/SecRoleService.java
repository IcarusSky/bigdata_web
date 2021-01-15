package com.sunmnet.bigdata.web.security.service;

import com.google.common.base.Preconditions;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.security.model.po.SecRes;
import com.sunmnet.bigdata.web.security.model.po.SecRole;
import com.sunmnet.bigdata.web.security.persistent.SecRoleDao;
import com.sunmnet.bigdata.web.security.persistent.SecUserRelRoleDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SecRoleService {

    @Autowired
    private SecRoleDao roleDao;

    @Autowired
    private SecUserRelRoleDao userRelRoleDao;

    @Autowired
    private SecResService resService;

    public List<SecRole> getAll() {
        List<SecRole> roles = roleDao.selectAll();
        if (roles == null) {
            roles = Collections.emptyList();
        }
        return roles;
    }

    public SecRole getById(Integer roleId) {
        Preconditions.checkArgument(roleId != null && roleId > 0);
        return roleDao.selectById(roleId);
    }

    public SecRole getByName(String name) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(name));
        return roleDao.selectByName(name);
    }

    public void save(SecRole role) {
        Preconditions.checkNotNull(role, "角色信息不能为空");
        roleDao.insertSelective(role);
    }

    public void update(SecRole role) {
        Preconditions.checkNotNull(role, "角色信息不能为空");
        Preconditions.checkArgument(role.getId() != null && role.getId() > 0, "角色ID必须大于零");

        role.setUpdateTime(new Date());
        roleDao.updateByIdSelective(role);
    }

    @Transactional("securityTxManager")
    public void delete(Integer id) {
        Preconditions.checkArgument(id != null && id > 0, "角色ID必须大于零");
        if (userRelRoleDao.countByRoleId(id) > 0) {
            throw new ServiceException("有账户使用该角色，请勿删除");
        }
        roleDao.deleteById(id);
        resService.deleteByOwner(SecRes.OwnerType.ROLE, id);
    }
}
