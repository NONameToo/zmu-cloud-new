package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class PigImmuneVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "登记时间")
    private Date registrationTime;

    @ApiModelProperty(value = "管理类型code 1免疫，2防治")
    private String manageType;

    @ApiModelProperty(value = "管理类型内容 1免疫，2防治")
    private String manageTypeContent;

    @ApiModelProperty(value="免疫对象code 1栋舍，2猪群 ，3猪只")
    private String immuneObject;

    @ApiModelProperty(value="免疫对象内容 1栋舍，2猪群 ，3猪只")
    private String immuneObjectContent;

    @ApiModelProperty(value="猪只耳号/群号/栋舍名")
    private String immuneNumber;

    @ApiModelProperty(value="位置id")
    private String locationId;

    @ApiModelProperty(value="位置")
    private String location;

    @ApiModelProperty(value="类型")
    private String immuneType;

    @ApiModelProperty(value="项目 ")
    private String immuneItem;

    @ApiModelProperty(value="疫苗名称")
    private String vaccineName;

    @ApiModelProperty(value="总用量")
    private Long totalConsumption;

    @ApiModelProperty(value="剂量单位")
    private String doseUnit;

    @ApiModelProperty(value="免疫方式")
    private String immuneMode;

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
