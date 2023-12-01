package com.zmu.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author YH
 */

@Getter
@AllArgsConstructor
public enum HouseTypeForSph {

    SPH_GZ(513419, "公猪舍", 3),
    SPH_RS(500285, "妊娠舍", 5),
    SPH_FM(500286, "分娩舍", 2),
    SPH_BY(500287, "保育舍", 6),
    SPH_HB(500289, "后备舍", 4),
    SPH_PZ(531179, "配种舍", 1),
    SPH_RZ(513417, "肉猪舍", 7),
    SPH_QT(513418, "其它", 8)
    ;

    private Integer type;
    private String name;
    private Integer sort;

    public static String getInstance(int type) {
        return Arrays.stream(HouseTypeForSph.values())
                .filter(pigHouseTypeCodeEnum -> pigHouseTypeCodeEnum.type == type)
                .map(HouseTypeForSph::getName)
                .findFirst()
                .orElse(HouseType.getInstance(type).getName());
    }


}
