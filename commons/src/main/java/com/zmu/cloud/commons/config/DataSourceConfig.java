package com.zmu.cloud.commons.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 使用了动态数据源就不能再自定义配置
 * @author YH
 */
//@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "cloud")
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.cloud")
    public DruidDataSource cloudDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("slave")
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.slave")
    public DruidDataSource slaveDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("jx")
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.jx")
    public DruidDataSource jxDataSource() {
        return DruidDataSourceBuilder.create().build();
    }


    @Bean(name = "jxJdbcTemplate")
    public JdbcTemplate jxJdbcTemplate(@Qualifier("jx") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean("equipment")
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.equipment")
    public DruidDataSource equipmentDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

}
