package com.zmu.cloud.commons.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * @author YH
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@ConditionalOnProperty(prefix = "spring.datasource", name = "type", havingValue = "com.alibaba.druid.pool.DruidDataSource")
public class DruidStatViewFilterConfig implements TransactionManagementConfigurer {

  @Qualifier("cloud")
  final DataSource cloudDataSource;

  @Bean
  public ServletRegistrationBean<StatViewServlet> druidStatViewServlet(
      @Value("${spring.datasource.druid.stat-view-servlet.login-username:admin}") String userName,
      @Value("${spring.datasource.druid.stat-view-servlet.login-password:admin@123}") String password) {
    ServletRegistrationBean<StatViewServlet> servletRegistrationBean =
        new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
    servletRegistrationBean.addInitParameter("loginUsername", userName);
    servletRegistrationBean.addInitParameter("loginPassword", password);
    servletRegistrationBean.addInitParameter("resetEnable", "true");
    log.info("druidStatViewServlet init with path /druid/index.html");
    return servletRegistrationBean;
  }

  @Bean
  public FilterRegistrationBean<WebStatFilter> druidStatFilter() {
    FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(new WebStatFilter());
    //添加过滤规则.
    filterRegistrationBean.addUrlPatterns("/*");
    //添加不需要忽略的格式信息.
    filterRegistrationBean.addInitParameter("exclusions",
        "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,/actuator,/actuator/*,/v2/*,/swagger/*,/webjars/*");
    return filterRegistrationBean;
  }

  @Bean
  @Override
  public PlatformTransactionManager annotationDrivenTransactionManager() {
    return new DataSourceTransactionManager(cloudDataSource);
  }

  @Bean(name = "jxJdbcTemplate")
  public JdbcTemplate jxJdbcTemplate() {
    return new JdbcTemplate(((DynamicRoutingDataSource) cloudDataSource).getDataSource("jx"));
  }
}
