package com.zmu.cloud.commons.enums;

import lombok.Getter;

@Getter
public enum MqttMessageType {
    Send("RX"), Receive("TX");

    String code;

    MqttMessageType(String code) {
        this.code = code;
    }
}
