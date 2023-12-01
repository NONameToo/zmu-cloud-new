package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.FeederTaskParam;
import com.zmu.cloud.commons.dto.TaskParam;
import com.zmu.cloud.commons.enums.RedisTopicEnum;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author YH
 */
public interface TaskService {

    @Transactional
    void feederAdd(FeederTaskParam param);
    @Transactional
    void feederUpdate(Long qrtzId, String time);
    @Transactional
    void towerAdd(String deviceNo, String taskTime);
    @Transactional
    void towerUpdate(Long qrtzId, String time);

    void add(String taskName, String taskGroup, String cron, String jobClass);
    void delete(String taskName, String taskGroup);
    void pause(String taskName, String taskGroup);
    void resume(String taskName, String taskGroup);

    /**
     * 按照新的cron重启任务
     */
    void rescheduleJob(String taskName, String taskGroup, String cron);
}
