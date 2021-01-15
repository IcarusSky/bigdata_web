package com.sunmnet.bigdata.web.security.authentication;

import com.sunmnet.bigdata.web.core.security.authentication.AuthenticationHolder;
import com.sunmnet.bigdata.web.core.security.userdetails.UserDetails;
import com.sunmnet.bigdata.web.security.model.dto.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationHolder implements AuthenticationHolder {
    @Override
    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (!(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }
}
