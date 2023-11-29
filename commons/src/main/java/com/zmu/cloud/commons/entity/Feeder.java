package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 饲喂器
    */
@ApiModel(value="饲喂器")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableName(value = "feeder")
public class Feeder {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="公司id")
    private Long companyId;

    /**
     * 猪场id
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value="猪场id")
    private Long pigFarmId;

    /**
     * 栋舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value="栋舍id")
    private Long pigHouseId;

    /**
     * 主机编号
     */
    @TableField(value = "client_id")
    @ApiModelProperty(value="主机编号")
    private Long clientId;

    /**
     * 料线ID
     */
    @TableField(value = "material_line_id")
    @ApiModelProperty(value="料线ID")
    private Long materialLineId;

    /**
     * 饲喂器编号
     */
    @TableField(value = "feeder_code")
    @ApiModelProperty(value="饲喂器编号")
    private Integer feederCode;

    /**
     * 饲喂器类型
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value="饲喂器类型")
    private String type;

    /**
     * 饲喂器状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="饲喂器状态")
    private String status;

    /**
     * 饲喂器信息
     */
    @TableField(value = "error")
    @ApiModelProperty(value="饲喂器信息")
    private String error;

    /**
     * 传感器重量
     */
    @TableField(value = "sensor_weight")
    @ApiModelProperty(value="传感器重量")
    private Integer sensorWeight;

    /**
     * 最新记录时间
     */
    @TableField(value = "`date`")
    @ApiModelProperty(value="最新记录时间")
    private LocalDateTime date;
}