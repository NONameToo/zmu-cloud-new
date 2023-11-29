package com.zmu.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author YH
 */
@AllArgsConstructor
@NoArgsConstructor
public enum TowerTabEnum {

    Normal("正常"),
    UnBind("未绑定"),
    UnAssociation("未关联"),
    UnCalibration("未校准"),
    MeasureIn("测量中"),
    CalibrationIn("校准中")
    ;

    private String desc;

}
