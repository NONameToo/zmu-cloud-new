package com.zmu.cloud.commons.entity;

import com.alibaba.excel.annotation.ExcelIgnore;import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * app版本管理表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-Version")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "version")
public class Version {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 版本号
     */
    @TableField(value = "version")
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
     * 审核版本号
     */
    @TableField(value = "code")
    @ApiModelProperty(value = "审核版本号")
    private Integer code;

    /**
     * 更新提示语
     */
    @TableField(value = "tips")
    @ApiModelProperty(value = "更新提示语")
    private String tips;

    /**
     * 下载地址
     */
    @TableField(value = "download")
    @ApiModelProperty(value = "下载地址")
    private String download;

    /**
     * 系统：1 安卓，2 iOS
     */
    @TableField(value = "os")
    @ApiModelProperty(value = "系统：1 安卓，2 iOS")
    private Integer os;

    /**
     * 更新类型：0 提示更新，1 强制更新
     */
    @TableField(value = "update_type")
    @ApiModelProperty(value = "更新类型：0 提示更新，1 强制更新")
    private Integer updateType;

    /**
     * 状态：0 下架，1 上架
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "状态：0 下架，1 上架")
    private Integer status;

    /**
     * 当前版本是非在审核：0 否，1 是
     */
    @TableField(value = "audit")
    @ApiModelProperty(value = "当前版本是非在审核：0 否，1 是")
    private Integer audit;

    /**
     * 0 未删除，1 已删除
     */
    @TableField(value = "del")
    @ApiModelProperty(value = "0 未删除，1 已删除")
    private String del;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人id
     */
    @TableField(value = "create_by")
    @ApiModelProperty(value = "创建人id")
    private Long createBy;

    /**
     * 更新人id
     */
    @TableField(value = "update_by")
    @ApiModelProperty(value = "更新人id")
    private Long updateBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "文件编码")
    @TableField(exist = false)
    private String md5;

}