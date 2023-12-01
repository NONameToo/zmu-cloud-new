package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@ApiModel("料线数据")
public class MaterialLineDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;
    @NotNull
    @ApiModelProperty(value = "栋舍ID", required = true, example = "5735562")
    private Long houseId;
    @NotEmpty
    @ApiModelProperty(value = "料线名称", required = true)
    private String name;
    @ApiModelProperty(value = "料线位置，比如：第7~12排")
    private String position;

}
