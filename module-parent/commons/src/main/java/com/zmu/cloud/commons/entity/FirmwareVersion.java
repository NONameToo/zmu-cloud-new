package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

/**
    * 固件版本管理
    */
@ApiModel(value="com-zmu-cloud-commons-entity-FirmwareVersion")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "firmware_version")
public class FirmwareVersion {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 固件类型
     */
    @TableField(value = "category")
    @ApiModelProperty(value="固件类型")
    private String category;

    /**
     * 固件版本号
     */
    @TableField(value = "version")
    @ApiModelProperty(value="固件版本号")
    private String version;

    /**
     * 文件名称
     */
    @TableField(value = "file_name")
    @ApiModelProperty(value="文件名称")
    private String fileName;

    /**
     * 存储目录
     */
    @TableField(value = "save_path")
    @ApiModelProperty(value="存储目录")
    private String savePath;

    /**
     * 0-未删除，1-已删除
     */
    @TableField(value = "del")
    @ApiModelProperty(value="0-未删除，1-已删除")
    private String del;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value="备注")
    private String remark;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    @ApiModelProperty(value="创建人")
    private Long createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by")
    @ApiModelProperty(value="更新人")
    private Long updateBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="更新时间")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String createUser;
}