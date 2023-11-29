package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.enums.Enable;
import com.zmu.cloud.commons.enums.FirmwareCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

/**
 * @author YH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("固件升级配置")
public class FirmwareUpgradeConfigDto {

    @NotNull
    @ApiModelProperty(value = "固件版本Id", required = true)
    private Long versionId;
    @NotNull
    @ApiModelProperty(value = "固件类别", required = true)
    private FirmwareCategory category;
    @NotBlank
    @ApiModelProperty(value = "升级时间,格式：yyyy-MM-dd HH:mm:ss", required = true)
    private String upgradeTime;
    @Range(min = 1, max = 5000)
    @ApiModelProperty(value = "一次升级的数量上限")
    private int upgradeLimit;
    @Min(512)
    @Max(1024)
    @ApiModelProperty(value = "发送的每帧数据包长度：字节")
    private int frameLength;
    @NotNull
    @ApiModelProperty(value = "是否启用配置")
    private Integer enable;
}
