package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 肉猪入场
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigPork")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_pork")
@SuperBuilder
public class PigPork extends BaseEntity {
    /**
     * 进场时间
     */
    @TableField(value = "approach_time")
    @ApiModelProperty(value = "进场时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "进场时间不能为空")
    private Date approachTime;

    /**
     * 所属位置
     */
    @TableField(value = "pig_house_columns_id")
    @ApiModelProperty(value = "所属位置")
    //@NotNull(message = "进场栏位Id为空")
    private Long pigHouseColumnsId;

    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "所属栋舍")
    private Long pigHouseId;

    @TableField(value = "pig_group_id")
    @ApiModelProperty(value = "猪群id")
    private Long pigGroupId;

    @TableField(value = "stock_id")
    @ApiModelProperty(value = "库存id")
    private Long stockId;


    @TableField(exist = false)
    @ApiModelProperty(value = "猪群名称")
    private String pigGroupName;

    /**
     * 进场类型，1购买，2转入
     */
    @TableField(value = "approach_type")
    @ApiModelProperty(value = "进场类型，1购买，2转入", required = true)
    @NotNull(message = "进场类型为空")
    private Integer approachType;

    /**
     * 出生日期
     */
    @TableField(value = "birth_date")
    @ApiModelProperty(value = "出生日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "出生日期不能为空")
    private Date birthDate;

    /**
     * 数量
     */
    @TableField(value = "`number`")
    @ApiModelProperty(value = "数量", required = true)
    @NotNull(message = "数量不能为空")
    private Integer number;

    /**
     * 品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜
     */
    @TableField(value = "variety")
    @ApiModelProperty(value = "品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜",required = true)
    @NotNull(message = "品种不能为空")
    private Integer variety;

    /**
     * 重量(kg)
     */
    @TableField(value = "weight")
    @ApiModelProperty(value = "重量(kg)")
    private BigDecimal weight;

    /**
     * 价格
     */
    @TableField(value = "price")
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    /**
     * 操作员
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作员",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;


}