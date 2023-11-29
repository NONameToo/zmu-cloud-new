package com.zmu.cloud.admin.mqtt;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import com.zmu.cloud.commons.entity.FeedTowerDevice;
import com.zmu.cloud.commons.entity.FeedTowerLog;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.mapper.DeviceAgingCheckMapper;
import com.zmu.cloud.commons.mapper.FeedTowerLogMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.redis.delay.DeviceUpgradeTimeoutDelayQueue;
import com.zmu.cloud.commons.redis.delay.MeasureDelayQueue;
import com.zmu.cloud.commons.redis.delay.MeasureStartDelayQueue;
import com.zmu.cloud.commons.service.TowerDeviceService;
import com.zmu.cloud.commons.service.TowerLogService;
import com.zmu.cloud.commons.service.TowerService;
import com.zmu.cloud.commons.utils.CRC16Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * @author YH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TowerMessageHandleService {

    final TowerDeviceService deviceService;
    final FeedTowerLogMapper towerLogMapper;
    final TowerLogService towerLogService;
    final RedissonClient redis;
    final MqttServer mqttServer;
    final ThreadPoolTaskExecutor taskExecutor;
    final TowerMessageHandleForV1Service v1Service;
    final TowerMessageHandleForV2Service v2Service;
    final MeasureDelayQueue measureDelayQueue;
    final MeasureStartDelayQueue measureStartDelayQueue;
    final DeviceUpgradeTimeoutDelayQueue deviceUpgradeTimeoutDelayQueue;
    final TowerMessageHandleForV2Service towerMessageHandleForV2Service;
    final TowerCalculationV2 towerCalculationV2;

    @Value("${spring.mqtt.enable}")
    private Boolean enable;

    @PostConstruct
    private void consumer() {
        taskExecutor.execute(() -> {
            while (true) {
                Long logId;
                try {
                    logId = measureStartDelayQueue.getBlockingQueue().take();
                    if (ObjectUtil.isNull(logId)) {
                        continue;
                    }
                    autoCancelMeasure(logId, TowerStatus.starting, "启动超时被取消");
                } catch (Exception e) {
                    log.error("启动超时判定线程出错: {}",e.getMessage());
                }
            }
        });
        taskExecutor.execute(() -> {
            while (true) {
                Long logId;
                try {
                    logId = measureDelayQueue.getBlockingQueue().take();
                    if (ObjectUtil.isNull(logId)) {
                        continue;
                    }
                    autoCancelMeasure(logId, TowerStatus.running, "采集数据超时被取消");
                    FeedTowerLog towerLog = towerLogMapper.selectById(logId);
                    v2Service.saveData(towerLog);
                } catch (Exception e) {
                    log.error("采集数据超时判定线程出错: {}",e.getMessage());
                }
            }
        });
        taskExecutor.execute(() -> {
            while (true) {
                String deviceNo;
                try {
                    deviceNo = deviceUpgradeTimeoutDelayQueue.getBlockingQueue().take();
                    if (ObjectUtil.isNotEmpty(deviceNo)) {
                        continue;
                    }
                    v2Service.updateFirmwareUpgradeInfo(deviceNo, UpgradeSchedule.timeout,
                            String.format("升级超时(%s)", DeviceUpgradeTimeoutDelayQueue.TIMEOUT));
                } catch (Exception e) {
                    log.error("升级超时判定线程出错: {}",e.getMessage());
                }
            }
        });
    }

    /**
     * 消息入口
     * @param mqttEvent
     */
    final void inputMessage(MqttEvent mqttEvent) {
        String topic = mqttEvent.getTopic();
        String resp = CRC16Util.byteArrToHex(mqttEvent.getByteMessage());
        //V1
        if (resp.toUpperCase().startsWith("CC")) {
            v1Service.handle(topic, resp);
        }
        //V2 AABB2C000201000000001C000000000000005A4D4B4A315F322E302E302E32303233303331345F64CCDD66D1
        else if (resp.toUpperCase().startsWith("AABB") && resp.toUpperCase().contains("CCDD")) {
            v2Service.handle(topic, resp);
        }
    }

    /**
     * 启动余料测量
     * @param deviceNo 设备编号
     * @param logId 日志ID
     */
    public synchronized void measureStart(String deviceNo, Long logId) {
        Optional<FeedTowerDevice> opt = deviceService.findByCache(deviceNo);
        opt.ifPresent(device -> {
            FeedTowerLog towerLog = towerLogMapper.selectById(logId);
            String taskNo = "";
            if ("V1".equals(device.getVersion())) {
                taskNo = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
                v1Service.measure(towerLog, Enable.ON, taskNo);
            } else if ("V2".equals(device.getVersion())) {
                taskNo = CRC16Util.toHexString(System.currentTimeMillis()/1000);
                v2Service.measure(towerLog, Enable.ON, taskNo);
            }
            towerLog.setTaskNo(taskNo);
            towerLogService.updateById(towerLog);
            RBucket<FeedTowerLog> bucket = redis.getBucket(CacheKey.Admin.tower_measure_log.key + towerLog.getDeviceNo() + ":" + towerLog.getTaskNo());
            bucket.set(towerLog);
            bucket.expire(CacheKey.Admin.tower_measure_log.duration);
            measureStartDelayQueue.offer(towerLog.getId());
        });
    }

    void autoCancelMeasure(Long logId, TowerStatus checkStatus, String remark) {
        FeedTowerLog towerLog = towerLogMapper.selectById(logId);
        if (towerLog.getStatus().equals(checkStatus.name())) {
            v2Service.measure(towerLog, Enable.OFF, towerLog.getTaskNo());
            towerLog.setStatus(TowerStatus.cancel.name());
            towerLog.setRemark(remark);
            towerLog.setCompletedTime(LocalDateTime.now());
            towerLogService.updateById(towerLog);
            send(String.format(TowerService.TOPIC_REFRESH, towerLog.getDeviceNo()), 1, "");

            //老化
            towerCalculationV2.isRunAgingCheck(towerLogMapper.selectById(logId),"01");

            //校准
            towerCalculationV2.isRunInitCheck(towerLogMapper.selectById(logId));
        }
    }

    /**
     * 停止余料测量
     * @param deviceNo 设备编号
     * @param taskNo 任务编号
     */
    public void measureStop(String deviceNo, String taskNo) {
        Optional<FeedTowerLog> opt = towerLogService.findByTaskNo(deviceNo, taskNo);
        if (!opt.isPresent()) {
            log.info("停止余量测量未找到启动日志");
            return;
        }
        FeedTowerLog towerLog = opt.get();
        towerLog.setStatus(TowerStatus.cancel.name());
        towerLog.setRemark("主动取消");
        towerLog.setCompletedTime(LocalDateTime.now());
        towerLogService.updateById(towerLog);
        Optional<FeedTowerDevice> deviceOpt = deviceService.findByCache(deviceNo);
        deviceOpt.ifPresent(device -> {
            if ("V1".equals(device.getVersion())) {
                v1Service.measure(towerLog, Enable.OFF, taskNo);
            } else if ("V2".equals(device.getVersion())) {
                v2Service.measure(towerLog, Enable.OFF, taskNo);
            }
        });
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
