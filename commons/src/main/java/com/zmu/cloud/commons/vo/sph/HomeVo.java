package com.zmu.cloud.commons.vo.sph;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeVo {
    private Long farmId;
    private String farmName;
    private String farmTel;
    private String farmAddress;
    @ApiModelProperty(value = "场长ID")
    private Long principalId;
    @ApiModelProperty(value = "场长")
    private String principal;
    @ApiModelProperty(value = "猪种")
    private String pigType;
}
