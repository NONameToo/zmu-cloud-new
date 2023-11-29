package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 料塔消息
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerMsg")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feed_tower_msg")
public class FeedTowerMsg {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 公司
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司")
    private Long companyId;

    /**
     * 猪场ID
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "猪场ID")
    private Long pigFarmId;

    /**
     * 设备ID
     */
    @TableField(value = "tower_id")
    @ApiModelProperty(value = "设备ID")
    private Long towerId;

    /**
     * 设备编号
     */
    @TableField(value = "device_no")
    @ApiModelProperty(value = "设备编号")
    private String deviceNo;

    /**
     * 接口
     */
    @TableField(value = "topic")
    @ApiModelProperty(value = "接口")
    private String topic;

    /**
     * 完整的消息
     */
    @TableField(value = "msg")
    @ApiModelProperty(value = "完整的消息")
    private String msg;

    /**
     * 内容长度
     */
    @TableField(value = "content_len")
    @ApiModelProperty(value = "内容长度")
    private Integer contentLen;

    /**
     * 参数
     */
    @TableField(value = "param")
    @ApiModelProperty(value = "参数")
    private String param;

    /**
     * Send、Received
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "Send、Received")
    private String type;

    /**
     * 协议版本
     */
    @TableField(value = "version")
    @ApiModelProperty(value = "协议版本")
    private String version;

    /**
     * 是否正确有效 CRC 验证
     */
    @TableField(value = "correct")
    @ApiModelProperty(value = "是否正确有效 CRC 验证")
    private String correct;

    /**
     * 检测任务标识
     */
    @TableField(value = "task_id")
    @ApiModelProperty(value = "检测任务标识")
    private String taskId;

    /**
     * 数据时间
     */
    @TableField(value = "data_time")
    @ApiModelProperty(value = "数据时间")
    private String dataTime;

    /**
     * 舵机角度
     */
    @TableField(value = "steering_angle")
    @ApiModelProperty(value = "舵机角度")
    private Integer steeringAngle;

    /**
     * 采集点数
     */
    @TableField(value = "amount")
    @ApiModelProperty(value = "采集点数")
    private Integer amount;

    /**
     * 完整的消息
     */
    @TableField(value = "content")
    @ApiModelProperty(value = "完整的消息")
    private String content;

    /**
     * 验证码
     */
    @TableField(value = "crc")
    @ApiModelProperty(value = "验证码")
    private String crc;

    /**
     * 接收数据是否结束
     */
    @TableField(value = "end_flag")
    @ApiModelProperty(value = "接收数据是否结束")
    private Boolean endFlag;

    @TableField(value = "create_time")
    @ApiModelProperty(value = "")
    private LocalDateTime createTime;
}