package com.zmu.cloud.admin.mqtt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.dto.TowerTreaty;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.FeedTowerLog;
import com.zmu.cloud.commons.entity.FeedTowerMsg;
import com.zmu.cloud.commons.enums.DeviceStatusEnum;
import com.zmu.cloud.commons.enums.Enable;
import com.zmu.cloud.commons.enums.MqttMessageType;
import com.zmu.cloud.commons.enums.TowerStatus;
import com.zmu.cloud.commons.mapper.FeedTowerLogMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.redis.delay.MeasureStartDelayQueue;
import com.zmu.cloud.commons.service.TowerLogService;
import com.zmu.cloud.commons.service.TowerMsgService;
import com.zmu.cloud.commons.service.TowerService;
import com.zmu.cloud.commons.utils.CRC16Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.zmu.cloud.commons.service.TowerService.TOPIC_REFRESH;
import static com.zmu.cloud.commons.service.TowerService.TOPIC_RX;

/**
 * 料塔V1版本协议处理
 * @author YH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TowerMessageHandleForV1Service {

    final TowerService towerService;
    final TowerLogService towerLogService;
    final TowerMsgService towerMsgService;
    final FeedTowerLogMapper towerLogMapper;
    final RedissonClient redissonClient;
    final MqttServer mqttServer;
    final MeasureStartDelayQueue measureStartDelayQueue;

    @Value("${spring.mqtt.enable}")
    private Boolean enable;
    static final int qos = 1;

    /**
     * 数据处理
     * @param topic
     * @param resp
     */
    public void handle(String topic, String resp) {
        TowerTreaty treaty = resolve(resp);
        String taskNo = treaty.getTaskNo();
        String deviceNo = topic.split("/")[2];
        if (treaty.isCorrect()) {
            switch (treaty.getCmd()) {
                case "01" : //测量结束：正常、异常（程序异常、手动停止）
                    Optional<FeedTower> towerOpt = towerService.find(deviceNo);
                    FeedTower tower = towerOpt.orElse(new FeedTower());
                    reply01Cmd(topic.replace("TX", "RX"), treaty);
                    log.info("【{}:{}】检测结束状态：{}", deviceNo, taskNo, treaty.getContent());
                    towerMsgService.insert(towerMsgService.receiveMsg(topic, deviceNo, resp, treaty));
                    //异常停止
                    if ("01".equals(treaty.getContent())) {
                        Optional<FeedTowerLog> opt = towerLogService.findByTaskNo(deviceNo, taskNo);
                        opt.ifPresent(log -> {
                            log.setStatus(TowerStatus.cancel.name());
                            log.setCompletedTime(LocalDateTime.now());
                            log.setRemark("检测任务异常终止");
                            towerLogService.updateById(log);
                        });
                    } else {
//                        towerOpt.ifPresent(t -> towerCalculation.calculationVolume(t.getId(), taskNo));
                    }
                    RBucket<DeviceStatus> status = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo));
                    if (status.isExists()) {
                        status.get().setDeviceStatus(DeviceStatusEnum.Standby);
                    }

                    noticeRefresh(deviceNo);
                    break;
                case "02" ://接收测量数据
                    //为了保存异常情况下的测量数据，使用两个key缓存数据
                    FeedTowerMsg msg = towerMsgService.receiveMsg(topic, deviceNo, resp, treaty);
                    RList<FeedTowerMsg> data = redissonClient.getList(CacheKey.Admin.tower_cache_data.key + deviceNo + ":" + msg.getDataTime());
                    data.add(msg);
                    break;
                case "03" :// 回复心跳及缓存状态
                    replyHeartbeat(topic.replace("STATUS", "RX"));
                    deviceStatus(deviceNo, treaty);
                    break;
                case "07" : //开启一次测量任务
                    towerMsgService.insert(towerMsgService.receiveMsg(topic, deviceNo, resp, treaty));
                    Optional<FeedTowerLog> opt = towerLogService.findByTaskNo(deviceNo, taskNo);
                    if (opt.isPresent()) {
                        FeedTowerLog log = opt.get();
                        if (TowerStatus.starting.name().equals(log.getStatus())) {
                            log.setStatus(TowerStatus.running.name());
                            towerLogService.updateById(log);
                            RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo));
                            if (bucket.isExists()) {
                                bucket.get().setDeviceStatus(DeviceStatusEnum.Measure);
                            }
                        }
                    } else {
                        log.error("设备启动日志不存在！DeviceNo:{}, TaskId:{}", deviceNo, taskNo);
                    }
                    noticeRefresh(deviceNo);
                    break;
            }
        }
    }

    /**
     * 通知前端刷新
     * @param deviceNo
     */
    void noticeRefresh(String deviceNo) {
        send(String.format(TOPIC_REFRESH, deviceNo), qos, "");
    }

    /**
     * 记录设备状态
     * @param deviceNo
     * @param treaty
     */
    void deviceStatus(String deviceNo, TowerTreaty treaty) {
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo));
        DeviceStatus status;
        if (bucket.isExists()) {
            status = bucket.get();
        } else {
            status = new DeviceStatus();
        }
        status.setVersion("V1");
