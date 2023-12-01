package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
@ApiModel("料塔设备情况信息")
public class TowerStatusInfoVo {

    private Long total;
    private Long normal;
    private Long error;

    private List<TowerVo> errorInfoList;
}
