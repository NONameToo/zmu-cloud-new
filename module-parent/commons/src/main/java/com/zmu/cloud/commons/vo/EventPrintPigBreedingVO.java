package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EventPrintPigBreedingVO {
    @ApiModelProperty("胎次")
    private Integer parity;
    @ApiModelProperty("配种日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date matingDate;
    @ApiModelProperty("妊检日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date pregnancyDate;
    @ApiModelProperty("妊娠结果1.妊娠，2流产，3返情，4阴性")
    private Integer pregnancyResult;
    @ApiModelProperty("分娩日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date laborDate;
    @ApiModelProperty("分娩活仔数")
    private Integer liveNumber;
    @ApiModelProperty("断奶日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date weanedDate;
    @ApiModelProperty("断奶数量")
    private Integer weanedNumber;
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;
}
