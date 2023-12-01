package com.zmu.cloud.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: GatewayConfig
 * @Date 2019-04-03 13:42
 */
@Data
@Component
@ConfigurationProperties(prefix = "commons.config", ignoreInvalidFields = true)
public class CommonsConfig {

    private Sms sms = new Sms();

    private ResolveError resolveError = new ResolveError();

    //------------------------
    @Data
    public static final class ResolveError {
        /**
         *  线上环境可以关闭
         * */
        private boolean showErrorDetail = true;

        private String cnTip = "服务繁忙，请稍后再试";

        private String enTip = HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();

    }

    @Data
    public static final class Sms {

        private Boolean testEnvNoVerify = true;

        private String codeTemplate = "您的验证码为：%s，5分钟内有效";

    }

}
