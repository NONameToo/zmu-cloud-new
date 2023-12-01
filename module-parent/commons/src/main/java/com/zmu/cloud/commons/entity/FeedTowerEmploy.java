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
    * 人员访问权限
    */
@ApiModel(value="com-zmu-cloud-commons-entity-FeedTowerEmploy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feed_tower_employ")
public class FeedTowerEmploy {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    @TableField(value = "tower_id")
    @ApiModelProperty(value="")
    private Long towerId;

    @TableField(value = "employ_id")
    @ApiModelProperty(value="")
    private Long employId;
}