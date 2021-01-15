package com.sunmnet.bigdata.web.security.authentication;

import com.sunmnet.bigdata.web.core.model.dto.JsonResult;
import com.sunmnet.bigdata.web.core.util.WebUtils;
import com.sunmnet.bigdata.web.security.model.dto.Menu;
import com.sunmnet.bigdata.web.security.model.dto.User;
import com.sunmnet.bigdata.web.security.service.MenuService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 登录验证成功处理
 */
@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Resource
    private MenuService menuService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        User user = (User) (authentication.getPrincipal());

        List<Menu> menus = menuService.getAllAuthorizedMenuTreeOfUser(user.getId());

        // 当用户未被授权任何菜单
        if (CollectionUtils.isEmpty(menus)) {
            JsonResult data = new JsonResult(false, "该账户未授权任何权限，请联系管理员授权");
            data.setErrorCode(-1);
            WebUtils.writeJSON(response, data);
            return;
        }

        user.setMenus(menus);
        JsonResult data = new JsonResult(true, "登录成功");
        data.setObj(user);
        WebUtils.writeJSON(response, data);
    }
}
