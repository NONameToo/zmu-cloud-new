package com.zmu.cloud.commons.dto;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.entity.PigLabor;
import com.zmu.cloud.commons.entity.PigMating;
import com.zmu.cloud.commons.enums.ResourceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pig {

    @ApiModelProperty(value = "猪只来源")
    private ResourceType source;
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty("猪只耳号")
    private String earNumber;
    @ApiModelProperty("个体号")
    private String individualNumber;
    @ApiModelProperty("猪只性别,1公猪，2母猪")
    private Integer sex;
    @ApiModelProperty(value = "出生日期")
    private LocalDate bornDate;
    @ApiModelProperty(value = "胎次")
    private int parity;
    @ApiModelProperty(value = "日龄")
    private Integer ageOfDay;
    @ApiModelProperty("当前胎次的配种日期")
    private String breedDate;
    @ApiModelProperty("当前胎次阶段")
    private int stage;
    @ApiModelProperty("当前胎次的分娩日期")
    private String laborDate;
    @ApiModelProperty("当前胎次的分娩可饲养仔猪数")
    private Integer piggy;
    @ApiModelProperty("背膘")
    private int backFat;
    @ApiModelProperty("背膘阶段")
    private Integer backFatStage;
    @ApiModelProperty("背膘日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date backFatDate;
    @ApiModelProperty(value = "品种ID")
    private Long varietyId;
    @ApiModelProperty(value = "品种")
    private String variety;
    @ApiModelProperty(value = "品系ID")
    private Long strainId;
    @ApiModelProperty(value = "品系")
    private String strain;
    @ApiModelProperty(value = "状态ID")
    private Long statusId;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "猪场ID")
    private Long farmId;
    @ApiModelProperty(value = "猪场名称")
    private String farmName;
    @ApiModelProperty(value = "栋舍ID")
    private Long houseId;
    @ApiModelProperty(value = "栋舍名称")
    private String houseName;
    @ApiModelProperty(value = "栏位ID")
    private Long colId;
    @ApiModelProperty(value = "栏位编号")
    private String colCode;
    @ApiModelProperty(value = "猪只类型ID")
    private Long pigTypeId;
    @ApiModelProperty(value = "猪只类型名称")
    private String pigTypeName;

    public static Pig wrap(PigBreeding breeding, PigMating mating, PigLabor labor) {
        Pig.PigBuilder builder = Pig.builder();
        builder.source(ResourceType.YHY)
                .id(breeding.getId())
                .farmId(breeding.getPigFarmId())
                .earNumber(breeding.getEarNumber())
                .sex(breeding.getType())
                .parity(ObjectUtil.isNull(breeding.getParity())?0:breeding.getParity())
                .backFat(ObjectUtil.isNull(breeding.getBackFat())?0:breeding.getBackFat());
        if (ObjectUtil.isNotEmpty(mating)) {
            builder.breedDate(DateUtil.formatDate(mating.getMatingDate()));
            builder.stage((int) DateUtil.between(mating.getMatingDate(), new Date(), DateUnit.DAY));
        }
        if (ObjectUtil.isNotEmpty(labor)) {
            builder.laborDate(DateUtil.formatDate(labor.getLaborDate())).piggy(labor.getFeedingNumber());
        }
        return builder.build();
    }

}
