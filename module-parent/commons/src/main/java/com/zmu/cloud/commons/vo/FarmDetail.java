package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhaojian
 * @create 2023/10/7 15:34
 * @Description 猪场详情
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("猪场详情")
public class FarmDetail {

        @ApiModelProperty("猪场id")
        private Long farmId;
        @ApiModelProperty("猪场名称")
        private String farmName;
        @ApiModelProperty("猪场在线设备数")
        private Long onFarmCount;
        @ApiModelProperty("猪场离线设备数")
        private Long offFarmCount;
        @ApiModelProperty("猪场未绑定设备数")
        private Long unBindFarmCount;
        @ApiModelProperty("猪场全部设备数")
        private Long sumFarmCount;
        @ApiModelProperty("耗料")
        private BigDecimal expend;
}