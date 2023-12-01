package com.zmu.cloud.commons.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 疫苗/药品出库对象 supplies_sub
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-SuppliesSub")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "supplies_sub")
public class SuppliesSub extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "库存id")
    @TableField(exist = false)
    private Long suppliesId;

    /** 出库时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "sub_date")
    @ApiModelProperty(value = "出库时间")
    private Date subDate;

    /** 物品名称 */
    @TableField(value = "name")
    @ApiModelProperty(value = "物品名称")
    private String name;

    /** 剂量单位 */
    @TableField(value = "unit")
    @ApiModelProperty(value = "剂量单位")
    private String unit;

    /** 领用数量 */
    @TableField(value = "num")
    @ApiModelProperty(value = "领用数量")
    private Long num;

    /** 领用数量 */
    @TableField(value = "numed")
    @ApiModelProperty(value = "现有库存")
    private Long numed;

    /** 领用类型 */
    @TableField(value = "sub_type")
    @ApiModelProperty(value = "领用类型,0 免疫 1 防治 2 洗消 3 其他")
    private String subType;

    /** 使用位置 */
    @TableField(value = "site")
    @ApiModelProperty(value = "使用位置")
    private String site;

    /** 生产厂家 */
    @TableField(value = "vender")
    @ApiModelProperty(value = "生产厂家")
    private String vender;

    /** 领用人 */
    @TableField(value = "receive_by")
    @ApiModelProperty(value = "领用人")
    private String receiveBy;

    /** 操作人 */
    @TableField(value = "operate_by")
    @ApiModelProperty(value = "操作人")
    private Long operateBy;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作人名称")
    private String operateByName;

    /** 开始时间-搜索用 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间-搜索用")
    private Date startTime;

    /** 结束时间-搜索用 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间-搜索用")
    private Date endTime;
}
