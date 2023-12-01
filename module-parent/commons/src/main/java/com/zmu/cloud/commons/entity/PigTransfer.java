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

@ApiModel(value = "com-zmu-cloud-commons-entity-PigTransfer")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_transfer")
public class PigTransfer {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "")
    private Long pigFarmId;

    /**
     * 栋舍ID
     */
    @TableField(value = "house_id")
    @ApiModelProperty(value = "栋舍ID")
    private Long houseId;

    /**
     * 栋舍类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "栋舍类型")
    private Integer houseType;

    /**
     * 栋舍名称
     */
    @TableField(value = "house_name")
    @ApiModelProperty(value = "栋舍名称")
    private String houseName;

    /**
     * 转猪数量
     */
    @TableField(value = "cnt")
    @ApiModelProperty(value = "转猪数量")
    private Integer cnt;

    @TableField(value = "del")
    @ApiModelProperty(value = "")
    private String del;

    @TableField(value = "create_by")
    @ApiModelProperty(value = "")
    private Long createBy;

    /**
     * 转猪时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "转猪时间")
    private LocalDateTime createTime;
}