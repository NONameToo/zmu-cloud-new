package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
@ApiModel("断奶事件")
public class EventWeanedVO extends EventDetailVO {
    /**
     * 断奶日期
     */
    @ApiModelProperty(value = "断奶日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date weanedDate;

    /**
     * 断奶数量
     */
    @ApiModelProperty(value = "断奶数量")
    private Integer weanedNumber;

    @ApiModelProperty("所属位置猪舍")
    private String pigHouse;
    @ApiModelProperty("所属位置猪舍排位")
    private String pigRows;
    @ApiModelProperty("所属位置猪舍栏位")
    private String pigColumns;
    @ApiModelProperty("位置")
    private String position;
    @ApiModelProperty(value = "所属位置(具体栏id)")
    private Long pigHouseColumnsId;
    @ApiModelProperty(value = "猪群名称")
    private String pigGroupName;


    /**
     * 胎次
     */
    @ApiModelProperty(value = "胎次")
    private Integer parity;

    /**
     * 断奶窝重
     */
    @ApiModelProperty(value = "断奶窝重")
    private BigDecimal weanedWeight;
    @ApiModelProperty("类型：1.配种，2妊娠，3分娩，4断奶")
    private Integer eventType = 4;

}
