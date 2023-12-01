package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @Description // 物联卡套餐生效类型 0当月生效  1次月生效
 **/
@Getter
public enum SkuChargeTypeEnum {

    CURRENT("0","当月生效"),
    NEXT("1","次月生效"),
    ;

    private String type;
    private String desc;

    SkuChargeTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static SkuChargeTypeEnum getStatus(String type) {
        SkuChargeTypeEnum[] values = SkuChargeTypeEnum.values();
        for (SkuChargeTypeEnum typeEnum : values) {
            if (typeEnum.getType() == type)
                return typeEnum;
        }
        return CURRENT;
    }
}
