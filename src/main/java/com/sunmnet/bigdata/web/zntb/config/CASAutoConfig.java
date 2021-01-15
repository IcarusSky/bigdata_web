package com.sunmnet.bigdata.web.zntb.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 *
 * @Author Oumuv
 * @Date 2018/10/23 16:59
 **/
@ConfigurationProperties(prefix = "cas")
@Component
public class CASAutoConfig {
   @Value("${cas.server-url-prefix}")
    private String serverUrlPrefix;
   @Value("${cas.server-login-url}")
    private String serverLoginUrl;
   @Value("${cas.client-host-url}")
    private String clientHostUrl;

    public String getServerUrlPrefix() {
		return serverUrlPrefix;
	}

	public void setServerUrlPrefix(String serverUrlPrefix) {
		this.serverUrlPrefix = serverUrlPrefix;
	}

	public String getServerLoginUrl() {
		return serverLoginUrl;
	}

	public void setServerLoginUrl(String serverLoginUrl) {
		this.serverLoginUrl = serverLoginUrl;
	}

	public String getClientHostUrl() {
		return clientHostUrl;
	}

	public void setClientHostUrl(String clientHostUrl) {
		this.clientHostUrl = clientHostUrl;
	}

	/**
     * 授权过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean filterAuthenticationRegistration(HttpSession session) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AuthenticationFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/bigdata/user/casLogin");//,"/bigdata/user/logout,"
        Map<String,String> initParameters = new HashMap<String, String>();
        initParameters.put("casServerLoginUrl", serverUrlPrefix);
        initParameters.put("serverName", clientHostUrl);
        //忽略的url，"|"分隔多个url
        initParameters.put("ignorePattern", "/bigdata/user/singleLogout|/bigdata/user/toLogin");

        registration.setInitParameters(initParameters);
        // 设定加载的顺序
        registration.setOrder(1);
        return registration;
    }

}
