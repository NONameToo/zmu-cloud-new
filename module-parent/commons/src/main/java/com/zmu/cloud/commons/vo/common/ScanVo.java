package com.zmu.cloud.commons.vo.common;

import com.zmu.cloud.commons.vo.sph.ScanType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("饲喂器二维码")
public class ScanVo {

    @ApiModelProperty("扫描栏位的类型")
    private ScanType scanType;
    @ApiModelProperty("栏位ID")
    private Long colId;
    @ApiModelProperty("位置")
    private String position;
    @ApiModelProperty("二维码编号")
    private String qrcode;
    @ApiModelProperty("饲喂器")
    private String feeder;
    @ApiModelProperty("主机号")
    private Long clientId;
    @ApiModelProperty("饲喂器编号")
    private Integer feederCode;
    @ApiModelProperty("饲喂器是否可用")
    private Integer feederEnable;
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
    @ApiModelProperty("背膘")
    private Integer backFat;
    @ApiModelProperty("背膘阶段")
    private Integer backFatStage;
    @ApiModelProperty("背膘日期")
    private LocalDate backFatDate;
    @ApiModelProperty("当前胎次的配种日期")
    private LocalDate breedDate;
    @ApiModelProperty("系统饲喂量")
    private Integer sysFeedingAmount;
    @ApiModelProperty("系统饲喂量说明")
    private String sysFeedingAmountDesc;
    @ApiModelProperty("修正饲喂量(克)")
    private Integer feedingAmount;

}
