package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author YH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ColumnVo extends FeederVo {

    @ApiModelProperty("栏位ID")
    private Long id;
    @ApiModelProperty("栏位编号")
    private Integer no;
    @ApiModelProperty("栋舍ID")
    private Long houseId;
    @ApiModelProperty("栋舍名称")
    private String houseName;
    @ApiModelProperty("栋舍类型:不同类型展示不同的页面[1, 2, 3, 5, 6] 种猪页面")
    private Integer houseType;

    @ApiModelProperty("猪只ID")
    private Long pigId;
    @ApiModelProperty("猪只耳号")
    private String earNumber;
    @ApiModelProperty("个体号")
    private String individualNumber;
    @ApiModelProperty("猪只性别,1公猪，2母猪")
    private Integer sex;
    @ApiModelProperty("当前胎次的配种日期")
    private String breedDate;
    @ApiModelProperty("当前胎次的分娩日期")
    private String laborDate;
    @ApiModelProperty("当前胎次的分娩可饲养仔猪数")
    private Integer piggy;

    @ApiModelProperty("背膘")
    private Integer backFat;
    @ApiModelProperty("背膘阶段")
    private Integer backFatStage;
    @ApiModelProperty("背膘日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date backFatDate;

    @ApiModelProperty("系统饲喂量")
    private Integer sysFeedingAmount;
    @ApiModelProperty("系统饲喂量说明")
    private String sysFeedingAmountDesc;
    @ApiModelProperty("修正饲喂量(克)")
    private Integer feedingAmount;
}
