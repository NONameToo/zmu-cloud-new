package com.zmu.cloud.commons.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YH
 */
@Getter
@AllArgsConstructor
public enum VarietyEnum {
//    put("长白", 1);
//    put("大白", 2);
//    put("二元", 3);
//    put("三元", 4);
//    put("土猪", 5);
//    put("大长", 6);
//    put("杜洛克", 7);
//    put("皮杜", 8);

    ChangBai(1, "长白"),
    DaBai(2, "大白"),
    ErYuan(3, "二元"),
    SanYuan(4, "三元"),
    TuZhu(5, "土猪"),
    DaChang(6, "大长"),
    DuLuoKe(7, "杜洛克"),
    PiDu(8, "皮杜");

    private Integer type;
    private String desc;

    public static VarietyEnum type(int type) {
        VarietyEnum[] values = VarietyEnum.values();
        for (VarietyEnum varietyEnum : values) {
            if (varietyEnum.getType() == type) {
                return varietyEnum;
            }
        }
        return null;
    }

    public static VarietyEnum desc(String desc) {
        VarietyEnum[] values = VarietyEnum.values();
        for (VarietyEnum varietyEnum : values) {
            if (ObjectUtil.equals(varietyEnum.getDesc(), desc)) {
                return varietyEnum;
            }
        }
        return null;
    }

}
