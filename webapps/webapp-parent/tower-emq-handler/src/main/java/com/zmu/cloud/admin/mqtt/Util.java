package com.zmu.cloud.admin.mqtt;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.MD5;
import com.zmu.cloud.commons.entity.MqttLog;
import com.zmu.cloud.commons.enums.MqttMessageType;
import com.zmu.cloud.commons.mapper.MqttLogMapper;
import com.zmu.cloud.commons.utils.CRC16Util;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class Util {
    public static Long toClientId(String topic) {
        try {
            return Long.parseLong(topic.split("/")[2]);
        } catch (Exception e) {
            log.info("主题：".concat(topic).concat("解析错误！"));
        }
        return null;
    }

    public static String crc(SmartPigHouseTreaty treaty) {
        return CRC16Util.bytesToHex(CRC16Util.getCrc16(CRC16Util.toByteArray(treaty.toString())));
    }

    public static String treatyId(Long clientId, String operationType, String treatyValue) {
        return MD5.create().digestHex(new StringBuilder().append(clientId).append(operationType).append(treatyValue).toString());
    }

    public static SmartPigHouseTreaty resolve(String hexString) {
        SmartPigHouseTreaty treaty = new SmartPigHouseTreaty();
        treaty.setHead(hexString.substring(0, 8));
        hexString = hexString.substring(8);
        treaty.setTotalLength(hexString.substring(0, 4));
        hexString = hexString.substring(4);
        treaty.setVersion(hexString.substring(0, 4));
        hexString = hexString.substring(4);
        treaty.setOperationType(hexString.substring(0, 4));
        hexString = hexString.substring(4);
        treaty.setValueLength(hexString.substring(0, 4));
        hexString = hexString.substring(4);
        treaty.setValue(hexString.substring(0, hexString.length()-4));
        String crc = hexString.substring(hexString.length()-4);
        if (Util.crc(treaty).equals(crc)) {
            treaty.setCorrect(Boolean.TRUE);
        } else {
            treaty.setCorrect(Boolean.FALSE);
        }
        treaty.setCrc(crc);
        return treaty;
    }

    public static MqttLog saveMqttLog(MqttLogMapper mapper, String topic, String message, MqttMessageType type, SmartPigHouseTreaty treaty) {
        return saveMqttLog(mapper, topic, message, type, true, treaty);
    }

    public static MqttLog saveMqttLog(MqttLogMapper mapper, String topic, String message, MqttMessageType type, Boolean correct, SmartPigHouseTreaty treaty) {
        MqttLog log = new MqttLog();
        log.setClientid(Util.toClientId(topic));
        log.setMessage(message);
        log.setTopic(topic);
        log.setCreateTime(LocalDateTime.now());
        log.setType(type.name());
        log.setCorrect(correct.toString());
        log.setHead(treaty.getHead());
        log.setTotalLength(treaty.getTotalLength());
        log.setVersion(treaty.getVersion());
        log.setOperationType(treaty.getOperationType());
        log.setValueLength(treaty.getValueLength());
        log.setValue(treaty.getValue());
        log.setCrc(treaty.getCrc());
        mapper.insert(log);
        return log;
    }

    public static String convertWeight(int weight) {
        if (weight < 16) {
            return "000".concat(Integer.toHexString(weight));
        } else if (weight < 256) {
            return "00".concat(Integer.toHexString(weight));
        } else if (weight < 4096) {
            return "0".concat(Integer.toHexString(weight));
        } else if (weight < 65536){
            return "".concat(Integer.toHexString(weight));
        } else {
            return "0000";
        }
    }

    public static double roundPlatformVolumeFormula(double R, double r, double h) {
        return Math.PI*h*(R*R + r*r +R*r)/3;
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, 6, 5, 18, 47, 0);
        System.out.println(DateUtil.between(new Date(), calendar.getTime(), DateUnit.SECOND));
    }
}
