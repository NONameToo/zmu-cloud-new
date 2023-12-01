package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.converter.PigApproachTypeConvert;
import com.zmu.cloud.commons.converter.PigTypeConvert;
import com.zmu.cloud.commons.converter.PigVarietyConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shining
 */
@Data
@ApiModel("种猪进场记录")
public class EventPigBreedingListVO {


    @ApiModelProperty(value = "进场日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("进场日期")
    private Date approachTime;

    /**
     * 耳号
     */
    @ApiModelProperty(value = "耳号")
    @ExcelProperty("耳号")
    private String earNumber;

    /**
     * 猪只类型1公猪，2母猪
     */
    @ApiModelProperty(value = "猪只类型1公猪，2母猪")
    @ExcelIgnore
    private Integer type;


    /**
     * 品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜
     */
    @ApiModelProperty(value = "品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜")
    @ExcelIgnore
    private Integer variety;
    /**
     * 进场类型1.自繁，2购买，3转入
     */
    @ApiModelProperty(value = "进场类型1.自繁，2购买，3转入")
    @ExcelProperty(value = "进场类型",converter = PigApproachTypeConvert.class)
    private Integer approachType;

    @ApiModelProperty(value = "入场胎次")
    @ExcelIgnore
    private Integer approachFetal;


    @ApiModelProperty(value = "日龄")
    @ExcelIgnore
    private Integer dayAge;
    /**
     * 在场状态，1在场，2离场
     */
    @ApiModelProperty(value = "在场状态，1在场，2离场")
    @ExcelIgnore
    private Integer presenceStatus;


    /**
     * 种猪状态默认：1.后备，配种，3，空怀，4，返情，5，流产，6，妊娠，7，断奶
     */
    @ApiModelProperty(value = "种猪状态默认：1.后备，配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳，8断奶")
    @ExcelIgnore
    private Integer pigStatus;

    @ApiModelProperty(value = "状态天数")
    @ExcelIgnore
    private Integer statusDay;

    @ApiModelProperty(value = "备注")
    @ExcelProperty("备注")
    private String remark;


    @ApiModelProperty("所属猪舍")
    @ExcelProperty("所属猪舍")
    private String pigHouse;

    @ApiModelProperty("所属排位")
    @ExcelProperty("所属排位")
    private String pigRows;

    @ApiModelProperty("所属栏位")
    @ExcelProperty("所属栏位")
    private String pigColumns;


    @ApiModelProperty("位置")
    @ExcelProperty(value = "位置")
    private String position;



    @ApiModelProperty(value = "所属位置(具体栏id)")
    @ExcelIgnore
    private Long pigHouseColumnsId;


    @ApiModelProperty("重量")
    @ExcelIgnore
    private BigDecimal weight;


    /**
     * 状态日期
     */
    @ApiModelProperty(value = "状态日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelIgnore
    private Date statusTime;

    @ApiModelProperty(value = "操作人")
    @ExcelIgnore
    private String operatorName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    private Date createTime;


}
