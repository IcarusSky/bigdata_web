package com.sunmnet.bigdata.web.security.authentication;

import com.sunmnet.bigdata.web.core.model.dto.JsonResult;
import com.sunmnet.bigdata.web.core.util.WebUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未登录处理
 */
@Component
public class LoginAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        JsonResult data = new JsonResult(false, "用户会话失效");
        data.setErrorCode(HttpServletResponse.SC_FORBIDDEN);
        WebUtils.writeJSON(response, data);
    }
}
