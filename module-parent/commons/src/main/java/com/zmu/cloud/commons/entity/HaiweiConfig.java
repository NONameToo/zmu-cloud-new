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
    * 海为云集成配置
    */
@ApiModel(value="com-zmu-cloud-commons-entity-HaiweiConfig")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "haiwei_config")
public class HaiweiConfig {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 所属猪场
     */
    @TableField(value = "farm_id")
    @ApiModelProperty(value="所属猪场")
    private Long farmId;

    @TableField(value = "house_id")
    @ApiModelProperty(value="")
    private Long houseId;

    /**
     * 集成链接
     */
    @TableField(value = "url")
    @ApiModelProperty(value="集成链接")
    private String url;

    /**
     * 集成链接
     */
    @TableField(value = "device")
    @ApiModelProperty(value="集成链接")
    private String device;

    /**
     * 账号
     */
    @TableField(value = "account")
    @ApiModelProperty(value="账号")
    private String account;

    /**
     * 项目私钥
     */
    @TableField(value = "privateKey")
    @ApiModelProperty(value="项目私钥")
    private String privatekey;

    /**
     * 机器码
     */
    @TableField(value = "machineCode")
    @ApiModelProperty(value="机器码")
    private String machinecode;

    /**
     * 平台代码
     */
    @TableField(value = "platform")
    @ApiModelProperty(value="平台代码")
    private Integer platform;
}