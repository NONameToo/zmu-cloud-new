package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 猪舍栏
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigHouseColumns")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_house_columns")
public class PigHouseColumns {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 猪场id
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "猪场id")
    private Long pigFarmId;

    /**
     * 栋舍ID
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "栋舍ID")
    private Long pigHouseId;

    /**
     * 猪舍排id
     */
    @TableField(value = "pig_house_rows_id")
    @ApiModelProperty(value = "猪舍排id")
    private Long pigHouseRowsId;

    /**
     * 名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 编号：比如第5栏：05
     */
    @TableField(value = "code")
    @ApiModelProperty(value = "编号：比如第5栏：05")
    private String code;

    /**
     * 栏位编号
     */
    @TableField(value = "no")
    @ApiModelProperty(value = "栏位编号")
    private Integer no;

    /**
     * 栏位位置
     */
    @TableField(value = "position")
    @ApiModelProperty(value = "栏位位置")
    private String position;

    /**
     * 主机ID
     */
    @TableField(value = "client_id")
    @ApiModelProperty(value = "主机ID")
    private Long clientId;

    /**
     * 饲喂器编号
     */
    @TableField(value = "feeder_code")
    @ApiModelProperty(value = "饲喂器编号")
    private Integer feederCode;

    /**
     * 肥猪饲喂器是否启用，默认关
     */
    @TableField(value = "feeder_enable")
    @ApiModelProperty(value = "肥猪饲喂器是否启用，默认关")
    private Integer feederEnable;

    /**
     * 栏位饲喂量（克）
     */
    @TableField(value = "feeding_amount")
    @ApiModelProperty(value = "栏位饲喂量（克）")
    private Integer feedingAmount;

    /**
     * 猪只与栏位关联，主要用于巨星猪只，云慧养端关系维护在表pig_breeding中
     */
    @TableField(value = "pig_id", updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "猪只与栏位关联，主要用于巨星猪只，云慧养端关系维护在表pig_breeding中")
    private Long pigId;

    @TableField(exist = false)
    @ApiModelProperty(value = "栏位中的猪只，小栏：0只或1只，大栏：0只或大于等于1只")
    private List<PigBreeding> pigs = new ArrayList<>();

    /**
     * AI计数：当前猪只数量
     */
    @TableField(value = "curr_quantity")
    @ApiModelProperty(value = "AI计数：当前猪只数量")
    private Integer currQuantity;

    /**
     * AI计数：栏位当前猪只数量时间
     */
    @TableField(value = "curr_date")
    @ApiModelProperty(value = "AI计数：栏位当前猪只数量时间")
    private LocalDateTime currDate;

    /**
     * 0未删除，1已删除
     */
    @TableField(value = "del")
    @ApiModelProperty(value = "0未删除，1已删除")
    private String del;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    @ApiModelProperty(value = "创建人")
    private Long createBy;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    @ApiModelProperty(value = "修改人")
    private Long updateBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}