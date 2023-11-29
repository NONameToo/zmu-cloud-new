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
 * 报料表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerApply")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feed_tower_apply")
public class FeedTowerApply {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 报料单号
     */
    @TableField(value = "apply_code")
    @ApiModelProperty(value = "报料单号")
    private String applyCode;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 猪场id
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "猪场id")
    private Long pigFarmId;

    /**
     * 料塔id
     */
    @TableField(value = "tower_id")
    @ApiModelProperty(value = "料塔id")
    private Long towerId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 饲料品类
     */
    @TableField(value = "feed_type")
    @ApiModelProperty(value = "饲料品类")
    private String feedType;

    /**
     * 报料量,单位:g
     */
    @TableField(value = "total")
    @ApiModelProperty(value = "报料量,单位:g")
    private Long total;

    /**
     * 状态
     */
    @TableField(value = "apply_status")
    @ApiModelProperty(value = "状态")
    private Integer applyStatus;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
}