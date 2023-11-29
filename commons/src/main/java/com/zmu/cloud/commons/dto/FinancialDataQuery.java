package com.zmu.cloud.commons.dto;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author lqp0817@gmail.com
 * @date 2022/5/2 20:29
 **/
@Data
@ApiModel
public class FinancialDataQuery extends BaseQuery {

    @ApiModelProperty(value = "关联财务数据类型")
    private Integer dataTypeId;
    @ApiModelProperty(value = "收支类型1,收入，2支出")
    private Integer income;
    @ApiModelProperty(value = "状态，1已导出，0未导出")
    private Integer status;
    @ApiModelProperty("开始时间，格式：yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @ApiModelProperty("结束时间，格式：yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public Date getStartDate() {
        if (startDate == null) {
            return null;
        }
        return DateUtil.beginOfDay(startDate);
    }

    public Date getEndDate() {
        if (endDate == null) {
            return null;
        }
        return DateUtil.endOfDay(endDate);
    }
}
