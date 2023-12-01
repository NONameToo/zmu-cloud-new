package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打印机表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-Printer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Printer {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 打印机名
     */
    @ApiModelProperty(value = "打印机名")
    private String printerName;

    /**
     * 打印机ip
     */
    @ApiModelProperty(value = "打印机ip")
    private String printerIp;

    /**
     * 打印机端口
     */
    @ApiModelProperty(value = "打印机端口")
    private Integer printerPort;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
}