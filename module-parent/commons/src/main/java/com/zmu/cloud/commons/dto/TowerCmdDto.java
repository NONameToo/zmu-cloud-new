package com.zmu.cloud.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TowerCmdDto implements Serializable {

    private Long towerId;
    private Long farmId;
    private String deviceNo;
    private int enable;
    private String taskId;

}
