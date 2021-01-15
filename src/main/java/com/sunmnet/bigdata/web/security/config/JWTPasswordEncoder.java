package com.sunmnet.bigdata.web.security.config;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;


public class JWTPasswordEncoder extends Md5PasswordEncoder  {

    @Override
    public boolean isPasswordValid(String s, String s1, Object o) {
        return true;
    }
}
