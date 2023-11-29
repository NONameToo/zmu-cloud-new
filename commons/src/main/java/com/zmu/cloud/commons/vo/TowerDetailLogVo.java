package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author zhaojian
 * @create 2023/10/18 9:12
 * @Description
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("料塔列表")
public class TowerDetailLogVo {

    @ApiModelProperty("设备编号")
    private String deviceNo;
    @ApiModelProperty("料塔id")
    private String towerId;
    @ApiModelProperty("料塔名称")
    private String towerName;
    @ApiModelProperty("余料体积")
    private Long volume;
    @ApiModelProperty("余料重量")
    private Long weight;
    @ApiModelProperty("料塔容积")
    private Long initVolume;
    @ApiModelProperty("密度")
    private Long density;

}
