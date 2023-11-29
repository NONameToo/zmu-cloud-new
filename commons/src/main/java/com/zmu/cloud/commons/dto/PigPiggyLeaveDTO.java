package com.zmu.cloud.commons.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 仔猪离场
 *
 * @author shining
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigPiggyLeave")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PigPiggyLeaveDTO {

    @ApiModelProperty(value = "ID", required = true)
    @NotNull(message = "数据ID")
    private Long id;

    @ApiModelProperty(value = "离场时间", required = true)
    @NotNull(message = "离场时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaveTime;

    @ApiModelProperty(value = "种猪id", required = true)
    private Long pigBreedingId;

    /**
     * 离场数量
     */
    @ApiModelProperty(value = "离场数量", required = true)
    @NotNull(message = "离场数量不能为空")
    private Integer number;

    /**
     * 离场类型1.死淘，2转出
     */
    @ApiModelProperty(value = "离场类型1.死淘，2转出", required = true)
    @NotNull(message = "离场类型不能为空")
    private Integer type;

    /**
     * 离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它
     */
    @ApiModelProperty(value = "离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它")
    private Integer leavingReason;

    /**
     * 重量
     */
    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人", required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;
}