//        status.setNetworkStatus("中");
        String content = treaty.getContent();
        //红外，1表示检测到信号，0表示未检测到信号
        //温度
        status.setTemperature(String.valueOf(Integer.parseInt(content.substring(0, 2), 16)));
        content = content.substring(2);
        //红外1
        content = content.substring(2);
        //红外2
        content = content.substring(2);
        //湿度
        status.setHumidity(String.valueOf(Integer.parseInt(content.substring(0, 2), 16)));
        bucket.set(status);
    }

    /**
     * 协议解析
     * @param resp 01d3
     * @return
     */
    private TowerTreaty resolve(String resp) {
        String hex = resp;
        TowerTreaty treaty = new TowerTreaty();
        try {
            treaty.setCrc(hex.substring(hex.length() - 4));
            treaty.setEnd(hex.substring(hex.length() - 6, hex.length() - 4));
            treaty.setCorrect(CRC16Util.towerCrc16(hex.substring(0, hex.length() - 4)).equals(treaty.getCrc()));
            treaty.setHead(hex.substring(0, 2));
            hex = hex.substring(2);
            treaty.setLen(Integer.parseInt(hex.substring(0, 4), 16));
            hex = hex.substring(4);
            treaty.setVersion(hex.substring(0, 2));
            hex = hex.substring(2);
            treaty.setTaskNo(hex.substring(0, 14));
            hex = hex.substring(14);
            treaty.setCmd(hex.substring(0, 2));
            hex = hex.substring(2);
            treaty.setContentLen(Integer.parseInt(hex.substring(0, 4), 16));
            treaty.setAmount((treaty.getContentLen() - 1) / 4);
            hex = hex.substring(4);//参数长度
            treaty.setContent(hex.substring(0, hex.length() - 6));
            treaty.setSteeringAngle(Integer.parseInt(treaty.getContent().substring(0, 2), 16));
        } catch (Exception e) {
            log.info("解析料塔数据失败：{}", resp);
        }
        return treaty;
    }

    /**
     * 启动/停止设备测量余料
     * @param towerLog
     * @param taskNo
     */
    public void measure(FeedTowerLog towerLog, Enable enable, String taskNo) {
        String topic = String.format(TOPIC_RX, towerLog.getDeviceNo());
        TowerTreaty treaty = runOrStopDevice(enable.getVal(), taskNo);
        towerMsgService.insert(topic, towerLog.getDeviceNo(), treaty, MqttMessageType.Send);
        log.info(Enable.OFF.equals(enable)?"停止料塔{}检测命令...":"启动料塔{}检测...", towerLog.getDeviceNo());
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
    }

    /**
     * 启动、停止
     * cmd：05
     * @param enable 0 / 1
     * @return
     */
    public static TowerTreaty runOrStopDevice(int enable, String taskNo) {
        TowerTreaty treaty = new TowerTreaty();
        treaty.setHead("CC");
        treaty.setLen(16);
        treaty.setVersion("01");
        treaty.setTaskNo(taskNo);
        treaty.setCmd("05");
        treaty.setContentLen(1);
        treaty.setContent(CRC16Util.toHexString(enable));
        treaty.setEnd("BB");
        treaty.setCorrect(true);
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        return treaty;
    }

    /**
     * 回复01命令
     * @param topic
     * @param receiveTreaty
     */
    public void reply01Cmd(String topic, TowerTreaty receiveTreaty) {
        TowerTreaty treaty = new TowerTreaty();
        BeanUtil.copyProperties(receiveTreaty, treaty, "crc", "correct");
        treaty.setTaskNo(DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN));
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        byte[] cmd = CRC16Util.hexToByteArr(treaty.toString());
        send(topic, qos, cmd);
    }

    /**
     * 回复心跳
     * @param topic
     */
    public void replyHeartbeat(String topic) {
        TowerTreaty treaty = new TowerTreaty();
        treaty.setHead("CC");
        treaty.setLen(15);
        treaty.setVersion("01");
        treaty.setTaskNo(DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN));
        treaty.setCmd("04");
        treaty.setEnd("BB");
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        byte[] cmd = CRC16Util.hexToByteArr(treaty.toString());
        send(topic, qos, cmd);
    }

    /**
     * 强制重启设备
     * cmd：06
     * @param farmId
     * @param towerId
     * @param deviceNo
     */
    public void reboot(Long farmId, Long towerId, String deviceNo) {
        String topic = String.format(TOPIC_RX, deviceNo);
        TowerTreaty treaty = new TowerTreaty();
        treaty.setHead("CC");
        treaty.setLen(15);
        treaty.setVersion("01");
        treaty.setTaskNo(DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN));
        treaty.setCmd("06");
        treaty.setEnd("BB");
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
    }

    private void send(String topic, int qos, Object data) {
        if (enable) {
            if (data instanceof byte[]) {
                mqttServer.sendToMqtt(topic, qos, (byte[]) data);
            } else if (data instanceof String) {
                mqttServer.sendToMqtt(topic, qos, data.toString());
            }
        }
    }
}
