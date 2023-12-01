package com.zmu.cloud.commons.dinger;

import com.github.jaemon.dinger.core.DingerConfigurerAdapter;
import com.github.jaemon.dinger.support.CustomMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

@Configuration
@Slf4j
public class MyDingerConfiguration extends DingerConfigurerAdapter {


    // 自定义text类型消息体，仅限手动发送功能，不适用于xml标签或注解统一管理消息体功能
    @Bean
    public CustomMessage textMessage() {
        return (projectId, request) ->
                MessageFormat.format(
                        "【通知】{0}", request.getContent());
    }

    // 自定义markdown类型消息体，仅限手动发送功能，不适用于xml标签或注解统一管理消息体功能
    @Bean
    public CustomMessage markDownMessage() {
        return (projectId, request) ->
                MessageFormat.format(
                        "#### 【通知】{0}", request.getContent());
    }
}