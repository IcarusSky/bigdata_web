package com.sunmnet.bigdata.web.security.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.security.config.SimpleAuthenticationManager;
import com.sunmnet.bigdata.web.security.service.SecUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qianleijava
 * @date 2020/1/8 11:31
 */
@RestController
@RequestMapping("/login")
public class AutoLoginController extends BaseController {

    @Autowired
    private SimpleAuthenticationManager authenticationManager;

    @Autowired
    private SecUserService secUserService;


    @PostMapping("/jz")
    public Object registerUser(@RequestParam(name = "userName", required = false) String userName,
                               @RequestParam(name = "token", required = false) String token,
                               HttpServletRequest request) {
        Object principal = null;
        // 使用name和password封装成为的token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, "!23qaz");
        try{
            authenticationToken.setDetails(new WebAuthenticationDetails(request));
            Authentication authenticatedUser = authenticationManager.authenticate(authenticationToken);

            // 存放authentication到SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            principal = authenticatedUser.getPrincipal();
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        } catch(Exception e ){
            e.printStackTrace();
            System.out.println("Authentication failed: " + e.getMessage());
            return buildErrJson(e.getMessage());
        }
        return buildSuccJson(principal);
    }
}
