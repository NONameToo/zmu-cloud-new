package com.zmu.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 料塔关联类型（除栋舍外的）
 * @author YH
 */
@Getter
@AllArgsConstructor
public enum AssociationType {

    ZZLT("中转料塔");

    private String type;

}
