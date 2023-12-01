package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 校准平均值记录表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-DeviceInitCheck")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInitCheck {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 料塔id
     */
    @ApiModelProperty(value = "料塔id")
    private Long towerId;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceNum;

    /**
     * 应运行次数
     */
    @ApiModelProperty(value = "应运行次数")
    private Integer checkCount;

    /**
     * 已运行次数
     */
    @ApiModelProperty(value = "已运行次数")
    private Integer runCount;

    /**
     * 运行出错次数
     */
    @ApiModelProperty(value = "运行出错次数")
    private Integer errCount;

    /**
     * 检测开始时间
     */
    @ApiModelProperty(value = "校准开始时间")
    private LocalDateTime startTime;

    /**
     * 检测结束时间
     */
    @ApiModelProperty(value = "校准结束时间")
    private LocalDateTime endTime;

    /**
     * 平均体积(单位 立方厘米)
     */
    @ApiModelProperty(value = "平均体积(单位 立方厘米)")
    private Long volume;

    /**
     * 校准状态  0校准中  1异常 2正常
     */
    @ApiModelProperty(value = "校准状态  0校准中  1异常 2正常")
    private Integer checkStatus;


    /**
     * 公司
     */
    @ApiModelProperty(value = "公司")
    private Long companyId;

    /**
     * 猪场
     */
    @ApiModelProperty(value = "猪场")
    private Long pigFarmId;

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

    /**
     * 结果处理(0自动,1手动)
     */
    @ApiModelProperty(value = "结果处理(0自动,1手动)")
    private Integer handle;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}