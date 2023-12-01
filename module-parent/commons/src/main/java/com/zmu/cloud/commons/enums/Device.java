package com.zmu.cloud.commons.enums;

import lombok.Getter;

@Getter
public enum Device {

    feeder("admin"), //饲喂器
    switcher("switcher"), //电闸
    tower("tower");     //料塔

    private String alias;

    Device(String alias) {
        this.alias = alias;
    }

}
