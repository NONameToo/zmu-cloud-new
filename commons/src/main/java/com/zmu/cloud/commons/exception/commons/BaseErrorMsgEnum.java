package com.zmu.cloud.commons.exception.commons;

import com.zmu.cloud.commons.exception.ErrorMsgEnum;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: LoginRequiredEnum
 * @Author
 * @Date 2019-07-09 14:51
 */
@Getter
public enum BaseErrorMsgEnum implements ErrorMsgEnum {
    /**
     * 地址已在其他设备登陆
     */
    KICK_OUT(401, "该账号已在其他设备登录"),
    /*请先登录*/
    LOGIN_REQUIRED(401, "请先登录"),
    LOGIN_TOKEN_EXPIRED(401, "登录状态过期，请重新登录"),

    ;

    private int code;
    private String msg;

    BaseErrorMsgEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
