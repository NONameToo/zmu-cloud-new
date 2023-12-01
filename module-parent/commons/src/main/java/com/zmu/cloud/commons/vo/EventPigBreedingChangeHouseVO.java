package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shining
 */
@Data
@ApiModel("转舍记录")
public class EventPigBreedingChangeHouseVO {
    @ApiModelProperty("耳号")
    @ExcelProperty("耳号")
    private String earNumber;

    @ApiModelProperty("转舍时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("转舍时间")
    private Date changeHouseTime;

    @ApiModelProperty("转入猪舍")
    @ExcelProperty("转入猪舍")
    private String pigHouseIn;

    @ApiModelProperty("转入排位")
    @ExcelProperty("转入排位")
    private String pigRowsIn;

    @ApiModelProperty("转入栏位")
    @ExcelProperty("转入栏位")
    private String pigColumnsIn;

    @ApiModelProperty("转入栏位位置")
    @ExcelProperty("转入栏位位置")
    private String positionIn;

    @ApiModelProperty("转出猪舍")
    @ExcelProperty("转出猪舍")
    private String pigHouseOut;

    @ApiModelProperty("转出排位")
    @ExcelProperty("转出排位")
    private String pigRowsOut;

    @ApiModelProperty("转出栏位")
    @ExcelProperty("转出栏位")
    private String pigColumnsOut;

    @ApiModelProperty("转出栏位位置")
    @ExcelProperty("转出栏位位置")
    private String positionOut;

    @ApiModelProperty("备注")
    @ExcelProperty("备注")
    private String remark;

    @ApiModelProperty("操作人")
    @ExcelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    private String createTime;


}
