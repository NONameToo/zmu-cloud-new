package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打料表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerAdd")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedTowerAdd {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 车辆id
     */
    @ApiModelProperty(value = "车辆id")
    private Long carId;

    /**
     * 料塔id
     */
    @ApiModelProperty(value = "料塔id")
    private Long towerId;

    /**
     * 当前状态：0未开始 1加料前测量中 2加料前测量结束 3等待开盖 4已开盖 5我已准备好加料 6加料中 7加料结束 8等待关盖 9已关盖 10关盖测量中 11关盖测量结束 12加料完成  13打料前测量失败 14 打料后测量失败 15中止
     */
    @ApiModelProperty(value = "当前状态：0未开始 1加料前测量中 2加料前测量结束 3等待开盖 4已开盖 5我已准备好加料 6加料中 7加料结束 8等待关盖 9已关盖 10关盖测量中 11关盖测量结束 12加料完成  13打料前测量失败 14 打料后测量失败 15中止")
    private Integer currentState;

    /**
     * 加料前余料(单位:g)
     */
    @ApiModelProperty(value = "加料前余料(单位:g)")
    private Long addBefore;

    /**
     * 加料前余料体积(单位:立方厘米)
     */
    @ApiModelProperty(value = "加料前余料体积(单位:立方厘米)")
    private Long addBeforeVolume;

    /**
     * 加料前余料测量日志log_id
     */
    @ApiModelProperty(value = "加料前余料测量日志log_id")
    private Long addBeforeLogId;

    /**
     * 加料开始时间
     */
    @ApiModelProperty(value = "加料开始时间")
    private LocalDateTime addStartTime;

    /**
     * 加料结束时间
     */
    @ApiModelProperty(value = "加料结束时间")
    private LocalDateTime addEndTime;

    /**
     * 预计加料结束时间
     */
    @ApiModelProperty(value = "预计加料结束时间")
    private LocalDateTime mayAddEndTime;

    /**
     * 预计加料结束倒计时(单位:秒)
     */
    @ApiModelProperty(value = "预计加料结束倒计时(单位:秒)")
    private Long mayLeftTime;

    /**
     * 加料后余料(单位:g)
     */
    @ApiModelProperty(value = "加料后余料(单位:g)")
    private Long addAfter;

    /**
     * 加料后余料体积(单位:立方厘米)
     */
    @ApiModelProperty(value = "加料后余料体积(单位:立方厘米)")
    private Long addAfterVolume;

    /**
     * 加料后余料测量日志log_id
     */
    @ApiModelProperty(value = "加料后余料测量日志log_id")
    private Long addAfterLogId;

    /**
     * 加料耗时(单位秒)
     */
    @ApiModelProperty(value = "加料耗时(单位秒)")
    private Long useTime;

    /**
     * 打料量(单位g)
     */
    @ApiModelProperty(value = "打料量(单位g)")
    private Long addTotal;

    /**
     * 开盖时间
     */
    @ApiModelProperty(value = "开盖时间")
    private LocalDateTime openTime;

    /**
     * 关盖时间
     */
    @ApiModelProperty(value = "关盖时间")
    private LocalDateTime closeTime;

    /**
     * 备注(中止流程的原因)
     */
    @ApiModelProperty(value = "备注(中止流程的原因)")
    private String remark;

    /**
     * 备注(中止流程前的状态)
     */
    @ApiModelProperty(value = "备注(中止流程前的状态)")
    private Integer stopStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
}