package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二维码
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-Qrcode")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "qrcode")
public class Qrcode {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 猪场id
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "猪场id")
    private Long pigFarmId;

    /**
     * 栋舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "栋舍id")
    private Long pigHouseId;

    /**
     * 栏位id
     */
    @TableField(value = "pig_column_id")
    @ApiModelProperty(value = "栏位id")
    private Long pigColumnId;

    /**
     * 栏位号
     */
    @TableField(value = "no")
    @ApiModelProperty(value = "栏位号")
    private Integer no;

    /**
     * 编码
     */
    @TableField(value = "code")
    @ApiModelProperty(value = "编码")
    private String code;

    /**
     * 饲喂器号
     */
    @TableField(value = "feeder_code")
    @ApiModelProperty(value = "饲喂器号")
    private Integer feederCode;

    /**
     * 批次
     */
    @TableField(value = "batch")
    @ApiModelProperty(value = "批次")
    private Integer batch;

    @TableField(value = "del")
    @ApiModelProperty(value = "")
    private String del;

    @TableField(value = "create_time")
    @ApiModelProperty(value = "")
    private LocalDateTime createTime;
}