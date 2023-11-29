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
 * 疫苗/药品入库对象 supplies_add
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-SuppliesAdd")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "supplies_add")
public class SuppliesAdd extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "库存id")
    @TableField(exist = false)
    private Long suppliesId;

    /** 入库时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "add_date")
    @ApiModelProperty(value = "入库时间")
    private Date addDate;

    /** 物品名称 */
    @TableField(value = "name")
    @ApiModelProperty(value = "物品名称")
    private String name;

    /** 剂量单位 */
    @TableField(value = "unit")
    @ApiModelProperty(value = "剂量单位")
    private String unit;

    /** 入库数量 */
    @TableField(value = "num")
    @ApiModelProperty(value = "入库数量")
    private Long num;

    /** 生产厂家 */
    @TableField(value = "vender")
    @ApiModelProperty(value = "生产厂家")
    private String vender;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "prod_date")
    @ApiModelProperty(value = "生产日期")
    private Date prodDate;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "exceDate")
    @ApiModelProperty(value = "失效时间")
    private Date exceDate;

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
