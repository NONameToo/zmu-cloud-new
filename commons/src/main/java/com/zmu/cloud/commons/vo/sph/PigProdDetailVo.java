package com.zmu.cloud.commons.vo.sph;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PigProdDetailVo {

    @ApiModelProperty(value = "类别：1:采精记录; 2:发情记录; 3:配种记录; 4：妊检记录; 5:分娩记录; 6:断奶记录; 7:死亡记录;8:淘汰记录;")
    private Integer type;
    @ApiModelProperty(value = "类别日期")
    private LocalDate typeDate;
    @ApiModelProperty(value = "胎次")
    private Integer parities;
    @ApiModelProperty(value = "首配公猪")
    private String boarPig;
    @ApiModelProperty(value = "评分")
    private String score;
    @ApiModelProperty(value = "预产日期")
    private LocalDate expectedDate;
    @ApiModelProperty(value = "发情间隔")
    private Integer estrousInterval;

    @ApiModelProperty(value = "妊检结果")
    private String result;

    @ApiModelProperty(value = "活仔X头")
    private int liveZz;
    @ApiModelProperty(value = "总仔X头")
    private int sumZz;
    @ApiModelProperty(value = "健")
    private int qualified;
    @ApiModelProperty(value = "弱")
    private int weakZz;
    @ApiModelProperty(value = "畸")
    private int deformity;
    @ApiModelProperty(value = "死")
    private int weakDie;
    @ApiModelProperty(value = "木")
    private int mummy;

    @ApiModelProperty(value = "断奶仔数")
    private Integer pigletAmount;
    @ApiModelProperty(value = "断奶窝重")
    private BigDecimal birthWeight;

}
