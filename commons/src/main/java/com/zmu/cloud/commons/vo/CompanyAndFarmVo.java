package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("猪场")
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAndFarmVo {

    @ApiModelProperty("公司名称")
    private String company;
    @ApiModelProperty("猪场ID")
    private String farmId;
    @ApiModelProperty("猪场名称")
    private String farmName;
    @ApiModelProperty("猪场名称首字母")
    private String farmNameInitials;
    @ApiModelProperty("猪场序号")
    private String rn;
    @ApiModelProperty("切换次数")
    private int times;

}
