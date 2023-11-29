package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.converter.PigApproachTypeConvert;
import com.zmu.cloud.commons.converter.PigVarietyConvert;
import com.zmu.cloud.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 肉猪入场
 */
@ApiModel(value = "肉猪入场记录")
@Data
public class EventPigPorkListVO {



    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 进场时间
     */
    @ApiModelProperty(value = "进场时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("进场日期")
    private Date approachTime;
    /**
     * 进场类型，1购买，2转入
     */
    @ApiModelProperty(value = "进场类型，1购买，2转入")
    @ExcelProperty(value = "进场类型", converter = PigApproachTypeConvert.class)
    private Integer approachType;
    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelIgnore
    private Date birthDate;

    /**
     * 预计出栏日期
     */
    @ApiModelProperty(value = "预计出栏日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelIgnore
    private Date goOutDate;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    @ExcelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "日龄")
    @ExcelProperty(value = "日龄")
    private Integer dayAge;

    /**
     * 品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜
     */
    @ApiModelProperty(value = "品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜")
    @ExcelProperty(value = "品种", converter = PigVarietyConvert.class)
    private Integer variety;

    /**
     * 重量(kg)
     */
    @ApiModelProperty(value = "重量(kg)")
    @ExcelProperty(value = "重量(kg)")
    private BigDecimal weight;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格")
    @ExcelProperty(value = "价格")
    private BigDecimal price;



    @ApiModelProperty("所属猪舍")
    @ExcelProperty(value = "所属猪舍")
    private String pigHouse;
    @ApiModelProperty("所属猪舍id")
    @ExcelIgnore
    private String pigHouseId;

    @ApiModelProperty("所属排位")
    @ExcelProperty(value = "所属排位")
    private String pigRows;

    @ApiModelProperty("所属栏位")
    @ExcelProperty(value = "所属栏位")
    private String pigColumns;



    @ApiModelProperty("位置")
    @ExcelProperty(value = "位置")
    private String position;


    @ApiModelProperty("猪群名称")
    @ExcelProperty(value = "猪群名称")
    private String pigGroupName;
    @ApiModelProperty("猪群id")
    @ExcelIgnore
    private Long pigGroupId;

    @ApiModelProperty(value = "所属位置(具体栏id)")
    @ExcelIgnore
    private Long pigHouseColumnsId;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty(value = "备注")
    @ExcelProperty("备注")
    private String remark;

    /**
     * 操作员
     */
    @ApiModelProperty(value = "操作员")
    @ExcelProperty(value = "操作员")
    private String operatorName;



}