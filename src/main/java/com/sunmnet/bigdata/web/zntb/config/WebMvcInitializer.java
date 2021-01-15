package com.sunmnet.bigdata.web.zntb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;

/**
 * Spring Boot MVC dispatcherServlet配置，和传统web.xml配置方式效果一样，如下：
 * <pre class="code">
 * {@code
 * <servlet>
 *   <servlet-name>dispatcher</servlet-name>
 *   <servlet-class>
 *     org.springframework.web.servlet.DispatcherServlet
 *   </servlet-class>
 *   <init-param>
 *     <param-name>contextConfigLocation</param-name>
 *     <param-value>/WEB-INF/spring/dispatcher-config.xml</param-value>
 *   </init-param>
 *   <load-on-startup>1</load-on-startup>
 * </servlet>
 *
 * <servlet-mapping>
 *   <servlet-name>dispatcher</servlet-name>
 *   <url-pattern>/</url-pattern>
 * </servlet-mapping>}</pre>
 */
@Component
public class WebMvcInitializer implements ServletContextInitializer {

    @Autowired
    private Environment env;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Create the dispatcher servlet's Spring application context
        AnnotationConfigWebApplicationContext webApplicationContext =
                new AnnotationConfigWebApplicationContext();

        // Register and map the dispatcher servlet
        ServletRegistration.Dynamic dispatcherServlet =
                servletContext.addServlet("dispatcherServlet", new DispatcherServlet(webApplicationContext));
        dispatcherServlet.setLoadOnStartup(1);
        dispatcherServlet.addMapping(env.getRequiredProperty("web.mvc.dispatcher-servlet.mapping"));

        // 文件上传相关配置
        dispatcherServlet.setMultipartConfig(new MultipartConfigElement(
                env.getRequiredProperty("web.mvc.multipart.location"),
                env.getRequiredProperty("web.mvc.multipart.max-file-size", Long.class),
                env.getRequiredProperty("web.mvc.multipart.max-request-size", Long.class),
                env.getRequiredProperty("web.mvc.multipart.file-size-threshold", Integer.class)
        ));

        // Session cookie config
        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
        sessionCookieConfig.setMaxAge(env.getRequiredProperty("web.server.session.cookie.max-age", Integer.class));
    }
}
