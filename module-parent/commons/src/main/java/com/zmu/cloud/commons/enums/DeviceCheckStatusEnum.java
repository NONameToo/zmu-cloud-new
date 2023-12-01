package com.zmu.cloud.commons.enums;

import lombok.Getter;


@Getter
public enum DeviceCheckStatusEnum {

    TESTING(-2, "测量中"),
    READY(-1,"未开始"),
    NOT_PASS(0, "不通过"),
    PASS(1, "通过"),
    INVALID(2, "无效测量"),
    CANCEL(3, "取消"),
    ;

    private int status;
    private String desc;

    DeviceCheckStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static DeviceCheckStatusEnum getStatus(int status) {
        DeviceCheckStatusEnum[] values = DeviceCheckStatusEnum.values();
        for (DeviceCheckStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return READY;
    }
}
