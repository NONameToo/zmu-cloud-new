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
public class PigImmuneDTO extends BaseEntity {

    private Long id;

    @NotNull
    @ApiModelProperty(value = "登记时间",required = true)
    private Date registrationTime;

    @ApiModelProperty(value="管理类型 1免疫，2防治",required = true)
    @NotEmpty
    private String manageType;

    @ApiModelProperty(value="免疫对象 1栋舍，2猪群 ，3猪只",required = true)
    @NotEmpty
    private String immuneObject;

    @ApiModelProperty(value="猪只耳号/群号/栋舍号",required = true)
    @NotEmpty
    private String immuneNumber;

    @ApiModelProperty(value="位置id",required = true)
    @NotNull
    private Long locationId;

    @ApiModelProperty(value="类型",required = true)
    @NotEmpty
    private String immuneType;

    @ApiModelProperty(value="项目",required = true)
    @NotEmpty
    private String immuneItem;

    @ApiModelProperty(value="疫苗名称",required = true)
    @NotEmpty
    private String vaccineName;

    @ApiModelProperty(value="总用量",required = true)
    @NotNull
    private Long totalConsumption;

    @ApiModelProperty(value="剂量单位",required = true)
    @NotEmpty
    private String doseUnit;

    @ApiModelProperty(value="免疫方式",required = true)
    @NotEmpty
    private String immuneMode;

    @ApiModelProperty(value = "操作人id")
    private Long operator;

    @ApiModelProperty(value="删除标识")
    private String del;
}
