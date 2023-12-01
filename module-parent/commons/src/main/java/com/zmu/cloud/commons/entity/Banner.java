package com.zmu.cloud.commons.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ApiModel(value = "Banner")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Banner extends BaseEntity {

  /**
   * 排除父类猪场id
   */
  @JsonIgnore
  @ExcelIgnore
  private transient Long pigFarmId;

  /**
   * 图片地址
   */
  @ApiModelProperty(value = "图片地址", required = true)
  @NotBlank(message = "图片地址不可为空")
  @ExcelProperty("图片地址")
  private String imgUrl;

  /**
   * 跳转地址
   */
  @ApiModelProperty(value = "跳转地址")
  @ExcelProperty("跳转地址")
  private String jumpUrl;

  /**
   * 位置：1 首页
   */
  @ApiModelProperty(value = "位置：1 首页", required = true)
  @NotNull(message = "位置不可为空")
  @ExcelProperty("位置：1 首页")
  private Integer position;

  /**
   * 排序：越小越靠前
   */
  @ApiModelProperty(value = "排序：越小越靠前", required = true)
  @NotNull(message = "排序不可为空")
  @ExcelProperty("排序：越小越靠前")
  private Integer sort;

  /**
   * 0 隐藏，1 显示
   */
  @ApiModelProperty(value = "0 隐藏，1 显示")
  @NotNull(message = "显示状态不可为空")
  @ExcelProperty("显示状态：0 隐藏，1 显示")
  private Integer status;
}