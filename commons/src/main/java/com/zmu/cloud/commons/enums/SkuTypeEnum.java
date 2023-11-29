package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @Description // 物联卡套餐类型   2月套餐  6加油包
 **/
@Getter
public enum SkuTypeEnum {

    BASE(2,"月套餐"),
    EXT(6,"加油包"),
    ;

    private int type;
    private String desc;

    SkuTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static SkuTypeEnum getStatus(int type) {
        SkuTypeEnum[] values = SkuTypeEnum.values();
        for (SkuTypeEnum typeEnum : values) {
            if (typeEnum.getType() == type)
                return typeEnum;
        }
        return BASE;
    }
}
