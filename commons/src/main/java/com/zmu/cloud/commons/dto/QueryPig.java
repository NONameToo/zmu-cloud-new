package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author shining
 */
@Data
public class QueryPig extends BaseQuery {
    @ApiModelProperty(value = "耳号")
    private String earNumber;
    @ApiModelProperty(value = "猪只类型1公猪，2母猪")
    private Integer type;
    @ApiModelProperty(value = "猪舍Id")
    private Long pigHouseId;
    @ApiModelProperty(value = "在场状态，1在场，2离场")
    private Integer presenceStatus;
    @ApiModelProperty(value = "种猪状态集合：1.后备，2.配种，3.空怀，4.返情，5.流产，6.妊娠，7.哺乳，8.断奶")
    private String pigStatuses;
    @ApiModelProperty(hidden = true)
    private List<Integer> statuses;
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;
    @ApiModelProperty("操作人id")
    private Long operatorId;
    @ApiModelProperty(value = "妊娠结果1.妊娠，2流产，3返情，4阴性")
    private Integer pregnancyResult;
    @ApiModelProperty(value = "猪群id")
    private Long pigGroupId;
    @ApiModelProperty(value = "转出猪群id")
    private Long pigGroupOutId;
    @ApiModelProperty(value = "转入猪群id")
    private Long pigGroupInId;
    @ApiModelProperty(value = "进场类型1.自繁，2购买，3转入")
    private Integer approachType;

    @ApiModelProperty("遗忘耳号猪只查询参数-时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String loseTime;


    @ApiModelProperty("日龄")
    private Long dayAge;
    @ApiModelProperty("猪群编号")
    private String pigGroupName;

}
