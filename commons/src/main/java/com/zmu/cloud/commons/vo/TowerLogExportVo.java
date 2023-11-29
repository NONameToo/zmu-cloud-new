package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zhaojian
 * @create 2023/10/19 14:01
 * @Description 料塔导出实体类
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("料塔导出实体类")
public class TowerLogExportVo {

    @ApiModelProperty("料塔id")
    @ExcelIgnore
    private Long towerId;

    @ApiModelProperty("logId")
    @ExcelIgnore
    private Long logId;

    @ApiModelProperty("料塔名称")
    @ExcelProperty("料塔名称")
    private String towerName;

    @ApiModelProperty("空塔体积")
    @ExcelProperty("空塔体积")
    private Long towerVolume;

    @ApiModelProperty("设备编号")
    @ExcelProperty("设备编号")
    private String deviceNo;

    @ApiModelProperty("标准密度")
    @ExcelProperty("标准密度")
    private Long density;

    @ApiModelProperty("输入密度")
    @ExcelProperty("输入密度")
    private Long inputDensity;

    @ApiModelProperty("测量开始时间")
    @ExcelProperty("测量开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("测量结束时间")
    @ExcelProperty("测量结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty("启动方式")
    @ExcelProperty("启动方式")
    private String startMode;

    @ApiModelProperty("空腔体积")
    @ExcelProperty("空腔体积")
    private Long calVolume;

    @ApiModelProperty("饲料体积")
    @ExcelProperty("饲料体积")
    private Long calFeedVolume;

    @ApiModelProperty("测算密度")
    @ExcelProperty("测算密度")
    private Long calDensity;

    @ApiModelProperty("测算重量")
    @ExcelProperty("测算重量")
    private Long calWeightPrediction;

    @ApiModelProperty("重量")
    @ExcelProperty("重量")
    private Long calWeight;

    @ApiModelProperty("备注")
    @ExcelProperty("备注")
    private String remark;


}
