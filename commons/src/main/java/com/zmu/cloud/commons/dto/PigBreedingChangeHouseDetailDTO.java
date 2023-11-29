package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author shining
 */
@Data
@ApiModel("转舍详情")
public class PigBreedingChangeHouseDetailDTO {
    @ApiModelProperty(value = "种猪Id", required = true)
    @NotNull(message = "种猪Id不能为空")
    private Long pigBreedingId;
    @ApiModelProperty(value = "转入栏位Id", required = true)
    @NotNull(message = "转入栏位Id不能为空")
    private Long houseColumnsInId;
}
