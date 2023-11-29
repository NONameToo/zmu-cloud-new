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
    * 巨星公司表
    */
@ApiModel(value="com-zmu-cloud-commons-entity-SphCompany")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sph_company")
public class SphCompany {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 公司名称
     */
    @TableField(value = "org_nm")
    @ApiModelProperty(value="公司名称")
    private String orgNm;

    /**
     * 是否启用
     */
    @TableField(value = "is_use")
    @ApiModelProperty(value="是否启用")
    private Integer isUse;

    /**
     * 公司类型
     */
    @TableField(value = "org_type")
    @ApiModelProperty(value="公司类型")
    private Long orgType;

    /**
     * 上级公司
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value="上级公司")
    private Long parentId;
}