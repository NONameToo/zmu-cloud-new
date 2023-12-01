package com.zmu.cloud.commons.locale;

import com.zmu.cloud.commons.exception.ErrorMsgEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Created by shining on 2018/10/24.
 */
@Configuration
public class LocaleMessage {

    private static MessageSource messageSource;

    @Autowired
    public LocaleMessage(MessageSource messageSource) {
        LocaleMessage.messageSource = messageSource;
    }

    /**
     * @param key：对应文本配置的key.
     * @return 对应地区的语言消息字符串
     */
    public static String getMessage(String key) {
        return messageSource.getMessage(key, new Object[]{}, key, LocaleContextHolder.getLocale());
    }

    public static String getMessage(ErrorMsgEnum messageEnum) {
        return messageSource.getMessage(messageEnum.toString(), new Object[]{}, messageEnum.getMsg(), LocaleContextHolder.getLocale());
    }
}