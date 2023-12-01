package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmHouseRowVo {

    @ApiModelProperty("猪场ID")
    private Long farmId;
    @ApiModelProperty("猪场名称")
    private String farmName;
    @ApiModelProperty("栋舍ID")
    private Long houseId;
    @ApiModelProperty("栋舍名称")
    private String houseName;
    @ApiModelProperty("栋舍类型")
    private String houseType;
    @ApiModelProperty("排ID")
    private Long rowId;
    @ApiModelProperty("排")
    private String row;

}
