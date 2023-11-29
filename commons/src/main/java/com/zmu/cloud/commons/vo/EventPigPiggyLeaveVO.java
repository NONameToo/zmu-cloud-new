package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.converter.PigLeaveTypeConvert;
import com.zmu.cloud.commons.converter.PigLeavingReasonConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 种猪离场
 *
 * @author shining
 */
@ApiModel(value = "仔猪离场")
@Data
public class EventPigPiggyLeaveVO {
    @ApiModelProperty(value = "耳号")
    @ExcelProperty("耳号")
    private String earNumber;

    @ApiModelProperty(value = "离场时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("离场时间")
    private Date leaveTime;

    /**
     * 离场类型1.死淘，2转出
     */
    @ApiModelProperty(value = "离场类型1.死淘，2转出,3育成")
    @ExcelProperty(value = "离场类型",converter = PigLeaveTypeConvert.class)
    private Integer type;

    @ApiModelProperty(value = "数量")
    @ExcelProperty("数量")
    private Integer number;

    /**
     * 离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它
     */
    @ApiModelProperty(value = "离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它")
    @ExcelProperty(value = "离场原因",converter = PigLeavingReasonConvert.class)
    private Integer leavingReason;

    /**
     * 重量kg
     */
    @ApiModelProperty(value = "重量kg")
    @ExcelProperty("重量kg")
    private BigDecimal weight;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    @ExcelProperty("金额")
    private BigDecimal price;
    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    @ExcelProperty("操作人")
    private String operatorName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty(value = "备注")
    @ExcelProperty("备注")
    private String remark;

    @ApiModelProperty("所属猪舍id")
    private String pigHouseId;

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
    @ExcelProperty("位置")
    private String position;

}