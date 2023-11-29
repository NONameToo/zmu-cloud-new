package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @Description // 手续费比例,单位千分之)
 **/
@Getter
public enum PayOtherPercentEnum {
    ALIPAY(6,"支付宝手续费比例"),
    WECHAT(10,"微信手续费比例"),
    COMPANY(0,"对公转账手续费比例"),
    BALANCE(0,"余额扣款手续费比例"),
    ;

    private Integer percent;
    private String desc;

    PayOtherPercentEnum(Integer percent, String desc) {
        this.percent = percent;
        this.desc = desc;
    }

    public static PayOtherPercentEnum getPercent(Integer percent) {
        PayOtherPercentEnum[] values = PayOtherPercentEnum.values();
        for (PayOtherPercentEnum percentEnum : values) {
            if (percentEnum.getPercent() == percent)
                return percentEnum;
        }
        return COMPANY;
    }
}
