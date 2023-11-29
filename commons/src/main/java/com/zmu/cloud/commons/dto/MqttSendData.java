package com.zmu.cloud.commons.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
public class MqttSendData implements Serializable {

    private Long clientId;
    private List<BaseFeedingDTO> baseFeedings;

}
