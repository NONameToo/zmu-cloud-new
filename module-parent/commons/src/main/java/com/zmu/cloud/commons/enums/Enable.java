package com.zmu.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 启动、停止
 * @author YH
 */

@Getter
@AllArgsConstructor
public enum Enable {

    ON(1), OFF(0);

    private int val;

}
