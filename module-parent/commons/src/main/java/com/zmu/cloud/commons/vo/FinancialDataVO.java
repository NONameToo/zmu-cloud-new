package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lqp0817@gmail.com
 * @date 2022/5/2 20:24
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDataVO {

    @ApiModelProperty("时间")
    private Date createTime;

    @ApiModelProperty("摘要")
    private String remark;

    @ApiModelProperty(value="数量")
    private Integer number;

    @ApiModelProperty(value="单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value="总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value="关联财务数据类型")
    private String dataType;

    @ApiModelProperty(value="收支类型1,收入，2支出")
    private Integer income;

    @ApiModelProperty(value="状态，1已导出，0未导出")
    private Integer status;

}
