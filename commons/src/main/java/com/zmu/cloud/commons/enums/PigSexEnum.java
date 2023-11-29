package com.zmu.cloud.commons.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YH
 */
@Getter
@AllArgsConstructor
public enum PigSexEnum {

    Boar(1, "公猪"),
    Sow(2, "母猪");

    private Integer type;
    private String desc;

    public static PigSexEnum type(int type) {
        PigSexEnum[] values = PigSexEnum.values();
        for (PigSexEnum sexEnum : values) {
            if (sexEnum.getType() == type) {
                return sexEnum;
            }
        }
        return null;
    }

    public static PigSexEnum desc(String desc) {
        PigSexEnum[] values = PigSexEnum.values();
        for (PigSexEnum sexEnum : values) {
            if (ObjectUtil.equals(sexEnum.getDesc(), desc)) {
                return sexEnum;
            }
        }
        return null;
    }
}
