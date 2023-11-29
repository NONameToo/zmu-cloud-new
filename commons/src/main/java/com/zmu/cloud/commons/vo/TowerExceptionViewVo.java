package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

/**
 * @author zhaojian
 * @create 2023/10/17 9:33
 * @Description 设备异常提醒
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("设备异常提醒")
public class TowerExceptionViewVo {

    @ApiModelProperty("猪场名称")
    private String farmName;
    @ApiModelProperty("料塔名称")
    private String towerName;
    @ApiModelProperty("设备编号")
    private String deviceNo;
    @ApiModelProperty("异常状态")
    private String exception;
    @ApiModelProperty("时间")
    private LocalDateTime time;

}
