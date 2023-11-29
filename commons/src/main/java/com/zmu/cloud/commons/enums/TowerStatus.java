package com.zmu.cloud.commons.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author YH
 */
@ApiModel("料塔设备测量状态")
public enum TowerStatus {

    @ApiModelProperty("设备离线或未获取到网络状态")
    nothing,
    @ApiModelProperty("启动中：已发送启动命令，且未收到设备确认启动命令")
    starting,
    @ApiModelProperty("运行中：收到设备确认启动命令")
    running,
    @ApiModelProperty("测量正常完成")
    completed,
    @ApiModelProperty("设备异常")
    exception,
    @ApiModelProperty("手动取消测量")
    cancel,
    @ApiModelProperty("无效测量")
    invalid
}
