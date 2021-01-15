package com.sunmnet.bigdata.web.zntb.service;

import com.google.common.base.Preconditions;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.util.DateUtils;
import com.sunmnet.bigdata.web.security.model.po.SecUser;
import com.sunmnet.bigdata.web.security.service.SecUserService;
import com.sunmnet.bigdata.web.zntb.model.dto.AccountUser;
import com.sunmnet.bigdata.web.zntb.model.dto.UserExt;
import com.sunmnet.bigdata.web.zntb.model.po.SecUserExt;
import com.sunmnet.bigdata.web.zntb.persistent.SecUserExtDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SecUserExtService {
    @Autowired
    private SecUserExtDao userExtDao;

    @Autowired
    private SecUserService userService;

    public UserExt selectByUserId(Integer userId) {
        Preconditions.checkArgument(userId != null && userId > 0, "用户ID必须大于零");

        SecUser user = userService.getById(userId);
        if (user == null) {
            throw new ServiceException("未找到用户ID【" + userId + "】对应的用户");
        }

        UserExt userExt = new UserExt();
        userExt.setAccountType(user.getAccountType().intValue());
        userExt.setAccountCode(user.getAccountCode());

        SecUserExt secUserExt = userExtDao.selectByUserId(userId);
        if (secUserExt == null) {
            return userExt;
        }

        if (StringUtils.isNotEmpty(secUserExt.getAcademyCode())) {
            userExt.setAcademyCode(secUserExt.getAcademyCode());
        }
        if (StringUtils.isNotEmpty(secUserExt.getMajorCode())) {
            userExt.setMajorCode(secUserExt.getMajorCode());
        }
        if (secUserExt.getDepartmentId() > 0) {
            userExt.setDepartmentId(secUserExt.getDepartmentId());
        }
        if (secUserExt.getPositionId() > 0) {
            userExt.setPositionId(secUserExt.getPositionId());
        }
        userExt.setCreateTime(DateUtils.formatDateTime(secUserExt.getCreateTime()));
        return userExt;
    }

    public List<SecUserExt> selectByName(String name, String topN) {
        List<SecUserExt> userExtList = userExtDao.selectByName(name, topN);
        if (userExtList == null) {
            return Collections.emptyList();
        }
        return userExtList;
    }

    public void insert(SecUserExt userExt) {
        Preconditions.checkNotNull(userExt, "用户扩展信息不能为空");
        userExtDao.insert(userExt);
    }

    public void updateByUserId(SecUserExt userExt) {
        Preconditions.checkNotNull(userExt, "用户扩展信息不能为空");
        Preconditions.checkArgument(userExt.getUserId() != null && userExt.getUserId() > 0, "用户ID必须大于零");
        userExtDao.updateByUserId(userExt);
    }

    public void deleteByUserId(Integer userId) {
        Preconditions.checkArgument(userId != null && userId > 0, "用户ID必须大于零");
        userExtDao.deleteByUserId(userId);
    }

    public SecUserExt selectInfoByUserId(Integer userId) {
        return userExtDao.selectInfoByUserId(userId);
    }

    public AccountUser confirmUserInfo(String userName) {
        return userExtDao.confirmUserInfo(userName);
    }
}
