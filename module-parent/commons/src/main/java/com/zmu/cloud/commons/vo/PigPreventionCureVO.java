package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class PigPreventionCureVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "登记时间")
    private Date registrationTime;

    @ApiModelProperty(value = "管理类型 1免疫，2防治")
    private String manageType;

    @ApiModelProperty(value="免疫对象 1栋舍，2猪群 ，3猪只")
    private String preventionCureObject;

    @ApiModelProperty(value="猪只耳号/群号/栋舍名")
    private String preventionCureNumber;

    @ApiModelProperty(value="位置id")
    private String locationId;

    @ApiModelProperty(value="位置")
    private String location;

    @ApiModelProperty(value="疾病")
    private String preventionCureDisease;

    @ApiModelProperty(value="症状")
    private String preventionCureSymptom;

    @ApiModelProperty(value="药品名称")
    private String medicineName;

    @ApiModelProperty(value="总用量")
    private Long totalConsumption;

    @ApiModelProperty(value="剂量单位")
    private String doseUnit;

    @ApiModelProperty(value="用药方式")
    private String medicineMode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "更新人")
    private Long updateBy;

    @ApiModelProperty(value = "公司id")
    private Long companyId;

    @ApiModelProperty(value = "猪场id")
    private Long pigFarmId;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @ExcelProperty("更新时间")
    private Date updateTime;
}
