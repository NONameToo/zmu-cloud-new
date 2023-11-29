package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ApiModel(value="feedback")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feedback")
public class Feedback  extends BaseEntity {

    @TableField(value = "content")
    @ApiModelProperty(value="")
    private String content;

    @TableField(value = "img")
    @ApiModelProperty(value="")
    private String img;
}