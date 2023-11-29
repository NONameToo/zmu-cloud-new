package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shining
 */
@Data
public class QueryPigStockPork extends BaseQuery {
    @ApiModelProperty(value = "猪舍id")
    private String pigHouseId;

    @ApiModelProperty(value = "栋舍排id")
    private Long pigHouseRowId;


    @ApiModelProperty(value = "栋舍栏位id")
    private Long pigHouseColumnId;


    @ApiModelProperty(value = "1在场，2离场", required = true)
    @NotNull(message = "在场状态不能为空")
    private Integer type;
    @ApiModelProperty(value = "猪群id")
    private Long pigGroupId;
    @ApiModelProperty(value = "猪群名称")
    private String pigGroupName;

    @ApiModelProperty("日龄")
    private Long dayAge;
}
