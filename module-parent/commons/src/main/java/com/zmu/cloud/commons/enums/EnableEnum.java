package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: UserTypeEnum
 * @Date 2019-04-01 21:34
 */
@Getter
public enum EnableEnum {
    DISABLE(0, "未开启"),
    ENABLE(1, "已开启"),
    ;

    private int status;
    private String desc;

    EnableEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static EnableEnum getStatus(int status) {
        EnableEnum[] values = EnableEnum.values();
        for (EnableEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return ENABLE;
    }
}
