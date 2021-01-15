package com.sunmnet.bigdata.web.security.service;

import com.google.common.base.Preconditions;
import com.sunmnet.bigdata.web.security.enums.AuthorizedType;
import com.sunmnet.bigdata.web.security.model.po.SecRes;
import com.sunmnet.bigdata.web.security.persistent.SecResDao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SecResService {

    @Autowired
    private SecResDao secResDao;

    public boolean hasAuthorized(SecRes.Type resType, AuthorizedType authorizedType, Integer userId, Long resId) {
        Integer permission = selectPermission(resType, resId, userId);
        if (SecRes.Permission.READ.getValue().intValue() == permission) {
            if (authorizedType.equals(AuthorizedType.INFO)) {
                return true;
            }
        }
        return SecRes.Permission.ALL.getValue().intValue() == permission;
    }

    public List<Long> resIdList(SecRes.Type resType, Integer userId) {
        return secResDao.resIdList(userId, resType.getValue());
    }

    public Integer selectPermission(SecRes.Type resType, Long resId, Integer userId) {
        Integer permission = secResDao.selectPermission(userId, resId, resType.getValue());
        return permission == null ? 0 : permission;
    }

    public List<SecRes> selectRes(SecRes.Type resType, Integer userId) {
        return secResDao.selectResByUserIdAndResType(userId, resType.getValue());
    }

    public List<SecRes> selectAllRoleRes() {
        List<SecRes> list = secResDao.selectAllRoleRes();
        if (list == null) {
            list = Collections.emptyList();
        }
        return list;
    }

    public List<SecRes> selectRoleRes(Integer roleId) {
        List<SecRes> list = secResDao.selectRoleRes(roleId);
        if (list == null) {
            list = Collections.emptyList();
        }
        return list;
    }

    public void save(SecRes.Type resType, Long resId, SecRes.OwnerType ownerType, Integer ownerId, SecRes.Permission permission) {
        SecRes res = new SecRes();
        res.setResType(resType.getValue());
        res.setResId(resId);
        res.setOwnerType(ownerType.getValue());
        res.setOwnerId(ownerId);
        res.setPermission(permission);
        secResDao.insert(res);
    }

    public void saveBatch(List<SecRes> resList) {
        if (CollectionUtils.isEmpty(resList)) {
            return;
        }
        secResDao.insertBatch(resList);
    }

    public void deleteByRes(SecRes.Type resType, Long resId) {
        secResDao.deleteByRes(resType.getValue(), resId);
    }

    public void deleteByOwner(SecRes.OwnerType ownerType, Integer ownerId) {
        secResDao.deleteByOwner(ownerType.getValue(), ownerId);
    }

    public void deleteByMenuId(Integer menuId) {
        Preconditions.checkArgument(menuId != null && menuId > 0, "菜单ID必须大于零");
        secResDao.deleteByMenuId(menuId);
    }

}
