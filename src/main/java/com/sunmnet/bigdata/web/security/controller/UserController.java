package com.sunmnet.bigdata.web.security.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.security.model.po.SecUser;
import com.sunmnet.bigdata.web.security.service.SecUserService;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private SecUserService userService;

    @ResponseBody
    @GetMapping(value = "/checkSession")
    public Object checkSession() {
        if (authenticationHolder.getAuthenticatedUser() == null) {
            return buildErrJson(HttpServletResponse.SC_FORBIDDEN, "用户会话失效");
        }
        return buildSuccJson();
    }


     /*
        以下为统一登录部分
     */

    @GetMapping("/casLogin")
    public void  casLogin(HttpServletRequest request, HttpServletResponse response) {
        Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        if (assertion != null) {
            AttributePrincipal principal = assertion.getPrincipal();
            String username = principal.getName();//账号
            try {
                SecUser userInfo = userService.getByUsername(username);
                String password="";
                if(userInfo != null){
                    password=userInfo.getUserPassword();
                }

                //重定向至前端页面，前端页面根据判断token是否存在，根据拿到的token执行系统自身登录逻辑
                response.sendRedirect("http://10.77.100.10:9097/#/loginMiddle?username="+username+"&password="+password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                response.sendRedirect("http://10.77.100.10:9097/#/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/singleLogout", method = {RequestMethod.GET,RequestMethod.POST})
    public void singleLogout(HttpServletRequest request,HttpServletResponse response) {
        request.getSession(true).removeAttribute("user");
        String casLogoutURL = "http://authserver.tsgzy.edu.cn/authserver/logout";
        String redirectURL=casLogoutURL+"?service="+ URLEncoder.encode("http://jc.tsgzy.edu.cn:9095/bigdata/user/toLogin");
        try {

            request.getSession(true).invalidate();
            request.getSession().invalidate();
            //清除cookie
            clearCookie(request,response);

            //重定向至前端页面到金智登出页面
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/toLogin", method = {RequestMethod.GET,RequestMethod.POST})
    public void toLogin(HttpServletRequest request,HttpServletResponse response) {
        try {
            //重定向至前端页面到金智登出页面
            response.sendRedirect("http://jc.tsgzy.edu.cn:9095/bigdata/user/casLogin");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * 功能描述: 清除cookie
     *
     */
    @GetMapping("/clearCookie")
    public void clearCookie (HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }
}
