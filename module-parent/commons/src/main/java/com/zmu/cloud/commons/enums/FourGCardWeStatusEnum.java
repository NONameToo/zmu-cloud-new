package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @Description // 我们系统4G卡状态:1 正常(使用中) 2流量不足以及流量用超停机 3 已停机(流量用超) 4 已停机(套餐到期) 5 已失效 6本月到期(待充月基础套餐)
 **/
@Getter
public enum FourGCardWeStatusEnum {
    RUNNING(1,"正常(使用中)"),
    WARNINGDATE(2,"流量不足以及流量用超停机"),
    OVERFLOW(3, "已停机(流量用超)"),
    TIMEEND(4, "已停机(套餐到期)"),
    SHIT(5, "已失效"),
    CURRENTMONTHTIMEOUT(6, "本月到期(待充月基础套餐)"),
    Enable(7, "除未激活和作废的卡片以外的所有卡"),
    ;

    private int status;
    private String desc;

    FourGCardWeStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static FourGCardWeStatusEnum getStatus(int status) {
        FourGCardWeStatusEnum[] values = FourGCardWeStatusEnum.values();
        for (FourGCardWeStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return RUNNING;
    }
}
