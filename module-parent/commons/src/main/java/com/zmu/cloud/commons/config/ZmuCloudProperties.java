package com.zmu.cloud.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author YH
 */
@Data
@Component
@ConfigurationProperties(prefix = "zmu.config", ignoreInvalidFields = true)
public class ZmuCloudProperties {

  private Config config = new Config();

  @Data
  public static final class Config {
    private String jwtSecret = "zmu-cloud-secret";
    private String tokenHeaderName = "zmu-cloud-token";
    private String sphTokenHeaderName = "Authorization";
    private Duration tokenExpireTime = Duration.ofDays(7);
    private String clientTypeHeaderName = "zmu-cloud-client-type";
    private String appVersionHeaderName = "zmu-cloud-app-version";
    private String pigFarmIdHeaderName = "pig-farm-id";
    private Integer defaultFeedingAmount = 2300;
    private String jxAppBaseUrl = "";
  }
}