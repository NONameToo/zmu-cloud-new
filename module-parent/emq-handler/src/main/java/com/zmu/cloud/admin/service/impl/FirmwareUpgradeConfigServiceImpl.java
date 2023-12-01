package com.zmu.cloud.admin.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.cron.CronConfig;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.pattern.CronPatternUtil;
import cn.hutool.cron.task.CronTask;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.xml.bind.v2.TODO;
import com.zmu.cloud.admin.service.FirmwareUpgradeConfigService;
import com.zmu.cloud.commons.dto.FirmwareUpgradeConfigDto;
import com.zmu.cloud.commons.entity.FirmwareUpgradeConfig;
import com.zmu.cloud.commons.entity.FirmwareVersion;
import com.zmu.cloud.commons.enums.Enable;
import com.zmu.cloud.commons.enums.FirmwareCategory;
import com.zmu.cloud.commons.enums.TaskJobEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FirmwareUpgradeConfigMapper;
import com.zmu.cloud.commons.mapper.FirmwareVersionMapper;
import com.zmu.cloud.commons.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirmwareUpgradeConfigServiceImpl extends ServiceImpl<FirmwareUpgradeConfigMapper, FirmwareUpgradeConfig>
        implements FirmwareUpgradeConfigService {

    final FirmwareUpgradeConfigMapper configMapper;
    final FirmwareVersionMapper versionMapper;
    final TaskService taskService;

    @Override
    public FirmwareUpgradeConfig find(FirmwareCategory category) {
        return configMapper.selectOne(Wrappers.lambdaQuery(FirmwareUpgradeConfig.class)
                .eq(FirmwareUpgradeConfig::getCategory, category.name()));
    }

    @Override
    public void save(FirmwareUpgradeConfigDto configDto) {
        FirmwareUpgradeConfig config = configMapper.selectOne(Wrappers.lambdaQuery(FirmwareUpgradeConfig.class)
                .eq(FirmwareUpgradeConfig::getCategory, configDto.getCategory().name()));

        FirmwareVersion version = versionMapper.selectById(configDto.getVersionId());
        LocalDateTime dateTime = LocalDateTimeUtil.parse(configDto.getUpgradeTime(), DatePattern.NORM_DATETIME_PATTERN);
        if (Enable.ON.equals(configDto.getEnable())) {
            if (dateTime.isBefore(LocalDateTime.now())) {
                throw new BaseException("升级时间不能早于当前时间");
            }
        }

        if (null == config) {
            config.setCategory(configDto.getCategory().name());
            config.setUpgradeLimit(configDto.getUpgradeLimit());
            config.setUpgradeTime(dateTime);
            config.setEnable(configDto.getEnable());
            config.setFrameLength(configDto.getFrameLength());
            config.setVersionId(version.getId());
            config.setVersion(version.getVersion());
            config.setVersionFile(version.getSavePath().concat(File.separator).concat(version.getFileName()));
            configMapper.insert(config);
        } else {
            config.setUpgradeLimit(configDto.getUpgradeLimit());
            config.setUpgradeTime(dateTime);
            config.setEnable(configDto.getEnable());
            config.setFrameLength(configDto.getFrameLength());
            config.setVersionId(version.getId());
            config.setVersion(version.getVersion());
            config.setVersionFile(version.getSavePath().concat(File.separator).concat(version.getFileName()));
            configMapper.updateById(config);
        }

        //如果状态为启用，则加入定时任务去执行升级
        if (Enable.ON.equals(configDto.getEnable())) {
            switch (configDto.getCategory()) {
                case tower:
                    String cron = LocalDateTimeUtil.format(config.getUpgradeTime(), "ss mm HH dd MM ? yyyy");
                    taskService.add(configDto.getUpgradeTime(), TaskJobEnum.TowerFirmwareUpgrade.getGroup(), cron,
                            TaskJobEnum.TowerFirmwareUpgrade.getJobClass());
                    break;
                case feeder_master:
//                    taskService.add();
                    break;
                case feeder_slave:
//                    taskService.add();
                    break;
            }
        }


    }

}
