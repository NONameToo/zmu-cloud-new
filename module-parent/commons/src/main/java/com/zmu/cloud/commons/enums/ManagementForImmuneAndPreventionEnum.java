package com.zmu.cloud.commons.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum ManagementForImmuneAndPreventionEnum {

    Immune("1", "免疫"),
    Prevention("2", "防治");

    private String type;
    private String msg;

    ManagementForImmuneAndPreventionEnum(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public static ManagementForImmuneAndPreventionEnum matchByType(String type) {
        for (ManagementForImmuneAndPreventionEnum value : ManagementForImmuneAndPreventionEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}