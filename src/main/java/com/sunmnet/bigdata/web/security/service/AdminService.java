package com.sunmnet.bigdata.web.security.service;

import com.google.common.base.Preconditions;
import com.sunmnet.bigdata.web.security.model.dto.RoleRes;
import com.sunmnet.bigdata.web.security.model.po.SecRes;
import com.sunmnet.bigdata.web.security.model.po.SecRole;
import com.sunmnet.bigdata.web.security.model.po.SecUser;
import com.sunmnet.bigdata.web.security.model.po.SecUserRelRole;
import com.sunmnet.bigdata.web.security.persistent.SecUserRelRoleDao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private SecUserRelRoleDao userRelRoleDao;

    @Autowired
    private SecResService resService;

    @Autowired
    private SecUserService userService;

    @Autowired
    private SecRoleService roleService;

    /**
     * 获取所有用户和角色关联关系
     */
    public List<SecUserRelRole> getAllUserRole() {
        List<SecUserRelRole> list = userRelRoleDao.selectAll();
        if (list == null) {
            list = Collections.emptyList();
        }
        return list;
    }

    /**
     * 获取所有角色和资源关联关系
     */
    public List<RoleRes> getAllRoleRes() {
        List<SecRes> list = resService.selectAllRoleRes();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        return list.stream().collect(Collectors.groupingBy(SecRes::getOwnerId)).entrySet().stream().map(i -> {
            RoleRes roleRes = new RoleRes();
            roleRes.setRoleId(i.getKey());
            roleRes.setMenu(i.getValue().stream().filter(j -> j.getResType().equals(SecRes.Type.MENU.getValue()))
                    .map(k -> k.getResId().intValue()).collect(Collectors.toList()));
            return roleRes;
        }).collect(Collectors.toList());
    }

    /**
     * 获取某个角色和资源关联关系
     */
    public RoleRes getRoleRes(Integer roleId) {
        Preconditions.checkArgument(roleId != null && roleId > 0, "角色ID必须大于零");

        RoleRes roleRes = new RoleRes();
        roleRes.setRoleId(roleId);

        List<SecRes> list = resService.selectRoleRes(roleId);
        if (CollectionUtils.isEmpty(list)) {
            roleRes.setMenu(Collections.emptyList());
            return roleRes;
        }

        roleRes.setMenu(list.stream().filter(i -> i.getResType().equals(SecRes.Type.MENU.getValue()))
                .map(j -> j.getResId().intValue()).collect(Collectors.toList()));
        return roleRes;
    }

    @Transactional("securityTxManager")
    public void updateUserRole(Integer userId, List<Integer> roleIds) {
        Preconditions.checkArgument(userId != null && userId > 0, "用户ID必须大于零");

        SecUser user = userService.getById(userId);
        if (user == null) {
            return;
        }

        userRelRoleDao.deleteByUserId(userId);
        if (CollectionUtils.isNotEmpty(roleIds)) {
            userRelRoleDao.insertBatch(roleIds.stream().map(roleId -> new SecUserRelRole(userId, roleId))
                    .collect(Collectors.toList()));
        }
    }

    @Transactional("securityTxManager")
    public void updateRoleRes(RoleRes roleRes) {
        Preconditions.checkNotNull(roleRes, "角色资源不能为空");
        Preconditions.checkArgument(roleRes.getRoleId() != null && roleRes.getRoleId() > 0, "角色ID必须大于零");

        SecRole role = roleService.getById(roleRes.getRoleId());
        if (role == null) {
            return;
        }

        resService.deleteByOwner(SecRes.OwnerType.ROLE, roleRes.getRoleId());

        List<SecRes> resList = new ArrayList<>();

        // 菜单资源
        if (CollectionUtils.isNotEmpty(roleRes.getMenu())) {
            resList.addAll(roleRes.getMenu().stream().map(i -> {
                SecRes res = new SecRes();
                res.setOwnerType(SecRes.OwnerType.ROLE.getValue());
                res.setOwnerId(roleRes.getRoleId());
                res.setResType(SecRes.Type.MENU.getValue());
                res.setResId(i.longValue());
                return res;
            }).collect(Collectors.toList()));
        }

        resService.saveBatch(resList);
    }
}
