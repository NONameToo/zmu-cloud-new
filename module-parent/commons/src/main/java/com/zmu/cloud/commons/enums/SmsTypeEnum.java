package com.zmu.cloud.commons.enums;

import lombok.Getter;

@Getter
public enum SmsTypeEnum {

    COMMON(0, "通用"),
    REGISTER(1, "注册"),
    FIND_LOGIN_PASSWORD(2, "找回登录密码"),

    ;
    private int type;
    private String desc;

    SmsTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static SmsTypeEnum getInstance(int type) {
        for (SmsTypeEnum value : SmsTypeEnum.values()) {
            if (value.getType() == type)
                return value;
        }
        throw new RuntimeException("错误的短信类型：" + type);
    }

}
