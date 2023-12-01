package com.zmu.cloud.commons.card;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微妙物联卡配置自动配置类参数
 *
 */
@ConfigurationProperties(prefix = "card")
@Data
public class CardProperties {
    private String url;

    private String appKey;

    private String secret;
}
