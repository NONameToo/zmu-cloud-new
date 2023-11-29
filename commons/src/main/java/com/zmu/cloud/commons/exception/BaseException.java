package com.zmu.cloud.commons.exception;

import com.zmu.cloud.commons.locale.LocaleMessage;
import com.zmu.cloud.commons.utils.ExceptionUtils;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @author YH
 * @DESCRIPTION: BaseException
 * @Date 2018-12-01 23:39
 */

@Data
@Slf4j
/**
 * 重要！！！
 * 重要！！！
 * 重要！！！
 *  所有手动抛的自定义异常类都应该继承此类  或者 直接使用此类
 */
public class BaseException extends RuntimeException implements CustomExceptionMsg {
    private static final long serialVersionUID = 2728349477671832429L;

    private static final int DEFAULT_ERROR_CODE = 1000;

    private int code;

    private String msg;

    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * @Description 不支持国际化
     */
    public BaseException(String message) {
        this(DEFAULT_ERROR_CODE, message);
    }

    /**
     * @Description 支持国际化
     */
    public BaseException(ErrorMsgEnum messageEnum) {
        this(messageEnum.getCode(), LocaleMessage.getMessage(messageEnum));
    }

    /**
     * 支持占位符
     */
    public BaseException(ErrorMsgEnum messageEnum, Object... args) {
        this(messageEnum.getCode(), String.format(LocaleMessage.getMessage(messageEnum), args));
    }

    public BaseException(String msg, Object... args) {
        this(DEFAULT_ERROR_CODE, String.format(msg, args));
    }

    @Override
    public BaseException fillInStackTrace() {
        String message = ExceptionUtils.getExceptionMessageDetail(super.fillInStackTrace());
        log.error("{} --> {}", message, this.getClass().getCanonicalName());
        RequestContextUtils.getRequestInfo().getMap().put(EXCEPTION_MESSAGE_DETAIL, message);
        super.setStackTrace(new StackTraceElement[]{});
        return this;
    }
}
