package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel("栋舍类型")
@AllArgsConstructor
@NoArgsConstructor
public class PigHouseTypeVo {

    @ApiModelProperty("类型名称")
    private String typeName;
    @ApiModelProperty("类型编号")
    private Integer typeId;

}
