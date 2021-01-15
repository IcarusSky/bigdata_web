package com.sunmnet.bigdata.web.security.config;

import com.sunmnet.bigdata.web.security.model.dto.Menu;
import com.sunmnet.bigdata.web.security.model.dto.User;
import com.sunmnet.bigdata.web.security.model.po.SecUser;
import com.sunmnet.bigdata.web.security.service.MenuService;
import com.sunmnet.bigdata.web.security.service.SecUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义验证方法
 *
 * @author qianleijava
 * @date 2020/1/8 16:35
 */
@Component
public class SimpleAuthenticationManager implements AuthenticationManager {

    @Autowired
    private SecUserService secUserService;

    @Resource
    private MenuService menuService;

    /**
     * 验证方法
     */
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        SecUser secUser = secUserService.getByUsername(auth.getName());
        if (secUser == null) {
            throw new BadCredentialsException("用户信息不存在，请联系管理员！");
        }
        // 这里我们自定义了验证通过条件：username与password相同就可以通过认证
        if (auth.getName().equals(secUser.getUserName())) {

            List<Menu> menus = menuService.getAllAuthorizedMenuTreeOfUser(secUser.getId());

            // 当用户未被授权任何菜单
            if (CollectionUtils.isEmpty(menus)) {
                throw new BadCredentialsException("该账户未授权任何权限，请联系管理员授权！");
            }
            User user = new User(secUser.getUserName(),"",true,true,true,true,auth.getAuthorities());
            user.setId(secUser.getId());
            user.setName(secUser.getName());
            user.setUserId("");
            user.setAccountCode("");
            user.setSex((byte)0);
            user.setRoles("");
            user.setMenus(menus);
            return new UsernamePasswordAuthenticationToken(user, auth.getCredentials());
        }
        // 没有通过认证则抛出密码错误异常
        throw new BadCredentialsException("Bad Credentials");
    }
}
