package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaojian
 * @create 2023/10/7 15:34
 * @Description 料塔总览
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("料塔总览")
public class TowerOverViewVo {

    @ApiModelProperty("时间")
    private LocalDateTime time;
    @ApiModelProperty("全部设备数")
    private Long sumCount;
    @ApiModelProperty("在线设备数")
    private Long onCount;
    @ApiModelProperty("离线设备数")
    private Long offCount;
    @ApiModelProperty("未绑定设备数")
    private Long noBondCount;
    @ApiModelProperty("各场数据")
    private List<FarmDetail> farmDetailList;

}
