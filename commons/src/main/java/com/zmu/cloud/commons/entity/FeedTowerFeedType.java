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

import java.time.LocalDateTime;

@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerFeedType")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feed_tower_feed_type")
public class FeedTowerFeedType {
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
     * 品类名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "品类名称")
    private String name;

    /**
     * 密度（g/m³）
     */
    @TableField(value = "density")
    @ApiModelProperty(value = "密度（g/m³）")
    private Long density;

    /**
     * 是否内置
     */
    @TableField(value = "inlay")
    @ApiModelProperty(value = "是否内置")
    private boolean inlay;

    @ApiModelProperty(value = "创建人id", hidden = true)
    private Long createBy;
    @ApiModelProperty(value = "更新人id", hidden = true)
    private Long updateBy;
    @ApiModelProperty(value = "更新人Name", hidden = true)
    @TableField(exist = false)
    private String updateByName;
    @ApiModelProperty(value = "创建时间", hidden = true)
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    private LocalDateTime updateTime;
}