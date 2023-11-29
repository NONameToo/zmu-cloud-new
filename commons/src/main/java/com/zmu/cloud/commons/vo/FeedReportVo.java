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
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("首页饲喂汇总")
public class FeedReportVo {

    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("总量")
    private BigDecimal total;
    private List<FeedReportDetailVo> data;

}
