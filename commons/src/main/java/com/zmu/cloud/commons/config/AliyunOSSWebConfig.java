package com.zmu.cloud.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置类
 *
 * @author liyi
 * @create 2018-01-18 13:39
 **/
@Component
@ConfigurationProperties(prefix = "oss", ignoreInvalidFields = true)
@Data
public class AliyunOSSWebConfig {

    private String accessKeyId;
    private String accessKeySecret;
    private String bucket;
    private String baseUrl;
    private String endpoint;
    private String callbackUrl;


    @Bean
    public AliyunOSS aliyunOSS(){
        return AliyunOSS.options()
                .setId(accessKeyId)
                .setSecret(accessKeySecret)
                .setBucket(bucket)
                .setBaseUrl(baseUrl)
                .setEndpoint(endpoint)
                .build();
    }
}
