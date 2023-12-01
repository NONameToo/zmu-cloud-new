package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class PigPreventionCureDTO extends BaseEntity {

    private Long id;

    @NotNull
    @ApiModelProperty(value = "登记时间",required = true)
    private Date registrationTime;

    @ApiModelProperty(value="管理类型 1免疫，2防治",required = true)
    @NotEmpty
    private String manageType;

    @ApiModelProperty(value="防治对象 1栋舍，2猪群 ，3猪只",required = true)
    @NotEmpty
    private String preventionCureObject;

    @ApiModelProperty(value="猪只耳号",required = true)
    private String preventionCureNumber;

    @ApiModelProperty(value="位置id",required = true)
    @NotNull
    private Long locationId;

    @ApiModelProperty(value="疾病",required = true)
    @NotEmpty
    private String preventionCureDisease;

    @ApiModelProperty(value="症状",required = true)
    @NotEmpty
    private String preventionCureSymptom;

    @ApiModelProperty(value="药品名称",required = true)
    @NotEmpty
    private String medicineName;

    @ApiModelProperty(value="总用量",required = true)
    @NotNull
    private Long totalConsumption;

    @ApiModelProperty(value="剂量单位",required = true)
    @NotEmpty
    private String doseUnit;

    @ApiModelProperty(value="用药方式",required = true)
    @NotEmpty
    private String medicineMode;

    @ApiModelProperty(value = "操作人id")
    private Long operator;

    @ApiModelProperty(value="删除标识")
    private String del;
}
