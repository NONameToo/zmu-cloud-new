package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * @author YH
 */
@Data
@ApiModel("妊娠事件")
public class EventPregnancyVO extends EventDetailVO {
    /**
     * 妊娠时间
     */
    @ApiModelProperty(value = "妊娠时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date pregnancyDate;

    /**
     * 妊娠结果1.妊娠，2流产，3返情，4阴性
     */
    @ApiModelProperty(value = "妊娠结果1.妊娠，2流产，3返情，4阴性")
    private Integer pregnancyResult;

    /**
     * 操作员
     */
    @ApiModelProperty(value = "操作员")
    private Long operatorId;
    @ApiModelProperty("类型：1.配种，2妊娠，3分娩，4断奶")
    private Integer eventType = 2;
}
