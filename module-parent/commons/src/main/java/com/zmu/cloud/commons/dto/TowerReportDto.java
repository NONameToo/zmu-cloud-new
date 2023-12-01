package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TowerReportDto {

    @ApiModelProperty(value = "日期")
    private String day;
    @ApiModelProperty(value = "当日的第一条数据")
    private BigDecimal first;
    @ApiModelProperty(value = "当日的最后一条数据")
    private BigDecimal last;

}
