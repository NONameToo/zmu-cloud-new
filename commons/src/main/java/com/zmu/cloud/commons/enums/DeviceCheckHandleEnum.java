package com.zmu.cloud.commons.enums;

import lombok.Getter;


@Getter
public enum DeviceCheckHandleEnum {
    AUTO(0, "自动"),
    PERSON(1, "手动"),
    ;

    private int status;
    private String desc;

    DeviceCheckHandleEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static DeviceCheckHandleEnum getStatus(int status) {
        DeviceCheckHandleEnum[] values = DeviceCheckHandleEnum.values();
        for (DeviceCheckHandleEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return AUTO;
    }
}
