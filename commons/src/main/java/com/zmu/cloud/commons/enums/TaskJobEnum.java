package com.zmu.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * QuartzJob分組
 * @author YH
 */
@Getter
@AllArgsConstructor
public enum TaskJobEnum {

    Feeder("Feeder", "FeederJob", "饲喂器Quartz定时任务"),
    Tower("Tower", "TowerJob", "料塔Quartz定时任务"),
    TowerFirmwareUpgrade("TowerFirmwareUpgrade", "TowerFirmwareUpgradeJob", "料塔设备固件升级定时任务"),
    FeederMasterFirmwareUpgrade("FeederMasterFirmwareUpgrade", "FeederMasterFirmwareUpgradeJob", "饲喂器主机固件升级定时任务");

    private String group;
    private String jobClass;
    private String desc;

}
