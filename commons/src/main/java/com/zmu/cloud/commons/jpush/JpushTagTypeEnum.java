package com.zmu.cloud.commons.jpush;

import lombok.Getter;

/**
 *
 * @DESCRIPTION: 消息推送类型(tag标签)枚举
 */

@Getter
public enum JpushTagTypeEnum {

    /***/
    MATING("Mating", "待配种"),
    PREGNANCY("Pregnancy","待妊检"),
    LABOR("Labor", "待分娩"),
    WEANED("Weaned", "待断奶"),
    TOWER("Tower", "料塔下料"),
    DATAWARNING("DataWarning", "流量不足"),
    BALANCEWARNING("BalanceWarning", "余额不足"),


    ;

    private String type;
    private String desc;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    JpushTagTypeEnum(String type, String desc) {
        this.desc = desc;
        this.type = type;
    }
}
