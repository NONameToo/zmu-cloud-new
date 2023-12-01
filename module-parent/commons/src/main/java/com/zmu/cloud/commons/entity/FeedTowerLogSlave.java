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
 * 料塔日志从表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerLogSlave")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feed_tower_log_slave")
public class FeedTowerLogSlave {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    @TableField(value = "log_id")
    @ApiModelProperty(value = "")
    private Long logId;

    @TableField(value = "device_no")
    @ApiModelProperty(value = "")
    private String deviceNo;

    @TableField(value = "task_no")
    @ApiModelProperty(value = "")
    private String taskNo;

    /**
     * 测量数据
     */
    @TableField(value = "data")
    @ApiModelProperty(value = "测量数据")
    private String data;

    @TableField(value = "create_time")
    @ApiModelProperty(value = "")
    private LocalDateTime createTime;
}