package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
public class FarmStatisticProductionTotalVO {

    @ApiModelProperty("配种情况")
    private List<Mating> mating;
    @ApiModelProperty("断奶情况")
    private Weaned weaned;
    @ApiModelProperty("分娩情况")
    private Labor labor;

    // -----------

    @Data
    @ApiModel
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Mating {

        @ApiModelProperty("数量")
        private Integer count;
        @ApiModelProperty("1.后备，2配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳 8.断奶")
        private Integer type;

    }

    @Data
    @ApiModel
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Weaned {

        @ApiModelProperty("断奶母猪头数")
        private Integer sow;
        @ApiModelProperty("断奶仔猪头数")
        private Integer piggy;
        @ApiModelProperty("窝均断奶数")
        private BigDecimal avg;
        @ApiModelProperty("窝均断奶重/kg")
        private BigDecimal avgKg;
    }

    @Data
    @ApiModel
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Labor {

        @ApiModelProperty("分娩母猪")
        private Integer sow;
        @ApiModelProperty("总活仔")
        private Integer totalLive;
        @ApiModelProperty("活仔母")
        private Integer live;
        @ApiModelProperty("畸形")
        private Integer deformity;
        @ApiModelProperty("死胎")
        private Integer dead;
        @ApiModelProperty("总产仔")
        private Integer totalLabor;
        @ApiModelProperty("健仔")
        private Integer healthy;
        @ApiModelProperty("弱仔")
        private Integer weak;
        @ApiModelProperty("木乃伊")
        private Integer mummy;
    }
}
