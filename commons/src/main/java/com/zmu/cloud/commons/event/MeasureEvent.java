package com.zmu.cloud.commons.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author YH
 */
public class MeasureEvent extends ApplicationEvent {

    private final Long logId;
    private final String deviceNo;

    public MeasureEvent(Object source, Long logId, String deviceNo) {
        super(source);
        this.logId = logId;
        this.deviceNo = deviceNo;
    }

    public Long getLogId() {
        return logId;
    }

    public String getDeviceNo() {
        return deviceNo;
    }
}
