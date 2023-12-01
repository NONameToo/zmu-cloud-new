package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  首页分娩汇总
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeLaborReportVO {

        @ApiModelProperty("分娩窝数")
        private Integer ws;

        @ApiModelProperty("分娩总数")
        private Integer zs;

        @ApiModelProperty("窝均头数")
        private Double wjts;

        @ApiModelProperty("死亡头数")
        private Integer swts;

        @ApiModelProperty("存栏数")
        private Integer cl;

        @ApiModelProperty("死亡率")
        private Double swl;

}
