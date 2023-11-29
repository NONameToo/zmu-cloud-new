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
    * 混养栏位
    */
@ApiModel(value="com-zmu-cloud-commons-entity-BlendFeedColumn")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "blend_feed_column")
public class BlendFeedColumn {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    @TableField(value = "blend_feed_id")
    @ApiModelProperty(value="")
    private Long blendFeedId;

    @TableField(value = "farm_id")
    @ApiModelProperty(value="")
    private Long farmId;

    @TableField(value = "house_id")
    @ApiModelProperty(value="")
    private Long houseId;

    @TableField(value = "col_id")
    @ApiModelProperty(value="")
    private Long colId;

    @TableField(value = "position")
    @ApiModelProperty(value="")
    private String position;
}