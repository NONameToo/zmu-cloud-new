package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@ApiModel("料塔预警")
@NoArgsConstructor
@AllArgsConstructor
public class TowerWarningVo {

    @ApiModelProperty("料塔ID")
    private Long id;

    @ApiModelProperty("料塔名称")
    private String name;

    @ApiModelProperty("余料重量")
    private String residualWeight;

    @ApiModelProperty("警戒值")
    private Integer warning;

    @ApiModelProperty("余料占比")
    private Integer residualPercentage;

    @ApiModelProperty("刷新时间(最后一次测量的时间)")
    private LocalDateTime refreshTime;

}
