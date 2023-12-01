package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseFeedingDTO implements Serializable {

    @ApiModelProperty(value = "饲喂器号")
    private int feederCode;
    @ApiModelProperty(value = "饲喂重量")
    private int weight;

}
