package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zmu.cloud.commons.dto.FeederTaskParam;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.FeedTowerQrtz;
import com.zmu.cloud.commons.entity.FeederQrtz;
import com.zmu.cloud.commons.enums.TaskJobEnum;
import com.zmu.cloud.commons.mapper.FeedTowerQrtzMapper;
import com.zmu.cloud.commons.service.TowerQrtzService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TowerQrtzServiceImpl implements TowerQrtzService {

    final FeedTowerQrtzMapper towerQrtzMapper;

    @Override
    public List<FeedTowerQrtz> find(Long towerId) {
        List<FeedTowerQrtz> qrtzs = towerQrtzMapper.selectList(
                Wrappers.lambdaQuery(FeedTowerQrtz.class).eq(FeedTowerQrtz::getTowerId, towerId));
        return qrtzs.stream()
                .peek(q -> q.setTriggerTime(DateUtil.parseTime(q.getTriggerTime() + ":00").toTimeStr()))
                .sorted(Comparator.comparing(FeedTowerQrtz::getTriggerTime))
                .peek(q -> q.setTriggerTime(DateUtil.format(DateUtil.parseTime(q.getTriggerTime()), "HH:mm")))
                .collect(Collectors.toList());
    }

    @Override
    public FeedTowerQrtz addTowerQrtz(FeedTower tower, String taskTime) {
        FeedTowerQrtz qrtz = new FeedTowerQrtz();
        qrtz.setTowerId(tower.getId());
        qrtz.setDeviceNo(tower.getDeviceNo());
        qrtz.setTriggerTime(taskTime);
        qrtz.setJobName(taskTime);
        qrtz.setJobGroup(TaskJobEnum.Tower.getGroup());
        qrtz.setJobEnable(1);
        towerQrtzMapper.insert(qrtz);
        return qrtz;
    }

    @Override
    public void deleteByDeviceNo(String deviceNo) {
        towerQrtzMapper.delete(Wrappers.lambdaQuery(FeedTowerQrtz.class).eq(FeedTowerQrtz::getDeviceNo, deviceNo));
    }

    @Override
    public void enable(Long id, Integer enable) {
        FeedTowerQrtz qrtz = towerQrtzMapper.selectById(id);
        qrtz.setJobEnable(enable);
        towerQrtzMapper.updateById(qrtz);
    }
}
