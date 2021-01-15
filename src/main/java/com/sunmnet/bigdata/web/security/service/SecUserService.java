package com.sunmnet.bigdata.web.security.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.core.security.userdetails.UserDetails;
import com.sunmnet.bigdata.web.security.model.dto.User;
import com.sunmnet.bigdata.web.security.model.dto.UserQuery;
import com.sunmnet.bigdata.web.security.model.po.SecRes;
import com.sunmnet.bigdata.web.security.model.po.SecUser;
import com.sunmnet.bigdata.web.security.model.po.SecUserRelRole;
import com.sunmnet.bigdata.web.security.model.po.SecUserRoles;
import com.sunmnet.bigdata.web.security.persistent.SecUserDao;
import com.sunmnet.bigdata.web.security.persistent.SecUserRelRoleDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("deprecation")
public class SecUserService {

    @Autowired
    private SecUserDao userDao;

    @Autowired
    private SecUserRelRoleDao userRelRoleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecResService resService;

    @Value("${security.default-user-password}")
    private String defaultUserPassword;

    public SecUser getById(Integer userId) {
        Preconditions.checkArgument(userId != null && userId > 0, "用户ID必须大于零");
        return userDao.selectById(userId);
    }

    public SecUser getByUsername(String username) {
        Preconditions.checkNotNull(StringUtils.isNotEmpty(username), "用户名不能为空");
        return userDao.selectByUsername(username);
    }

    public List<SecUser> getAllUser() {
        return userDao.selectAll(null);
    }

    public PageResult<User> getByPage(Integer pageNum, Integer pageSize, UserQuery query) {
        PageInfo<SecUser> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> userDao.selectAll(query));

        List<SecUserRoles> userRoles = null;
        if (CollectionUtils.isNotEmpty(pageInfo.getList())) {
            Map<String, Object> params = new HashMap<>();
            params.put("userIds", pageInfo.getList().stream().map(SecUser::getId).collect(Collectors.toList()));
            userRoles = userDao.selectUserRoles(params);
        }

        List<SecUserRoles> tmpUserRoles = userRoles;
        List<User> dtoList = pageInfo.getList().stream().map(i -> {
            User dto = i.convertToDTO();
            String roles = "";
            if (CollectionUtils.isNotEmpty(tmpUserRoles)) {
                Optional<SecUserRoles> opt = tmpUserRoles.stream().filter(j -> j.getUserId().equals(i.getId())).findFirst();
                if (opt.isPresent()) {
                    roles = StringUtils.trimToEmpty(opt.get().getRoles());
                }
            }
            dto.setRoles(roles);
            return dto;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : dtoList);
    }

    @Transactional("securityTxManager")
    public void save(SecUser user, Integer roleId) {
        Preconditions.checkNotNull(user, "用户信息不能为空");
        Preconditions.checkArgument(roleId != null && roleId > 0, "用户角色ID必须大于零");

        if (userDao.countSameUserName(null, user.getUserName()) > 0) {
            throw new ServiceException("用户名已存在");
        }

        user.setUserPassword(this.encodePassword(user.getUserPassword()));
        userDao.insertSelective(user);
        userRelRoleDao.insertSelective(new SecUserRelRole(user.getId(), roleId));
    }

    @Transactional("securityTxManager")
    public void update(SecUser user, Integer roleId) {
        Preconditions.checkNotNull(user, "用户信息不能为空");
        Preconditions.checkArgument(user.getId() != null && user.getId() > 0, "用户ID必须大于零");
        Preconditions.checkArgument(roleId != null && roleId > 0, "用户角色ID必须大于零");

        if (userDao.countSameUserName(user.getId(), user.getUserName()) > 0) {
            throw new ServiceException("用户名已存在");
        }

        user.setUpdateTime(new Date());
        userDao.updateByIdSelective(user);
        userRelRoleDao.deleteByUserId(user.getId());
        userRelRoleDao.insertSelective(new SecUserRelRole(user.getId(), roleId));
    }

    private String encodePassword(String rawPass) {
        if (StringUtils.isEmpty(rawPass)) {
            return rawPass;
        }
        return passwordEncoder.encodePassword(rawPass, null);
    }

    @Transactional("securityTxManager")
    public void delete(Integer id) {
        Preconditions.checkArgument(id != null && id > 0, "用户ID必须大于零");
        userDao.deleteById(id);
        userRelRoleDao.deleteByUserId(id);
        resService.deleteByOwner(SecRes.OwnerType.USER, id);
    }

    public String initPwd(Integer id) {
        Preconditions.checkArgument(id != null && id > 0, "用户ID必须大于零");
        SecUser user = new SecUser();
        user.setId(id);
        user.setUserPassword(this.encodePassword(defaultUserPassword));
        userDao.updateByIdSelective(user);
        return defaultUserPassword;
    }
    
    /**
     * @Description 同一账户类型，学号/教工号唯一性校验
     * @Author libin
     * @Date 2019年8月26日下午4:28:46
     * @return
     */
    public boolean accountUnique(Byte accountType, String accountCode) {
    	boolean flag = true;// 默认唯一
    	List<SecUser> users = userDao.selectAccount(accountType, accountCode);
    	if (null != users && users.size()>0) {
    		flag=false;// 不唯一
		}
    	return flag;
    	
    }

	public void modifyPassword(UserDetails userDetails, String oldpassword, String newPassword) {
		SecUser userInfo = userDao.selectById(userDetails.getId());
        if (null == userInfo)
        {
            throw new ServiceException("用户信息不存在！");
        }
        
        System.out.println("旧密码"+encodePassword(oldpassword)); 
        if (!StringUtils.equals(encodePassword(oldpassword), userInfo.getUserPassword()))
        {
            throw new ServiceException("旧密码验证失败！");
        }
        System.out.println("新密码"+encodePassword(newPassword)); 
        userInfo.setUserPassword(encodePassword(newPassword));
        userInfo.setUpdateTime(new Date());
        int count = 0;
        try
        {
            count = userDao.updateUserPasswordByUserNo(userInfo);
        }
        catch (Exception e)
        {
        	throw new ServiceException("修改用户密码失败，详情：" + e.getMessage());
        }
        if (count != 1)
        {
        	throw new ServiceException("修改用户密码失败！");
        }
	}
}
