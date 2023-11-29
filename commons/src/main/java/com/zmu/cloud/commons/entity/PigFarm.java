package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmu.cloud.commons.vo.TowerVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 猪场
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigFarm")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_farm")
public class PigFarm {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 猪场类型1.种猪场，2育种场，3自繁自养，4，商品猪场，5，家庭农场，6，集团猪场
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "猪场类型1.种猪场，2育种场，3自繁自养，4，商品猪场，5，家庭农场，6，集团猪场")
    private Integer type;

    /**
     * 猪场猪只类型id
     */
    @TableField(value = "pig_type_id")
    @ApiModelProperty(value = "猪场猪只类型id")
    private Long pigTypeId;

    /**
     * 规模，1.100头以下，2，100-500头，3，500-1000头，4，1000-3000头，5，3000头以上
     */
    @TableField(value = "level")
    @ApiModelProperty(value = "规模，1.100头以下，2，100-500头，3，500-1000头，4，1000-3000头，5，3000头以上")
    private Integer level;

    /**
     * 负责人，关联用户id
     */
    @TableField(value = "principal_id")
    @ApiModelProperty(value = "负责人，关联用户id")
    private Long principalId;

    /**
     * 负责人联系电话
     */
    @TableField(value = "principal_tel")
    @ApiModelProperty(value = "负责人联系电话")
    private String principalTel;

    /**
     * 省id
     */
    @TableField(value = "province_id")
    @ApiModelProperty(value = "省id")
    private Integer provinceId;

    /**
     * 市id
     */
    @TableField(value = "city_id")
    @ApiModelProperty(value = "市id")
    private Integer cityId;

    /**
     * 行政区划代码
     */
    @TableField(value = "area_id")
    @ApiModelProperty(value = "行政区划代码")
    private Integer areaId;

    /**
     * 详细地址
     */
    @TableField(value = "address")
    @ApiModelProperty(value = "详细地址")
    private String address;

    /**
     * 默认饲喂量
     */
    @TableField(value = "default_feeding_amount")
    @ApiModelProperty(value = "默认饲喂量")
    private Integer defaultFeedingAmount;

    /**
     * 0未删除，1已删除
     */
    @TableField(value = "del")
    @ApiModelProperty(value = "0未删除，1已删除")
    private String del;

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
     * 修改人
     */
    @TableField(value = "update_by")
    @ApiModelProperty(value = "修改人")
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

    /**
     * 账户余额单位:分
     */
    @TableField(value = "balance")
    @ApiModelProperty(value = "账户余额单位:分")
    private Integer balance;

    /**
     * 是否巨星猪场：0：否，1：是
     */
    @TableField(value = "jx")
    @ApiModelProperty(value = "是否巨星猪场：0：否，1：是")
    private Integer jx;

    @TableField(exist = false)
    @ApiModelProperty(value = "料塔列表")
    private List<TowerVo> towers;
}