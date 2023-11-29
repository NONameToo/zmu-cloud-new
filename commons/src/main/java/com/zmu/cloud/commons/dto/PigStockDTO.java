package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shining
 */
@Data
public class PigStockDTO {
    @ApiModelProperty("已存总数")
    private Integer total;
    @ApiModelProperty("最大存栏总数")
    private Integer maxPerColumns;
    @ApiModelProperty("猪舍名称")
    private String pigHouse;
    @ApiModelProperty("排名")
    private String pigRows;
    @ApiModelProperty("栏名")
    private String pigColumns;
}
