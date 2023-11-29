package com.zmu.cloud.commons.dto.commons.oss;

import java.io.Serializable;
import lombok.Data;


@Data
public class PolicyResponse implements Serializable {
    private String policy;
    private String expire;
    private String signature;
}
