package com.sunmnet.bigdata.web.security.config;

import com.sunmnet.bigdata.web.security.authentication.DbUserDetailService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Web安全配置类
 */
@SuppressWarnings("deprecation")
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private ApplicationContext ctx;

    @Autowired
    private Environment env;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(securityAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider securityAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(securityUserDetailsService());
        authenticationProvider.setPasswordEncoder(securityPasswordEncoder());
        authenticationProvider.setHideUserNotFoundExceptions(false);
        return authenticationProvider;
    }

    @Bean
    public UserDetailsService securityUserDetailsService() {
        DbUserDetailService userDetailsService = new DbUserDetailService();
        userDetailsService.setDataSource(ctx.getBean("securityDataSource", DataSource.class));
        userDetailsService.setAuthoritiesByUsernameQuery("select u.user_name, r.role_name from sec_user u, " +
                "sec_user_rel_role rel, sec_role r where u.id = rel.user_id and rel.role_id = r.id " +
                "and u.user_name = ?");
        userDetailsService.setUsersByUsernameQuery("SELECT id, user_name, user_password, name, sex, " +
                "1 AS enabled FROM sec_user WHERE user_name = ?");
        return userDetailsService;
    }

    @Bean
    public PasswordEncoder securityPasswordEncoder() {
        return new JWTPasswordEncoder();
    }

    @SuppressWarnings("rawtypes")
	@Override
    protected void configure(HttpSecurity http) throws Exception {

        ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry urlRegistry = http.authorizeRequests();
        String excludeUrls = this.env.getProperty("security.exclude-url");
        if(StringUtils.isNotEmpty(excludeUrls)) {
            String[] urls = excludeUrls.split("\\s*,\\s*");
            if(ArrayUtils.isNotEmpty(urls)) {
                urlRegistry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)urlRegistry.antMatchers(urls)).permitAll();
            }
        }

        (((((HttpSecurity)((((FormLoginConfigurer)((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)
                urlRegistry.antMatchers(new String[]{"/**"})).not().anonymous()
                .and()).formLogin().loginProcessingUrl(this.env.getRequiredProperty("security.login-url")))
                .usernameParameter("username").passwordParameter("password")
                .successHandler(this.ctx.getBean(AuthenticationSuccessHandler.class)))
                .failureHandler(this.ctx.getBean(AuthenticationFailureHandler.class)))
                .and()).logout().logoutUrl(this.env.getRequiredProperty("security.logout-url"))
                .logoutSuccessHandler(this.ctx.getBean(LogoutSuccessHandler.class)).deleteCookies(new String[]{"JSESSIONID"})
                .and()).rememberMe().userDetailsService(this.securityUserDetailsService())
                .key("ecffc51b-48d8-4b6b-9387-d640a62d4579")
                .tokenValiditySeconds((this.env.getRequiredProperty("security.remember-me.max-age", Integer.class)).intValue())
                .and()).exceptionHandling().authenticationEntryPoint(this.ctx.getBean(AuthenticationEntryPoint.class)).and()).csrf().disable();

//        http.authorizeRequests().antMatchers("/**").not().anonymous()
//                .and().formLogin().loginProcessingUrl(env.getRequiredProperty("security.login-url"))
//                    .usernameParameter("username").passwordParameter("password")
//                    .successHandler(ctx.getBean(AuthenticationSuccessHandler.class))
//                    .failureHandler(ctx.getBean(AuthenticationFailureHandler.class))
//                .and().logout().logoutUrl(env.getRequiredProperty("security.logout-url"))
//                    .logoutSuccessHandler(ctx.getBean(LogoutSuccessHandler.class)).deleteCookies("JSESSIONID")
//                .and().rememberMe().userDetailsService(securityUserDetailsService())
//                    .key("ecffc51b-48d8-4b6b-9387-d640a62d4579")
//                    .tokenValiditySeconds(env.getRequiredProperty("security.remember-me.max-age", Integer.class))
//                .and().exceptionHandling().authenticationEntryPoint(ctx.getBean(AuthenticationEntryPoint.class))
//                .and().csrf().disable();
    }
}
