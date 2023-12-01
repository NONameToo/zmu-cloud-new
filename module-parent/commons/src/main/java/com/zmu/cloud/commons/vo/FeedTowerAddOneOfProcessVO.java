package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;

/**
 * 打料流程每个步骤 VO
 */
@Data
@ApiModel("FeedTowerAddProcessVO")
public class FeedTowerAddOneOfProcessVO {

    /**
     * 1开盖测量节点
     */
    @ApiModelProperty(value="开盖测量节点")
    private HashMap<String,Object> addBefore;

    /**
     * 2开盖节点
     */
    @ApiModelProperty(value="开盖节点")
    private HashMap<String,Object> open;

    /**
     * 3选车节点
     */
    @ApiModelProperty(value="选车节点")
    private HashMap<String,Object> chooseCar;


    /**
     * 4我已准备好加料节点
     */
    @ApiModelProperty(value="我已准备好加料")
    private HashMap<String,Object> readyToAdd;


    /**
     * 5结束加料节点
     */
    @ApiModelProperty(value="结束加料节点")
    private HashMap<String,Object> addFinish;



    /**
     * 6关盖节点
     */
    @ApiModelProperty(value="关盖节点")
    private HashMap<String,Object> close;


    /**
     * 7关盖测量节点
     */
    @ApiModelProperty(value="关盖测量节点")
    private HashMap<String,Object> addAfter;


}
