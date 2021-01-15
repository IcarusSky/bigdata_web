package com.sunmnet.bigdata.web.zntb.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户登录会话验证、资源访问权限等安全相关拦截器
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception ex)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        HttpSession session = request.getSession(false);
        //判断是否有会话,没有就进行拦截.....
/*        if(session == null){
            String redirectURL = ("http://10.10.1.169:8991/bigdata/user/casLogin");
            response.sendRedirect(redirectURL);
            return false;
        }*/
        return true;

        /*
        if (session == null) {
            //查询cookie
            String token = CookieUtils.getValueByName("token", request);
            if (StringUtils.isBlank(token)) {
                String redirectURL = ("http://jc.tsgzy.edu.cn:9095/bigdata/user/toLogin");
                response.sendRedirect(redirectURL);
                return false;
            }
        }
        Object user = session.getAttribute("user");
        if (user == null) {
            //查询cookie
            String token = CookieUtils.getValueByName("token", request);
            if (StringUtils.isNotBlank(token)) {
                //查询token是否有效
                Map<String, String> params = new HashedMap();
                params.put("token", token);
                params.put("systemNo", "ZNTB");
                String redirectURL = "http://jc.tsgzy.edu.cn:9095/bigdata/user/toLogin";
                response.sendRedirect(redirectURL);
                return false;
                }
            }
          return true;
          */
        }


}

