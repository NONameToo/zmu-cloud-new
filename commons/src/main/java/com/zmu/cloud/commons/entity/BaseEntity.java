package com.zmu.cloud.commons.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/17 17:24
 **/
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ApiModel
public class BaseEntity {

    @ApiModelProperty("主键id")
    @ExcelProperty("主键id")
    private Long id;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("猪场id")
    private Long pigFarmId;

    @ApiModelProperty(value = "删除标识：0-未删除，1-已删除", hidden = true)
    @TableLogic
    @JsonIgnore
    private boolean del;

    @ApiModelProperty("备注")
    @ExcelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "创建人id", hidden = true)
    @JsonIgnore
    @ExcelIgnore
    private Long createBy;

    @ApiModelProperty(value = "更新人id", hidden = true)
    @JsonIgnore
    @ExcelIgnore
    private Long updateBy;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @ExcelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", hidden = true)
    @ExcelProperty("更新时间")
    private Date updateTime;
}
