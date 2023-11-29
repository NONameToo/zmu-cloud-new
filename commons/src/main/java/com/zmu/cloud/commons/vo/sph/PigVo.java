package com.zmu.cloud.commons.vo.sph;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PigVo {

    @ApiModelProperty(value = "猪只ID")
    private Long pigId;
    @ApiModelProperty(value = "耳号")
    private String earNumber;
    @ApiModelProperty(value = "个体号")
    private String individualNumber;
    @ApiModelProperty(value = "性别")
    private Integer sex;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "出生日期")
    private LocalDate bornDate;
    @ApiModelProperty(value = "当前胎次")
    private Integer currParities;
    @ApiModelProperty(value = "配种胎次")
    private Integer breedParities;
    @ApiModelProperty(value = "日龄")
    private Integer ageOfDay;
    @ApiModelProperty(value = "品种ID")
    private Long varietyId;
    @ApiModelProperty(value = "品种")
    private String variety;
    @ApiModelProperty(value = "品系ID")
    private Long strainId;
    @ApiModelProperty(value = "品系")
    private String strain;
    @ApiModelProperty(value = "状态ID")
    private Long statusId;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "猪场ID")
    private Long farmId;
    @ApiModelProperty(value = "猪场名称")
    private String farmName;
    @ApiModelProperty(value = "栋舍ID")
    private Long houseId;
    @ApiModelProperty(value = "栋舍名称")
    private String houseName;
    @ApiModelProperty(value = "栏位ID")
    private Long fieldId;
    @ApiModelProperty(value = "栏位编号")
    private String fieldCode;
    @ApiModelProperty(value = "猪只类型ID")
    private Long pigTypeId;
    @ApiModelProperty(value = "猪只类型名称")
    private String pigTypeName;
    @ApiModelProperty(value = "背膘")
    private Integer backFat;

}
