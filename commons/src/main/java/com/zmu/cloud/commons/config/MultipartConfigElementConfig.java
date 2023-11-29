package com.zmu.cloud.commons.config;

import javax.servlet.MultipartConfigElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.unit.DataSize;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: MultipartConfigElementConfig
 * @Author
 * @Date 2019-04-01 20:57
 */
@ConditionalOnClass(MultipartConfigElement.class)
@Configuration
public class MultipartConfigElementConfig {

    @Value("${spring.servlet.multipart.maxFileSize:10MB}")
    private String max_file_size;
    @Value("${spring.servlet.multipart.maxRequestSize:10MB}")
    private String max_request_size;
    @Autowired
    private Environment environment;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize(DataSize.parse(max_file_size));
        /// 总上传数据大小
        factory.setMaxRequestSize(DataSize.parse(max_request_size));
        //临时文件上传地址
        if (StringUtils.isBlank(environment.getProperty("server.tomcat.basedir")))
            factory.setLocation("/data/apps/temp");
        return factory.createMultipartConfig();
    }
}
