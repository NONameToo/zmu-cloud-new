package com.zmu.cloud.commons.config;

import com.zmu.cloud.commons.constants.CommonsConstants;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: EnvConfig
 * @Date 2019-11-25 9:46
 */
@Component
@Data
@Slf4j
public class EnvConfig {

    @Value("${spring.profiles.active}")
    private String env;
    @Value("${spring.application.name}")
    private String serviceName;
    @Autowired(required = false)
    private GitProperties gitProperties;

    public static String ACTIVE;

    @PostConstruct
    public void init() {
        ACTIVE = env;
        if (gitProperties != null) {
            String branch = gitProperties.getBranch();
            String commitMsg = gitProperties.get("commit.message.full");
            String time = gitProperties.get("commit.time");
            String id = gitProperties.get("commit.id");
            log.info("Git info：branch=[{}]，message=[{}]，commit.time=[{}]，commit.id=[{}]", branch, commitMsg, time, id);
        }
    }

    public static boolean isDev() {
        return CommonsConstants.Env.DEV.equalsIgnoreCase(ACTIVE);
    }

    public static boolean isTest() {
        return CommonsConstants.Env.TEST.equalsIgnoreCase(ACTIVE);
    }

    public static boolean isProd() {
        return CommonsConstants.Env.PROD.equalsIgnoreCase(ACTIVE);
    }

    public static boolean isDefault() {
        return CommonsConstants.Env.DEFAULT.equalsIgnoreCase(ACTIVE);
    }

}
