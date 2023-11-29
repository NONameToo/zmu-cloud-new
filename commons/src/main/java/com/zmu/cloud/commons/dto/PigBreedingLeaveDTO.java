package com.zmu.cloud.commons.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@ApiModel("离场")
public class PigBreedingLeaveDTO {
    @ApiModelProperty(value = "离场猪Id", required = true)
    @NotNull(message = "离场种猪不能为空")
    private List<Long> list;

    @ApiModelProperty(value = "离场时间", required = true)
    @NotNull(message = "离场时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaveTime;
    /**
     * 离场类型1.死淘，2转出
     */
    @ApiModelProperty(value = "离场类型1.死亡，2.淘汰,3.其他", required = true)
    @NotNull(message = "离场类型不能为空")
    private Integer type;

    /**
     * 离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它
     */
    @ApiModelProperty(value = "离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它")
    private Integer leavingReason;

    /**
     * 重量kg
     */
    @ApiModelProperty(value = "重量kg")
    private BigDecimal weight;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private BigDecimal price;

    /**
     * 头单价
     */
    @ApiModelProperty(value = "头单价")
    private BigDecimal unitPrice;
    @ApiModelProperty(value = "操作人id",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;
    @ApiModelProperty("备注")
    private String remark;
}
