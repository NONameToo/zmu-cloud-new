package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zhaojian
 * @create 2023/10/31 10:12
 * @Description 售后表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-SaleAfter")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleAfter {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @ApiModelProperty(value = "售后类型：1更换设备  2更换电控箱  3更换设备和电控箱")
    private Integer type;
    @ApiModelProperty(value = "处理状态：0待处理 1已处理")
    private Integer status;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "创建人id", hidden = true)
    private Long createBy;
    @ApiModelProperty(value = "更新人id", hidden = true)
    private Long updateBy;
    @ApiModelProperty(value = "创建时间", hidden = true)
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    private LocalDateTime updateTime;
}
