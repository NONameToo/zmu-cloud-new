package com.zmu.cloud.commons.enums.app;


import com.zmu.cloud.commons.exception.ErrorMsgEnum;

public enum ErrorMessageEnum implements ErrorMsgEnum {

     /***/
    PHONE_ERROR(3001, "手机号格式错误"),

    VERIFY_CODE_INVALID(3002, "验证码错误"),

    USER_FARM_ACCESS_DENIED(3003,"您没有权限访问该猪场"),

    ;

    private int code;
    private String msg;

    ErrorMessageEnum(int code, String msg) {
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
