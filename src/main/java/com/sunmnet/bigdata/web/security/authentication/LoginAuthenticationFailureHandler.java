package com.sunmnet.bigdata.web.security.authentication;

import com.sunmnet.bigdata.web.core.model.dto.JsonResult;
import com.sunmnet.bigdata.web.core.util.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录验证失败处理
 */
@Component
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String msg;
        if (exception instanceof UsernameNotFoundException) {
            msg = "用户名错误";
        } else if (exception instanceof BadCredentialsException) {
            msg = "密码错误";
        } else {
            msg = "登录失败";
        }

        JsonResult data = new JsonResult(false, msg);
        data.setErrorCode(-1);
        WebUtils.writeJSON(response, data);
    }
}
