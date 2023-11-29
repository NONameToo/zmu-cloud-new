package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 主机
 * @author YH
 */
@ApiModel(value="主机")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableName(value = "gateway")
public class Gateway extends BaseEntity implements Serializable {

    /**
     * 栋舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value="栋舍id")
    private Long pigHouseId;

    /**
     * 主机编号
     */
    @TableField(value = "client_id")
    @ApiModelProperty(value="主机编号")
    private Long clientId;

    /**
     * 料线ID
     */
    @TableField(value = "material_line_id")
    @ApiModelProperty(value="料线ID")
    private Long materialLineId;

    /**
     * 最近的在线时间
     */
    @TableField(value = "online_time")
    @ApiModelProperty(value="最近的在线时间")
    private LocalDateTime onlineTime;

}