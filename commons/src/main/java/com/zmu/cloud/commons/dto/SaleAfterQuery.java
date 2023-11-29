package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaojian
 * @create 2023/10/31 14:33
 * @Description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleAfterQuery {


    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "售后类型：1更换设备  2更换电控箱  3更换设备和电控箱")
    private Integer type;
    @ApiModelProperty(value = "处理状态：0待处理 1已处理")
    private Integer status;
    @ApiModelProperty(value = "起始页")
    private Integer pageNum;
    @ApiModelProperty(value = "分页大小")
    private Integer pageSize;
}
