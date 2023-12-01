package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * MQTT日志
    */
@ApiModel(value="MQTT日志")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "mqtt_log")
public class MqttLog {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 主题
     */
    @TableField(value = "topic")
    @ApiModelProperty(value="主题")
    private String topic;

    /**
     * 消息
     */
    @TableField(value = "message")
    @ApiModelProperty(value="消息")
    private String message;

    /**
     * 客户端ID
     */
    @TableField(value = "clientId")
    @ApiModelProperty(value="客户端ID")
    private Long clientid;

    /**
     * 消息类型，发送：Send、接收：Receive
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value="消息类型，发送：Send、接收：Receive")
    private String type;

    /**
     * 命令是否正确
     */
    @TableField(value = "correct")
    @ApiModelProperty(value="命令是否正确")
    private String correct;

    @TableField(value = "create_time")
    @ApiModelProperty(value="")
    private LocalDateTime createTime;

    @TableField(value = "head")
    @ApiModelProperty(value="")
    private String head;

    @TableField(value = "version")
    @ApiModelProperty(value="")
    private String version;

    @TableField(value = "total_length")
    @ApiModelProperty(value="")
    private String totalLength;

    @TableField(value = "operation_type")
    @ApiModelProperty(value="")
    private String operationType;

    @TableField(value = "value_length")
    @ApiModelProperty(value="")
    private String valueLength;

    @TableField(value = "`value`")
    @ApiModelProperty(value="")
    private String value;

    @TableField(value = "crc")
    @ApiModelProperty(value="")
    private String crc;

    /**
     * 是否反馈
     */
    @TableField(value = "feedback")
    @ApiModelProperty(value="是否反馈")
    private Integer feedback;
}