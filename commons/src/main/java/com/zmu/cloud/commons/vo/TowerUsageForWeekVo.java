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
public class TowerUsageForWeekVo {

    @ApiModelProperty(value = "日期")
    private String dayStr;
    @ApiModelProperty(value = "变化量")
    private String modified;
    @ApiModelProperty(value = "余料量")
    private String surplus;
    private int spot;

}
