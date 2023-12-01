package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @Description // 4G卡状态:1 可激活 2 已激活 3 已停用 4 已失效
 **/
@Getter
public enum FourGCardStatusEnum {
    ACTIVATE(1,"可激活"),
    ACTIVATED(2, "已激活"),
    DISABLED(3, "已停用"),
    STOP(4, "已失效"),
    ;

    private int status;
    private String desc;

    FourGCardStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static FourGCardStatusEnum getStatus(int status) {
        FourGCardStatusEnum[] values = FourGCardStatusEnum.values();
        for (FourGCardStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return ACTIVATE;
    }
}
