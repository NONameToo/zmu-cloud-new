package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.FeedTowerAdd;
import com.zmu.cloud.commons.entity.FeedTowerCar;
import com.zmu.cloud.commons.entity.FeedTowerLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("FeedTowerAddVO")
public class FeedTowerAddVO extends FeedTowerAdd {

    /**
     * 料塔信息
     */
    @ApiModelProperty(value="料塔信息")
    private TowerVo tower;


    /**
     * 打料前测量信息
     */
    @ApiModelProperty(value="打料前测量信息")
    private FeedTowerLog feedTowerLogBefore;


    /**
     * 打料后测量信息
     */
    @ApiModelProperty(value="打料后测量信息")
    private FeedTowerLog feedTowerLogAfter;


    /**
     * 车辆信息
     */
    @ApiModelProperty(value="车辆信息")
    private FeedTowerCar feedTowerCar;

    /**
     * 倒计时
     */
    @ApiModelProperty(value="倒计时(单位秒)")
    private Long finishTimeRemain;


}
