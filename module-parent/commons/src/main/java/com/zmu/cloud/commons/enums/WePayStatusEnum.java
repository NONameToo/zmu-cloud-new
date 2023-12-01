package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @Description // 我方系统处理状态:1待处理,2处理中,3处理完成
 **/
@Getter
public enum WePayStatusEnum {

    PENDING(1,"待处理"),
    PROCESSING(2,"处理中"),
    PROCESSED(3, "已完成"),
    ;

    private int status;
    private String desc;

    WePayStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static WePayStatusEnum getStatus(int status) {
        WePayStatusEnum[] values = WePayStatusEnum.values();
        for (WePayStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return PENDING;
    }
}
