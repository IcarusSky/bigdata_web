package com.sunmnet.bigdata.web.zntb.config;

import com.sunmnet.bigdata.web.zntb.interceptor.SecurityInterceptor;
import com.sunmnet.bigdata.web.zntb.interceptor.UserExtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Web MVC配置类
 */
@SpringBootConfiguration
@EnableWebMvc
@ComponentScan(
        basePackages = {"com.sunmnet.bigdata"},
        useDefaultFilters = false,
        includeFilters = {@ComponentScan.Filter(classes = Controller.class)}
)
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private UserExtInterceptor userExtInterceptor;

    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(userExtInterceptor).addPathPatterns("/admin/user/save",
                "/admin/user/update", "/admin/user/delete");
        registry.addInterceptor(securityInterceptor).addPathPatterns("/**").
                excludePathPatterns("/user/casLogin",
                        "/user/toCasLogin",
                        "/user/toLogin",
                        "/user/singleLogout",
                        "/user/checkSession");
    }
}
