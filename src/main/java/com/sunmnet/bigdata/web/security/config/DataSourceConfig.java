package com.sunmnet.bigdata.web.security.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import com.google.common.collect.Lists;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Web基础数据源配置类
 */
@Configuration("securityDataSourceConfig")
@EnableTransactionManagement
@MapperScan(
        basePackages = {
                "com.sunmnet.bigdata.web.security.persistent"
        },
        sqlSessionFactoryRef = "securitySqlSessionFactory"
)
public class DataSourceConfig {

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationContext ctx;

    @Bean
    public PlatformTransactionManager securityTxManager() {
        return new DataSourceTransactionManager(securityDataSource());
    }

    @Bean
    public SqlSessionFactory securitySqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(securityDataSource());

        // Mybatis Config
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);

        // Mybatis Mapper XML Config
        Resource[] resourceArray = ctx.getResources("classpath*:/mybatis/mappers/security/*.xml");
        sessionFactory.setMapperLocations(resourceArray);

        // Mybatis Plugins
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties pageInterceptorProperties = new Properties();
        pageInterceptorProperties.setProperty("helperDialect", "mysql");
        pageInterceptorProperties.setProperty("reasonable", "true");
        pageInterceptor.setProperties(pageInterceptorProperties);

        Interceptor[] plugins = new Interceptor[]{
                pageInterceptor
        };
        sessionFactory.setPlugins(plugins);

        return sessionFactory.getObject();
    }

    @Bean
    public DataSource securityDataSource() {
        DruidDataSource dataSource = new DruidDataSource();

        // 基础配置
        dataSource.setName("Security");
        dataSource.setDriverClassName(env.getRequiredProperty("security.jdbc.driver"));
        dataSource.setUrl(env.getRequiredProperty("security.jdbc.url"));
        dataSource.setUsername(env.getRequiredProperty("security.jdbc.username"));
        dataSource.setPassword(env.getRequiredProperty("security.jdbc.password"));

        // 配置初始化大小、最小、最大
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(20);

        // 配置获取连接等待超时的时间，单位是毫秒
        dataSource.setMaxWait(60000);

        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(60000);

        // 配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(300000);

        // 配置连接存活测试
        dataSource.setValidationQuery("select 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);

        dataSource.setPoolPreparedStatements(false);
        dataSource.setProxyFilters(Lists.newArrayList(securitySqlLogFilter()));
        return dataSource;
    }

    @Bean
    public Filter securitySqlLogFilter() {
        Slf4jLogFilter logFilter = new Slf4jLogFilter();
        logFilter.setStatementCreateAfterLogEnabled(false);
        logFilter.setStatementCloseAfterLogEnabled(false);
        logFilter.setStatementParameterClearLogEnable(false);
        logFilter.setStatementPrepareAfterLogEnabled(false);
        logFilter.setStatementPrepareCallAfterLogEnabled(false);
        return logFilter;
    }
}
