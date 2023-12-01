package com.zmu.cloud.commons.dto.admin;

import com.zmu.cloud.commons.dto.commons.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@ApiModel
public class SysOperationLogQuery extends Page {

    @ApiModelProperty("操作名称")
    private String name;
    @ApiModelProperty("操作人")
    private String operator;
    @ApiModelProperty("ip")
    private String ip;
    @ApiModelProperty("操作地址")
    private String location;
    @ApiModelProperty("状态：0 异常，1 成功")
    private Integer status;
    @ApiModelProperty("开始时间，格式：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @ApiModelProperty("结束时间，格式：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
