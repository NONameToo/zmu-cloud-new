package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @Description // 用户支付状态:1未付款,2已付款,已取消,已失效
 **/
@Getter
public enum PayStatusEnum {

    NOTPAY(1,"未付款"),
    FINISHPAY(2, "已付款"),
    CANCEL(3, "已取消"),
    TIMEOUT(4, "已失效")
    ;

    private int status;
    private String desc;

    PayStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static PayStatusEnum getStatus(int status) {
        PayStatusEnum[] values = PayStatusEnum.values();
        for (PayStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return NOTPAY;
    }
}
