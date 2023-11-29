package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TowerReportByDayVo{
    @ApiModelProperty(value = "饲料名称")
    private String feedType;

    @ApiModelProperty(value = "用料合计")
    private String useTotal;

    @ApiModelProperty(value = "加料合计")
    private String addTotal;


    @ApiModelProperty(value = "用料")
    private List<TowerLogReportVo> use;

    @ApiModelProperty(value = "补料")
    private List<TowerLogReportVo> add;

    @ApiModelProperty(value = "所有")
    private List<TowerLogReportVo> all;
}
