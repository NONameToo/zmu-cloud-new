package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.enums.CheckMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BackFatScanVo {

    private Long taskId;
    private Long detailId;
    private Long columnId;
    private Long pigId;
    private String columnCode;
    private String position;
    private String earNumber;
    private Integer backFat;
    private Integer backFatStage;
    private Boolean skip;
    private CheckMode mode;
    private String status;

}
