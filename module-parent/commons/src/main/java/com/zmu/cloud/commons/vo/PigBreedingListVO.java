package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.converter.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * @author YH
 */
@Data
@ApiModel("种猪列表")
public class PigBreedingListVO {


    private Long id;
    /**
     * 猪只类型1公猪，2母猪
     */
    @ApiModelProperty(value = "猪只类型1公猪，2母猪")
    @ExcelProperty(value = "类型", converter = PigTypeConvert.class)
    private Integer type;

    /**
     * 耳号
     */
    @ApiModelProperty(value = "耳号")
    @ExcelProperty(value = "耳号")
    private String earNumber;

    /**
     * 品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜
     */
    @ApiModelProperty(value = "品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜")
    @ExcelProperty(value = "品种", converter = PigVarietyConvert.class)
    private Integer variety;
    /**
     * 进场类型1.自繁，2购买，3转入
     */
    @ApiModelProperty(value = "进场类型1.自繁，2购买，3转入")
    @ExcelProperty(value = "进场类型", converter = PigApproachTypeConvert.class)
    private Integer approachType;

    /**
     * 胎次
     */
    @ApiModelProperty(value = "胎次")
    @ExcelProperty(value = "胎次")
    private Integer parity;

    @ApiModelProperty(value = "日龄")
    @ExcelProperty(value = "日龄")
    private Integer dayAge;
    /**
     * 在场状态，1在场，2离场
     */
    @ApiModelProperty(value = "在场状态，1在场，2离场")
    @ExcelProperty(value = "在场状态", converter = PigPresenceStatusConvert.class)
    private Integer presenceStatus;


    /**
     * 种猪状态默认：1.后备，配种，3，空怀，4，返情，5，流产，6，妊娠，7，断奶
     */
    @ApiModelProperty(value = "种猪状态默认：1.后备，配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳，8断奶")
    @ExcelProperty(value = "种猪状态", converter = PigStatusConvert.class)
    private Integer pigStatus;

    @ApiModelProperty(value = "状态天数")
    @ExcelProperty(value = "状态天数")
    private Integer statusDay;
    @ApiModelProperty(value = "备注")
    @ExcelIgnore
    private String remark;

    @ApiModelProperty("所属猪舍")
    @ExcelProperty(value = "所属猪舍")
    private String pigHouse;

    @ApiModelProperty("所属猪舍类型")
    @ExcelProperty(value = "所属猪舍类型")
    private Integer pigHouseType;

    @ApiModelProperty("所属排位")
    @ExcelProperty(value = "所属排位")
    private String pigRows;

    @ApiModelProperty("所属栏位")
    @ExcelProperty(value = "所属栏位")
    private String pigColumns;

    @ApiModelProperty("位置")
    @ExcelProperty(value = "位置")
    private String position;

    @ApiModelProperty(value = "所属位置(具体栏id)")
    @ExcelIgnore
    private Long pigHouseColumnsId;

    @ApiModelProperty(value = "猪舍id")
    @ExcelIgnore
    private Long pigHouseId;

    @ApiModelProperty(value = "猪排id")
    @ExcelIgnore
    private Long pigRowsId;

    @ApiModelProperty(value = "价格")
    @ExcelIgnore
    private BigDecimal price;

    @ApiModelProperty("重量")
    @ExcelIgnore
    private BigDecimal weight;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "出生日期")
    private Date birthDate;


    @ApiModelProperty(value = "进场日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "进场日期")
    private Date approachTime;

    /**
     * 状态日期
     */
    @ApiModelProperty(value = "状态日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "状态日期")
    private Date statusTime;

    @ApiModelProperty(value = "预产期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "预产期")
    private Date expectedDate;

    @ApiModelProperty(value = "操作人")
    @ExcelIgnore
    private Long operatorId;

    @ApiModelProperty(value = "背膘")
    @ExcelProperty(value = "背膘")
    private Integer backFat;

    @ApiModelProperty(value = "饲喂器号")
    @ExcelProperty(value = "饲喂器号")
    private String feederCode;

}
