package com.zmu.cloud.commons.vo.sph;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PigProdVo {

    @ApiModelProperty(value = "胎次")
    private Integer parities;
    @ApiModelProperty(value = "存活X头")
    private Integer liveZz;
    @ApiModelProperty(value = "生产详情")
    List<PigProdDetailVo> prodDetails;

}
