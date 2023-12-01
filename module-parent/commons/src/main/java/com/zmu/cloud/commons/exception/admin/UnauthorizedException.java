package com.zmu.cloud.commons.exception.admin;

import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.exception.ErrorMsgEnum;
import com.zmu.cloud.commons.locale.LocaleMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @Author gmail.com
 * @Date 2020-08-13 13:52
 */
@Getter
@Setter
public class UnauthorizedException extends BaseException {

    private static final int DEFAULT_ERROR_CODE = 401;

    private int code;

    private String msg;

    public UnauthorizedException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * @Description  不支持国际化
     */
    public UnauthorizedException(String message) {
        this(DEFAULT_ERROR_CODE, message);
    }

    /**
     * @Description 支持国际化
     */
    public UnauthorizedException(ErrorMsgEnum messageEnum) {
        this(messageEnum.getCode(), LocaleMessage.getMessage(messageEnum.getMsg()));
    }

}
