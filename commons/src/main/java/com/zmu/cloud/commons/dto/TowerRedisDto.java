package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.enums.Enable;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 料塔测量
 * @author YH
 */
@Data
@Builder
public class TowerRedisDto implements Serializable {

    private String deviceNo;
    private String taskNo;
    private Enable enable;
    private Long logId;
    private Integer modbusId;
    private String wifiAccount;
    private String wifiPwd;

}
