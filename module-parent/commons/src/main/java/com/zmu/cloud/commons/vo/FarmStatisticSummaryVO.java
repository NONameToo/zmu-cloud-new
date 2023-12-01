package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-26 22:57
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmStatisticSummaryVO {

    @ApiModelProperty("公猪")
    private Integer boar;

    @ApiModelProperty("母猪")
    private Integer sow;

    @ApiModelProperty("肉猪")
    private Integer pork;

    @ApiModelProperty("猪仔")
    private Integer piggy;

    @ApiModelProperty("母猪的各种状态对应的数量")
    private List<Num> nums;

    @Data
    @ApiModel
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Num {
        @ApiModelProperty("1.后备，2 配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳，8断奶")
        private Integer status;
        @ApiModelProperty("数量")
        private Integer num;
    }
}
