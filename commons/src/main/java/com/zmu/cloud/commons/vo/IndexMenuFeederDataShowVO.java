package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
    * 首页饲喂器菜单数据
    */
@ApiModel(value="com.zmu.cloud.commons.vo.IndexMenuFeederDataShowVO")
@Data
public class IndexMenuFeederDataShowVO implements Serializable {
    /**
    * 首页菜单类型ID
    */
    @ApiModelProperty(value="配怀设备个数")
    private long ph;

    @ApiModelProperty(value="后备设备个数")
    private long hb;

    @ApiModelProperty(value="公猪设备个数")
    private long gz;

    @ApiModelProperty(value="分娩设备个数")
    private long fm;

    @ApiModelProperty(value="保育设备个数")
    private long by;


    /**
     * 饲喂量
     */
    @ApiModelProperty(value="配怀饲喂量")
    private BigDecimal sph;

    @ApiModelProperty(value="配怀饲喂量百分比")
    private BigDecimal sphPercent;

    @ApiModelProperty(value="后备饲喂量")
    private BigDecimal shb;

    @ApiModelProperty(value="后备饲喂量百分比")
    private BigDecimal shbPercent;


    @ApiModelProperty(value="公猪饲喂量")
    private BigDecimal sgz;

    @ApiModelProperty(value="公猪饲喂量百分比")
    private BigDecimal sgzPercent;


    @ApiModelProperty(value="分娩饲喂量")
    private BigDecimal sfm;

    @ApiModelProperty(value="分娩饲喂量百分比")
    private BigDecimal sfmPercent;



    @ApiModelProperty(value="保育饲喂量")
    private BigDecimal sby;

    @ApiModelProperty(value="保育饲喂量百分比")
    private BigDecimal sbyPercent;



    /**
     * 年度饲喂量
     */
    @ApiModelProperty(value="年度饲喂量")
    private BigDecimal ys;

    private static final long serialVersionUID = 1L;
}