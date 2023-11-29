package com.zmu.cloud.commons.card;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 微妙物联卡配置自动配置类
 *
 */
@Configuration
@EnableConfigurationProperties(CardProperties.class)
public class WmIotCardConfig {

    private final CardProperties cardProperties;


    public WmIotCardConfig(CardProperties cardProperties) {
        this.cardProperties = cardProperties;
    }


    @Bean
    public CardApi cardApi() {
        final CardApi cardApi = new CardApi(cardProperties);
        cardApi.init();
        return cardApi;
    }
}
