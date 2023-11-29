package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/26 18:29
 **/
@Data
@ApiModel
public class PigHouseRowsDTO {

    @ApiModelProperty("猪舍排位id")
    private Long id;

    @ApiModelProperty(value = "猪舍id",required = true)
    @NotNull
    private Long pigHouseId;

  /*   @ApiModelProperty(value = "名称")
    private String name;


  @ApiModelProperty(value="总栏数",required = true)
    @NotNull
    @Min(1)
    @Max(100)
    private Integer columns;

    @ApiModelProperty(value="每个栏位的最大存栏数",required = true)
    @NotNull
    @Min(1)
    private Integer max;*/
}
