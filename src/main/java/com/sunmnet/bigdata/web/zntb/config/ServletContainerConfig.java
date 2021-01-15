package com.sunmnet.bigdata.web.zntb.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.webresources.StandardRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.sunmnet.bigdata.web.core.filter.LogFilter;

/**
 * 配置SpringBoot中内嵌的Servlet容器
 */
@SpringBootConfiguration
public class ServletContainerConfig {

    @Autowired
    private Environment env;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(env.getRequiredProperty("web.server.port", Integer.class));
        factory.setSessionTimeout(env.getRequiredProperty("web.server.session.timeout-in-minutes", Integer.class),
                TimeUnit.MINUTES);


        // Tomcat自定义配置
        List<TomcatContextCustomizer> contextCustomizers = new ArrayList<>();

        // Tomcat守护线程执行backgroundProcess时间间隔（单位：秒），可以定期清理过期Session
        contextCustomizers.add(context -> context.setBackgroundProcessorDelay(10));

        // 设置应用资源缓存开关为关闭
        contextCustomizers.add(context -> {
            StandardRoot standardRoot = new StandardRoot(context);
            standardRoot.setCachingAllowed(false);
            context.setResources(standardRoot);
        });

        factory.setTomcatContextCustomizers(contextCustomizers);
        return factory;
    }

    @Bean
    public FilterRegistrationBean characterEncodingFilter() {
        FilterRegistrationBean characterEncodingFilter = new FilterRegistrationBean();
        characterEncodingFilter.setName("characterEncodingFilter");
        characterEncodingFilter.setFilter(new CharacterEncodingFilter());
        characterEncodingFilter.addUrlPatterns("/*");

        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("encoding", "UTF-8");
        initParameters.put("forceEncoding", "true");
        characterEncodingFilter.setInitParameters(initParameters);

        return characterEncodingFilter;
    }

    @Bean
    public FilterRegistrationBean webLogFilter() {
        FilterRegistrationBean webLogFilter = new FilterRegistrationBean();
        webLogFilter.setName("webLogFilter");
        webLogFilter.setFilter(new LogFilter());
        webLogFilter.addUrlPatterns("/*");
        return webLogFilter;
    }
    
}
