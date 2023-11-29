package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("待分娩任务VO")
public class PigLaborTaskListVO {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("种猪id")
    private String pigBreedingId;
    @ApiModelProperty("耳号")
    private String earNumber;
    @ApiModelProperty("配种日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date matingDate;
    @ApiModelProperty("妊娠天数")
    private Integer pregnancyDay;
    @ApiModelProperty("预产期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date preDate;
    @ApiModelProperty("所属位置猪舍id")
    private Long pigHouseId;
    @ApiModelProperty("所属位置猪舍")
    private String pigHouse;
    @ApiModelProperty("所属位置猪舍类型")
    private Integer pigHouseType;
    @ApiModelProperty("所属位置猪舍排位")
    private String pigRows;
    @ApiModelProperty("所属位置猪舍栏位")
    private String pigColumns;
    @ApiModelProperty("位置")
    private String position;
    @ApiModelProperty("超期天数")
    private Integer overdueDay;
    @ApiModelProperty("种猪状态默认：1.后备，2配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳 8.断奶")
    private String pigStatus;

}
