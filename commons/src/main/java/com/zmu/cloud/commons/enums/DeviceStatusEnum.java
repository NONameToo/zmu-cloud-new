package com.zmu.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author YH
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum DeviceStatusEnum {

    Init(0, "初始化"),
    Standby(1, "待机"),
    Measure(2, "测量中"),
    Dust(3, "扫灰中"),
    Upgrade(4, "升级中"),
    Other(5, "未知");

    private int type;
    private String desc;

    public static DeviceStatusEnum val(int type) {
        Optional<DeviceStatusEnum> statusEnum = Arrays.stream(DeviceStatusEnum.values()).filter(s -> s.type == type).findFirst();
        return statusEnum.orElse(Other);
    }
}
