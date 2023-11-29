package com.zmu.cloud.admin.mqtt;

import com.zmu.cloud.commons.enums.DataType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * topic事件
 * @author yh
 * @date 2019/5/920:16
 */
@Getter
public class MqttEvent extends ApplicationEvent {

    /**
     *
     */
    private String topic;
    private String qos;
    private DataType dataType;
    /**
     * 发送的消息
     */
    private byte[] byteMessage;

    public MqttEvent(Object source, String topic, byte[] byteMessage, String qos) {
        super(source);
        this.qos = qos;
        this.topic = topic;
        this.dataType = DataType.ByteArray;
        this.byteMessage = byteMessage;
    }
}
