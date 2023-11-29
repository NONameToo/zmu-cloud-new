package com.zmu.cloud.commons.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum ObjectOfImmuneAndPreventionEnum {

    PigHouse("1", "栋舍"),
    PigGroup("2", "猪群"),
    PigBreeding("3", "种猪");

    private String type;
    private String msg;

    ObjectOfImmuneAndPreventionEnum(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public static ObjectOfImmuneAndPreventionEnum matchByType(String type) {
        for (ObjectOfImmuneAndPreventionEnum value : ObjectOfImmuneAndPreventionEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}