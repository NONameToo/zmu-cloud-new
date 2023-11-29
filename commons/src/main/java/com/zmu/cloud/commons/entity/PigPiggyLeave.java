package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 仔猪离场
 *
 * @author shining
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigPiggyLeave")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_piggy_leave")
@SuperBuilder
public class PigPiggyLeave extends BaseEntity {
    /**
     * 仔猪id
     */
    @TableField(value = "pig_piggy_id")
    @ApiModelProperty(value = "仔猪id", required = true)
    @NotNull(message = "仔猪Id不能为空")
    private Long pigPiggyId;

    @TableField(value = "leave_time")
    @ApiModelProperty(value = "离场时间", required = true)
    @NotNull(message = "离场时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaveTime;

    /**
     * 离场数量
     */
    @TableField(value = "`number`")
    @ApiModelProperty(value = "离场数量", required = true)
    @NotNull(message = "离场数量不能为空")
    private Integer number;

    /**
     * 离场类型1.死淘，2转出
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "离场类型1.死淘，2转出", required = true)
    @NotNull(message = "离场类型不能为空")
    private Integer type;

    /**
     * 离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它
     */
    @TableField(value = "leaving_reason")
    @ApiModelProperty(value = "离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它")
    private Integer leavingReason;

    /**
     * 重量
     */
    @TableField(value = "weight")
    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    /**
     * 价格
     */
    @TableField(value = "price")
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    /**
     * 操作人
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作人",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;
}