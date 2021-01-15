package com.sunmnet.bigdata.web.zntb.interceptor;

import com.sunmnet.bigdata.web.security.model.po.SecUser;
import com.sunmnet.bigdata.web.security.service.SecUserService;
import com.sunmnet.bigdata.web.zntb.model.po.SecUserExt;
import com.sunmnet.bigdata.web.zntb.service.SecUserExtService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户扩展信息拦截器，拦截bigdata-web-security包AdminController类中用户管理相关接口，进行用户扩展信息的维护
 */
@Component
public class UserExtInterceptor implements HandlerInterceptor {
    @Autowired
    private SecUserService userService;

    @Autowired
    private SecUserExtService userExtService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception ex)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj,
                           ModelAndView modelAndView) throws Exception {
        int userId = ServletRequestUtils.getIntParameter(request, "id", 0);
        String username = ServletRequestUtils.getStringParameter(request, "userName", "");
        String academyCode = ServletRequestUtils.getStringParameter(request, "academyCode", "");
        String majorCode = ServletRequestUtils.getStringParameter(request, "majorCode", "");
        int departmentId = ServletRequestUtils.getIntParameter(request, "departmentId", 0);
        int positionId = ServletRequestUtils.getIntParameter(request, "positionId", 0);

        SecUserExt userExt = new SecUserExt();
        userExt.setUserId(userId);
        userExt.setAcademyCode(academyCode);
        userExt.setMajorCode(majorCode);
        userExt.setDepartmentId(departmentId);
        userExt.setPositionId(positionId);

        String url = request.getRequestURL().toString();

        // 保存用户扩展信息
        if (url.contains("/admin/user/save")) {
            if (StringUtils.isEmpty(username)) {
                return;
            }

            SecUser user = userService.getByUsername(username);
            if (user == null) {
                return;
            }

            userExt.setUserId(user.getId());
            userExtService.insert(userExt);
            return;
        }

        // 更新用户扩展信息
        if (url.contains("/admin/user/update")) {
            if (userId <= 0) {
                return;
            }
            userExtService.updateByUserId(userExt);
            return;
        }

        // 删除用户扩展信息
        if (url.contains("/admin/user/delete")) {
            if (userId <= 0) {
                return;
            }
            userExtService.deleteByUserId(userId);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        return true;
    }
}
