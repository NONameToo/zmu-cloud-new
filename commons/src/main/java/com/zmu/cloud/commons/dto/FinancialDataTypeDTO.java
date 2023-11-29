package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/28 11:17
 **/
@Data
@ApiModel
public class FinancialDataTypeDTO {

    private Long id;

    @ApiModelProperty(value="名称")
    private String name;

    @ApiModelProperty(value="科目代码")
    private String sujectCode;

    @ApiModelProperty(value="科目名称")
    private String sujectName;

    @ApiModelProperty(value="数据类型(1猪只销售,2猪只购买,3其它)")
    private Integer dataType;

    @ApiModelProperty(value="借贷类型1，借，2货")
    private String loanType;
}
