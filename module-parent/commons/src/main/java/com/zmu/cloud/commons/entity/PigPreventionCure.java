package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.Date;


@ApiModel(value = "com-zmu-cloud-commons-entity-PigPreventionCure")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_prevention_cure")
@SuperBuilder
public class PigPreventionCure extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 管理类型
     */
    @TableField(value = "manage_type")
    @ApiModelProperty(value = "管理类型 1免疫，2防治", required = true)
    @NotNull(message = "管理类型不能为空")
    private String manageType;

    /**
     * 登记时间
     */
    @TableField(value = "registration_time")
    @ApiModelProperty(value = "登记时间", required = true)
    @NotNull(message = "登记时间不能为空")
    private Date registrationTime;

    /**
     * 防治对象
     */
    @TableField(value = "prevention_cure_object")
    @ApiModelProperty(value="防治对象 1栋舍，2猪群 ，3猪只",required = true)
    @NotNull(message = "防治对象不能为空")
    private String preventionCureObject;

    /**
     * 猪只耳号/群号/栋舍名
     */
    @TableField(value = "prevention_cure_number")
    @ApiModelProperty(value="猪只耳号/群号/栋舍",required = true)
    @NotNull(message = "猪只耳号/群号/栋舍不能为空")
    private String preventionCureNumber;

    /**
     * 位置id
     */
    @TableField(value = "location_id")
    @NotNull(message = "位置id不能为空")
    @ApiModelProperty(value="位置id",required = true)
    private Long locationId;

    /**
     * 疾病
     */
    @TableField(value = "prevention_cure_disease")
    @NotNull(message = "疾病不能为空")
    @ApiModelProperty(value="疾病",required = true)
    private String preventionCureDisease;

    /**
     * 症状
     */
    @TableField(value = "prevention_cure_symptom")
    @NotNull(message = "症状不能为空")
    @ApiModelProperty(value="症状",required = true)
    private String preventionCureSymptom;

    /**
     * 药品名称
     */
    @TableField(value = "medicine_name")
    @NotNull(message = "药品名称不能为空")
    @ApiModelProperty(value="药品名称",required = true)
    private String medicineName;

    /**
     * 总用量
     */
    @TableField(value = "total_consumption")
    @NotNull(message = "总用量不能为空")
    @ApiModelProperty(value="总用量",required = true)
    private Long totalConsumption;

    /**
     * 剂量单位
     */
    @TableField(value = "dose_unit")
    @NotNull(message = "剂量单位不能为空")
    @ApiModelProperty(value="剂量单位",required = true)
    private String doseUnit;

    /**
     * 用药方式
     */
    @TableField(value = "medicine_mode")
    @NotNull(message = "用药方式不能为空")
    @ApiModelProperty(value="用药方式",required = true)
    private String medicineMode;

    /**
     * 操作人id
     */
    @TableField(value = "operator")
    @ApiModelProperty(value = "操作人id")
    private Long operator;

    /**
     * 删除：0-否，1-是
     */
    @TableField(value = "del")
    @ApiModelProperty(value = "删除：0-否，1-是")
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
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}