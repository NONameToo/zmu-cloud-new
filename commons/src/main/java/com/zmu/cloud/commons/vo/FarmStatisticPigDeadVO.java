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
public class FarmStatisticPigDeadVO {

    @ApiModelProperty("死淘原因 1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，\n" +
            "5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它")
    private Integer reason;
    @ApiModelProperty("公猪")
    private List<Detail> boar;
    @ApiModelProperty("母猪")
    private List<Detail> sow;
    @ApiModelProperty("肉猪")
    private List<Detail> pork;
    @ApiModelProperty("猪仔")
    private List<Detail> piggy;
    @ApiModelProperty("合计")
    private Integer total;
    @ApiModelProperty("占比")
    private BigDecimal percentage;

    @Data
    @ApiModel
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Detail{
        @ApiModelProperty("猪舍位置")
        private String name;
        @ApiModelProperty("猪舍位置code")
        private String position;
        @ApiModelProperty("公猪、母猪时为耳号，肉猪为群号，猪仔为母猪的耳号")
        private String value;
    }
}
