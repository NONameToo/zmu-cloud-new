package com.zmu.cloud.admin.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.admin.mqtt.MqttServer;
import com.zmu.cloud.admin.mqtt.TowerMessageHandleForV2Service;
import com.zmu.cloud.admin.service.FirmwareUpgradeConfigService;
import com.zmu.cloud.admin.service.FirmwareUpgradeDetailService;
import com.zmu.cloud.admin.service.FirmwareUpgradeService;
import com.zmu.cloud.admin.service.FirmwareVersionService;
import com.zmu.cloud.commons.dto.*;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.FirmwareCategory;
import com.zmu.cloud.commons.enums.UpgradeSchedule;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.TowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirmwareUpgradeServiceImpl implements FirmwareUpgradeService {

    final RedissonClient redis;
    final PigFarmMapper farmMapper;
    final MqttServer mqttServer;
    final FirmwareVersionMapper versionMapper;
    final FirmwareUpgradeReportMapper reportMapper;
    final FirmwareUpgradeDetailService upgradeDetailService;
    final FirmwareUpgradeDetailMapper upgradeDetailMapper;
    final FirmwareUpgradeConfigService configService;
    final FeedTowerDeviceMapper towerDeviceMapper;
    final TowerMessageHandleForV2Service v2Service;

    @Override
    public List<FirmwareUpgradeForFarmDto> towerDevices(String farmName, String deviceNo, String versionCode) {
        List<PigFarm> farms = farmMapper.selectList(Wrappers.lambdaQuery(PigFarm.class)
                .like(PigFarm::getName, farmName));
        return farms.stream().map(farm -> {
            //获取猪场下面待升级的设备
            LambdaQueryWrapper<FeedTowerDevice>  lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FeedTowerDevice::getPigFarmId, farm.getId());
            lambdaQueryWrapper.ne(FeedTowerDevice::getTowerId, 0L);
            if(ObjectUtil.isNotEmpty(versionCode)){
                lambdaQueryWrapper.like(FeedTowerDevice::getVersionCode, versionCode);
            }
            if(ObjectUtil.isNotEmpty(deviceNo)){
                lambdaQueryWrapper.like(FeedTowerDevice::getDeviceNo, deviceNo);
            }

            List<FeedTowerDevice> devices = towerDeviceMapper.selectList(lambdaQueryWrapper);

            List<FirmwareUpgradeForDeviceDto> deviceDtos = devices.stream().map(dev -> {
                RBucket<DeviceStatus> bucket = redis.getBucket(CacheKey.Admin.device_status.key + dev.getDeviceNo());
                String network = "离线";
                if (bucket.isExists() && !"离线".equals(bucket.get().getNetworkStatus())) {
                    network = bucket.get().getNetworkStatus();
                }
                FirmwareUpgradeConfig config = configService.find(FirmwareCategory.tower);
                return FirmwareUpgradeForDeviceDto.builder().deviceId(dev.getId()).deviceNo(dev.getDeviceNo())
                        .version(dev.getVersion())
                        .versionCode(dev.getVersionCode())
                        .upgradeVersion(ObjectUtil.isEmpty(config)?"":config.getVersion())
//                        .upgradeSchedule()
                        .towerName(dev.getTowerId() != 0 ? towerDeviceMapper.getTowerNameByTowerId(dev.getTowerId()) : null)
                        .network(network).build();}).collect(toList());
            return FirmwareUpgradeForFarmDto.builder().farmName(farm.getName()).deviceNum(devices.size()).devices(deviceDtos).build();
        }).collect(toList());
    }

    @Override
    public void towerUpgrade(FirmwareUpgradeParam param) {
        if (ObjectUtil.isEmpty(param.getVersionCode())) {
            throw new BaseException("未配置升级版本");
        }
        //生成升级报告及明细
        FirmwareUpgradeReport report = FirmwareUpgradeReport.builder()
                .firmwareCategory(FirmwareCategory.tower.name()).firmwareVersion(param.getVersionCode())
                .upgradeCount(param.getDeviceIds().size()).upgradeTime(LocalDateTime.now()).build();
        reportMapper.insert(report);
        List<FirmwareUpgradeDetail> details = param.getDeviceIds().stream().map(id -> {
            FeedTowerDevice device = towerDeviceMapper.selectById(id);
            //升级
            v2Service.upgradeFirmware(device.getDeviceNo(), 1);
            return FirmwareUpgradeDetail.builder().deviceNo(device.getDeviceNo()).upgradeTime(LocalDateTime.now())
                    .upgradeSchedule(UpgradeSchedule.connect.name()).reportId(report.getId()).build();
        }).filter(ObjectUtil::isNotNull).collect(toList());
        upgradeDetailService.saveBatch(details);
    }

    @Override
    public PageInfo<FirmwareUpgradeReport> towerUpgradeReports(QueryFirmwareUpgradeReport query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        List<FirmwareUpgradeReport> reports = reportMapper.selectList(Wrappers.lambdaQuery(FirmwareUpgradeReport.class)
                .eq(FirmwareUpgradeReport::getFirmwareCategory, query.getCategory())
                .like(FirmwareUpgradeReport::getFirmwareVersion, query.getVersion())
                .orderByDesc(FirmwareUpgradeReport::getUpgradeTime)
        );
        return PageInfo.of(reports);
    }

    @Override
    public TowerUpgradeReportDetailDto towerUpgradeReportDetail(QueryFirmwareUpgradeReportDetailParam param) {
        TowerUpgradeReportDetailDto.TowerUpgradeReportDetailDtoBuilder builder = TowerUpgradeReportDetailDto.builder();
        builder.report(reportMapper.selectById(param.getReportId()));
        LambdaQueryWrapper<FirmwareUpgradeDetail> wrapper = Wrappers.lambdaQuery(FirmwareUpgradeDetail.class);
        wrapper.eq(FirmwareUpgradeDetail::getReportId, param.getReportId());
        if (ObjectUtil.isNotEmpty(param.getDeviceNo())) {
            wrapper.like(FirmwareUpgradeDetail::getDeviceNo, param.getDeviceNo());
        }
        if (ObjectUtil.isNotEmpty(param.getSchedule())) {
            wrapper.eq(FirmwareUpgradeDetail::getUpgradeSchedule, param.getSchedule().name());
        }
        wrapper.orderByDesc(FirmwareUpgradeDetail::getSeq);
        builder.details(upgradeDetailMapper.selectList(wrapper));

        return builder.build();
    }
}
