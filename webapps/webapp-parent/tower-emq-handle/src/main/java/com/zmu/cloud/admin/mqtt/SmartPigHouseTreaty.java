package com.zmu.cloud.admin.mqtt;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

/**
 * @author YH
 */
@Data
public class SmartPigHouseTreaty {

    private String head;
    private String totalLength;
    private String version;
    private String operationType;
    private String valueLength;
    private String value;
    private String crc;
    private Boolean correct;
    private Boolean taskId;

    @Override
    public String toString() {
        return new StrBuilder()
                .append(ObjectUtil.isEmpty(head)?"":head)
                .append(ObjectUtil.isEmpty(totalLength)?"":totalLength)
                .append(ObjectUtil.isEmpty(version)?"":version)
                .append(ObjectUtil.isEmpty(operationType)?"":operationType)
                .append(ObjectUtil.isEmpty(valueLength)?"":valueLength)
                .append(ObjectUtil.isEmpty(value)?"":value)
                .append(ObjectUtil.isEmpty(crc)?"":crc)
                .toString();
    }
}
