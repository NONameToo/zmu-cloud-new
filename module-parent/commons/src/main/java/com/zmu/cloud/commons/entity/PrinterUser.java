package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户和打印机关联表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PrinterUser")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrinterUser {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 打印机ID
     */
    @ApiModelProperty(value = "打印机ID")
    private Long printerId;

    @ApiModelProperty(value = "")
    private Integer isDefault;
}