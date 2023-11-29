package com.zmu.cloud.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlendFeedColumnDto {

    private Integer firstAmount;
    private Integer feedAgain;
    private Integer feedAgainAmount;

}
