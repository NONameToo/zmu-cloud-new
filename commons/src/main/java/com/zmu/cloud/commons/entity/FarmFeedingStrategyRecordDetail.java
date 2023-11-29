package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 饲喂策略记录明细
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FarmFeedingStrategyRecordDetail")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "farm_feeding_strategy_record_detail")
public class FarmFeedingStrategyRecordDetail {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 阶段
     */
    @TableField(value = "stage")
    @ApiModelProperty(value = "阶段")
    private String stage;

    /**
     * 饲料
     */
    @TableField(value = "feed")
    @ApiModelProperty(value = "饲料")
    private String feed;

    @TableField(value = "one")
    @ApiModelProperty(value = "")
    private String one;

    /**
     * 偏瘦
     */
    @TableField(value = "thin")
    @ApiModelProperty(value = "偏瘦")
    private String thin;

    /**
     * 适宜
     */
    @TableField(value = "suitable")
    @ApiModelProperty(value = "适宜")
    private String suitable;

    /**
     * 偏胖
     */
    @TableField(value = "fat")
    @ApiModelProperty(value = "偏胖")
    private String fat;

    @TableField(value = "five")
    @ApiModelProperty(value = "")
    private String five;

    /**
     * 策略主记录
     */
    @TableField(value = "record_id")
    @ApiModelProperty(value = "策略主记录")
    private Long recordId;
}