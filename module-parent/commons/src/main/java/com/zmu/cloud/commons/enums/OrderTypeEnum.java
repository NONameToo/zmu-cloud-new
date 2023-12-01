package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @Description // 订单类型:1余额充值,2套餐购买(手动买月租/手动买加量包),3月租自动续费
 **/
@Getter
public enum OrderTypeEnum {

    CHARGE(1,"余额充值"),
    BUY(2, "套餐购买(月租/加量包)"),
    AUTO(3, "月租自动续费")
    ;

    private int type;
    private String desc;

    OrderTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static OrderTypeEnum getType(int type) {
        OrderTypeEnum[] values = OrderTypeEnum.values();
        for (OrderTypeEnum typeEnum : values) {
            if (typeEnum.getType() == type)
                return typeEnum;
        }
        return CHARGE;
    }
}
