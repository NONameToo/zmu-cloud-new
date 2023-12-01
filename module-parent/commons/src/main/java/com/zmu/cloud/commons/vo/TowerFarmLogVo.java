package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author zhaojian
 * @create 2023/10/17 17:48
 * @Description 料塔加料放料记录
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("料塔加料放料记录")
public class TowerFarmLogVo {

    @ApiModelProperty("猪场名称")
    private String farmName;
    @ApiModelProperty("猪场Id")
    private Long farmId;
    @ApiModelProperty("料塔列表")
    private List<TowerDetailLogVo> towers;


}
