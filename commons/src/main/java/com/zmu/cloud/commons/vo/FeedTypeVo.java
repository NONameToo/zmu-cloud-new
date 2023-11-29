package com.zmu.cloud.commons.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author YH
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("饲料品类")
public class FeedTypeVo implements Serializable {

    private Long id;
    private Long farmId;
    @ApiModelProperty(value = "品类名称")
    private String name;
    @ApiModelProperty(value = "密度（g/m³）")
    private Long density;
    @ApiModelProperty(value = "密度（Kg/m³）")
    private String densityString;
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