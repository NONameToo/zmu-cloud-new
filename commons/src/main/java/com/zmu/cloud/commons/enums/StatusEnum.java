package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: UserTypeEnum
 * @Date 2019-04-01 21:34
 */
@Getter
public enum StatusEnum {

    HIDE(0,"隐藏"),
    DISPLAY(1, "显示"),
    ;

    private int status;
    private String desc;

    StatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static StatusEnum getStatus(int status) {
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return HIDE;
    }
}
