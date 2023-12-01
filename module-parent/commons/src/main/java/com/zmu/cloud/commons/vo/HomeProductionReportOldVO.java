package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  首页生产汇总对象
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeProductionReportOldVO {

    @ApiModelProperty("配种情况")
    private Mating mating;

    @ApiModelProperty("妊检情况")
    private Pregnancy pregnancy;

    @ApiModelProperty("死淘情况")
    private DeadAmoy deadAmoy;


    @Data
    @ApiModel
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Mating {
        @ApiModelProperty("总数")
        private Integer count;

        @ApiModelProperty("头产")
        private Integer tc;

        @ApiModelProperty("经产")
        private Integer jc;

    }

    @Data
    @ApiModel
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Pregnancy{
        @ApiModelProperty("总数")
        private Integer count;
        @ApiModelProperty("妊娠")
        private Integer ss;
        @ApiModelProperty("空怀")
        private Integer kh;
        @ApiModelProperty("返情")
        private Integer fq;
        @ApiModelProperty("流产")
        private Integer lc;
    }

    @Data
    @ApiModel
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class DeadAmoy {
        @ApiModelProperty("总数")
        private Integer count;
        @ApiModelProperty("死亡")
        private Integer sw;
        @ApiModelProperty("淘汰")
        private Integer tt;
        @ApiModelProperty("其他")
        private Integer qt;
    }
}
