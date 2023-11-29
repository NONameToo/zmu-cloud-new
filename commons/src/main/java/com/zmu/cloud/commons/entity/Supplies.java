package com.zmu.cloud.commons.entity;


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

import java.util.Date;

/**
 * 疫苗/药品库存对象 supplies
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-Supplies")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "supplies")
public class Supplies extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /** 物品名称 */
    @TableField(value = "name")
    @ApiModelProperty(value = "物品名称")
    private String name;

    /** 剂量单位 */
    @TableField(value = "unit")
    @ApiModelProperty(value = "剂量单位")
    private String unit;

    /** 库存 */
    @TableField(value = "num")
    @ApiModelProperty(value = "库存")
    private Long num;

    /** 生产厂家 */
    @TableField(value = "vender")
    @ApiModelProperty(value = "生产厂家")
    private String vender;

    @ApiModelProperty(value = "全名称(疫苗/药品+厂家)")
    private String fullName;

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
