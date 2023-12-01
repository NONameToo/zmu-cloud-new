package com.zmu.cloud.commons.enums;

import lombok.Getter;


@Getter
public enum DeviceInitCheckStatusEnum {


    CHECKING(0, "校验中"),
    ERR(1, "异常"),
    NORMAL(2, "正常"),
    ;

    private int status;
    private String desc;

    DeviceInitCheckStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static DeviceInitCheckStatusEnum getStatus(int status) {
        DeviceInitCheckStatusEnum[] values = DeviceInitCheckStatusEnum.values();
        for (DeviceInitCheckStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return null;
    }
}
