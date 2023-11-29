package com.zmu.cloud.commons.enums;

public enum ExtraEnum {
    MANUAL_RECHARGE("admin", "用户ID"),
    MANUAL_MINUS("admin", "用户ID"),
    AUDIT_SUCCESS("admin", "审核单ID"),
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

    ExtraEnum(String type, String desc) {
        this.desc = desc;
        this.type = type;
    }
}
