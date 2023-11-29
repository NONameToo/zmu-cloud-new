package com.zmu.cloud.admin.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author YH
 */
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttServer {
    void sendToMqtt(@Header(MqttHeaders.TOPIC)String topic, @Header(MqttHeaders.QOS) int qos, String data);
    void sendToMqtt(@Header(MqttHeaders.TOPIC)String topic, @Header(MqttHeaders.QOS) int qos, byte[] data);
}
