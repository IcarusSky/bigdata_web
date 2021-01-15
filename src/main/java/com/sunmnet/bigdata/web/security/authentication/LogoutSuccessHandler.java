package com.sunmnet.bigdata.web.security.authentication;

import com.sunmnet.bigdata.web.core.model.dto.JsonResult;
import com.sunmnet.bigdata.web.core.util.WebUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出登录处理
 */
@Component
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        JsonResult data = new JsonResult(true, "退出成功");
        WebUtils.writeJSON(response, data);
    }
}
