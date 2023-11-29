package com.zmu.cloud.commons.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 固件升级进度
 * @author YH
 */
@Getter
@AllArgsConstructor
public enum UpgradeSchedule {
    connect("建立连接"),
    sendFirmwareParam("发送固件参数"),
    sendFirmware("发送固件包"),
    completed("升级完成"),
    exception("升级异常"),
    timeout("升级超时");

    private String desc;

}
