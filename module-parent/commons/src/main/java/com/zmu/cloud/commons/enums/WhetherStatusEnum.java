package com.zmu.cloud.commons.enums;

import lombok.Getter;

@Getter
public enum WhetherStatusEnum {
    YES(1, "是"),
    NO(0, "否"),
    ;

    private int status;
    private String desc;

    WhetherStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static WhetherStatusEnum getStatus(int status) {
        WhetherStatusEnum[] values = WhetherStatusEnum.values();
        for (WhetherStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return YES;
    }
}
