package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 *
 * @author YH
 * @DESCRIPTION: 料塔日志操作状态
 */

@Getter
public enum TowerLogStatusEnum {

    /***/
    USE(-1, "用料"),
    ADD(1, "补料"),
    ERROR(2,"异常"),
    CANCEL(0,"取消");

    private Integer type;
    private String desc;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    TowerLogStatusEnum(Integer type, String desc) {
        this.desc = desc;
        this.type = type;
    }
}
