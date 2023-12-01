package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 打料流程VO
 */
@Data
@ApiModel("FeedTowerAddProcessVO")
public class FeedTowerAddProcessVO  {

    /**
     * 打料流程id主键
     */
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 料塔id
     */
    @ApiModelProperty(value="料塔id")
    private Long towerId;

    /**
     * 料塔名称
     */
    @ApiModelProperty(value="料塔名称")
    private String towerName;

    /**
     * 设备编号
     */
    @ApiModelProperty(value="设备编号")
    private String deviceNo;


    /**
     * 流程节点List
     */
    @ApiModelProperty(value="流程节点")
    private List<Map<String,Object>> proArray;
}
