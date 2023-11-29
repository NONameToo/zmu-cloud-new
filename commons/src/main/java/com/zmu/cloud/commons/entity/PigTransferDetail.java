package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "com-zmu-cloud-commons-entity-PigTransferDetail")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_transfer_detail")
public class PigTransferDetail {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "")
    private Long pigFarmId;

    /**
     * 转出栋舍
     */
    @TableField(value = "out_house_id")
    @ApiModelProperty(value = "转出栋舍")
    private Long outHouseId;

    /**
     * 转出栋舍名称
     */
    @TableField(value = "out_house_name")
    @ApiModelProperty(value = "转出栋舍名称")
    private String outHouseName;

    /**
     * 转出栏位
     */
    @TableField(value = "out_col_id")
    @ApiModelProperty(value = "转出栏位")
    private Long outColId;

    /**
     * 转出栏位号
     */
    @TableField(value = "out_col_no")
    @ApiModelProperty(value = "转出栏位号")
    private Integer outColNo;

    /**
     * 转入栋舍ID
     */
    @TableField(value = "in_house_id")
    @ApiModelProperty(value = "转入栋舍ID")
    private Long inHouseId;

    /**
     * 转入栋舍名称
     */
    @TableField(value = "in_house_name")
    @ApiModelProperty(value = "转入栋舍名称")
    private String inHouseName;

    /**
     * 转入栏位ID
     */
    @TableField(value = "in_col_id")
    @ApiModelProperty(value = "转入栏位ID")
    private Long inColId;

    /**
     * 转入栏位号
     */
    @TableField(value = "in_col_no")
    @ApiModelProperty(value = "转入栏位号")
    private Integer inColNo;

    /**
     * 猪只ID
     */
    @TableField(value = "pig_id")
    @ApiModelProperty(value = "猪只ID")
    private Long pigId;

    /**
     * 猪只耳号
     */
    @TableField(value = "ear_number")
    @ApiModelProperty(value = "猪只耳号")
    private String earNumber;

    @TableField(value = "transfer_id")
    @ApiModelProperty(value = "")
    private Long transferId;
}