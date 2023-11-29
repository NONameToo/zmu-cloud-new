package com.zmu.cloud.commons.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 公司表
 */
@ApiModel(value = "公司表")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "company", resultMap = "BaseResultMap")
public class Company {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 公司名
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "公司名")
    private String name;

    /**
     * 联系人
     */
    @TableField(value = "contact_name")
    @ApiModelProperty(value = "联系人")
    private String contactName;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 省市区id
     */
    @TableField(value = "area_id")
    @ApiModelProperty(value = "省市区id")
    private Integer areaId;

    @TableField(value = "province_id")
    @ApiModelProperty(value="省id")
    private Integer provinceId;

    @TableField(value = "city_id")
    @ApiModelProperty(value="城市id")
    private Integer cityId;

    /**
     * 详细地址
     */
    @TableField(value = "address")
    @ApiModelProperty(value = "详细地址")
    private String address;

    /**
     * 是否可用：0-不可用，1-可用
     */
    @TableField(value = "enabled")
    @ApiModelProperty(value = "是否可用：0-不可用，1-可用")
    private boolean enabled;

    /**
     * 0-未删除，1-已删除
     */
    @TableField(value = "del")
    @ApiModelProperty(value = "0-未删除，1-已删除")
    private boolean del;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    @ApiModelProperty(value = "创建人")
    private Long createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by")
    @ApiModelProperty(value = "更新人")
    private Long updateBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty("省名称")
    @TableField(exist = false)
    private String provinceName;
    @ApiModelProperty("市名称")
    @TableField(exist = false)
    private String cityName;
    @ApiModelProperty("区名称")
    @TableField(exist = false)
    private String areaName;
}