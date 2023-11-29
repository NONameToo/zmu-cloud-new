package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zmu.cloud.commons.dto.DeviceStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 老化记录表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-DeviceAgingCheck")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceAgingCheck {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 料塔id
     */
    @ApiModelProperty(value = "料塔id")
    private Long towerId;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceNum;

    /**
     * 应运行次数
     */
    @ApiModelProperty(value = "应运行次数")
    private Integer checkCount;

    /**
     * 已运行次数
     */
    @ApiModelProperty(value = "已运行次数")
    private Integer runCount;

    /**
     * 运行出错次数
     */
    @ApiModelProperty(value = "运行出错次数")
    private Integer errCount;

    /**
     * 检测开始时间
     */
    @ApiModelProperty(value = "老化开始时间")
    private LocalDateTime startTime;

    /**
     * 检测结束时间
     */
    @ApiModelProperty(value = "老化结束时间")
    private LocalDateTime endTime;

    /**
     * 体积(单位 立方厘米)
     */
    @ApiModelProperty(value = "体积(单位 立方厘米)")
    private Long volume;

    /**
     * 参考标准(单位 立方厘米)
     */
    @ApiModelProperty(value = "参考标准(单位 立方厘米)")
    private Long standardVolume;

    /**
     * 准确率% 单位(保留两位小数后*100的整数)
     */
    @ApiModelProperty(value = "准确率% 单位(保留两位小数后*100的整数)")
    private Integer rightPercent;

    /**
     * 是否通过 -1未开始 0不通过  1通过 2无效测量
     */
    @ApiModelProperty(value = "是否通过 -1未开始 0不通过  1通过 2无效测量")
    private Integer pass;

    /**
     * 测量日志的id
     */
    @ApiModelProperty(value = "测量日志的id")
    private Long logId;

    /**
     * 序列号
     */
    @ApiModelProperty(value = "序列号")
    private String sn;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String batchNum;

    /**
     * 公司
     */
    @ApiModelProperty(value = "公司")
    private Long companyId;

    /**
     * 猪场
     */
    @ApiModelProperty(value = "猪场")
    private Long pigFarmId;

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

    /**
     * 结果处理(0自动,1手动)
     */
    @ApiModelProperty(value = "结果处理(0自动,1手动)")
    private Integer handle;

    /**
     * 备注(通过不通过的原因)
     */
    @ApiModelProperty(value = "备注(通过不通过的原因)")
    private String remark;

    /**
     * 质检员id
     */
    @ApiModelProperty(value = "老化检验员id")
    private Long checkerId;

    /**
     * 质检员名字
     */
    @ApiModelProperty(value = "老化检验员名字")
    private String checkerName;

    /**
     * 测量任务标识
     */
    @ApiModelProperty(value = "测量任务标识")
    private String taskNo;

    /**
     * 异常记录
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "异常记录")
    private List<FeedTowerLog> errLogs;

    /**
     * 时长
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "时长(分钟)")
    private Long longTime;

    /**
     * 质检id
     */
    @ApiModelProperty(value = "质检id")
    private Long qualityId;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备信息")
    private DeviceStatus deviceStatus;
}