package com.zmu.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YH
 */
@Getter
@AllArgsConstructor
public enum MeasureModeEnum {

    Auto("自动"),
    Manual("手动"),
    AddBefore("加料前测量"),
    AddAfter("加料后测量"),
    QualityInspection("质检"),
    Init("校准"),
    InitAverage("平均值校准"),
    Aging("老化"),
    ;

    private String desc;

}
