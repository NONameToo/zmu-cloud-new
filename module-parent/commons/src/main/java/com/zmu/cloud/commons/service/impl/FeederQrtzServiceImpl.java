package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zmu.cloud.commons.dto.FeederTaskParam;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.FeederQrtz;
import com.zmu.cloud.commons.entity.MaterialLine;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.enums.HouseTypeForSph;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FeederQrtzMapper;
import com.zmu.cloud.commons.mapper.MaterialLineMapper;
import com.zmu.cloud.commons.mapper.PigHouseMapper;
import com.zmu.cloud.commons.service.FeederQrtzService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.FeederTaskDetail;
import com.zmu.cloud.commons.vo.FeederTaskForLineVo;
import com.zmu.cloud.commons.vo.FeederTaskVo;
import com.zmu.cloud.commons.vo.PigHouseTypeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeederQrtzServiceImpl implements FeederQrtzService {

    final FeederQrtzMapper feederQrtzMapper;
    final MaterialLineMapper lineMapper;
    final PigHouseMapper houseMapper;

    @Override
    public void addFeederQrtz(FeederTaskParam param, String taskName, String taskGroup) {

        checkLineTasks(null, param.getHouseId(), param.getMaterialLineId(), param.getTaskTime());

        RequestInfo info = RequestContextUtils.getRequestInfo();
        MaterialLine line = lineMapper.selectById(param.getMaterialLineId());
        PigHouse house = houseMapper.selectById(param.getHouseId());
        FeederQrtz qrtz = new FeederQrtz();
        qrtz.setTriggerTime(param.getTaskTime());
        qrtz.setFarmId(info.getPigFarmId());
        qrtz.setHouseId(house.getId());
        qrtz.setHouseName(house.getName());
        qrtz.setHouseTypeId(house.getType());
        qrtz.setHouseType(HouseTypeForSph.getInstance(house.getType()));
        qrtz.setMaterialLineId(line.getId());
        qrtz.setMaterialLineName(line.getName());
        qrtz.setJobName(taskName);
        qrtz.setJobGroup(taskGroup);
        qrtz.setJobEnable(1);
        qrtz.setBlendFeedId(param.getBlendFeedId());
        feederQrtzMapper.insert(qrtz);
    }

    /**
     * 同一条料线上最多只能有两个时间点、且启用的时间点之间至少相隔2个小时
     * @param qrtzId
     * @param houseId
     * @param materialLineId
     * @param time
     */
    @Override
    public void checkLineTasks(Long qrtzId, Long houseId, Long materialLineId, String time) {
        List<FeederQrtz> qrtzs = feederQrtzMapper.selectList(Wrappers.lambdaQuery(FeederQrtz.class)
                .eq(FeederQrtz::getHouseId, houseId)
                .eq(FeederQrtz::getMaterialLineId, materialLineId));
        if (null == qrtzId && qrtzs.size() > 1) {
            throw new BaseException("同一条料线上最多有两个下料时间点,已有【%s】",
                    qrtzs.stream().map(FeederQrtz::getTriggerTime).collect(Collectors.joining(",")));
        }
        DateTime exists = null,newTime = DateUtil.parseTime(time + ":00");
        //新增
        if (null == qrtzId) {
            if (qrtzs.size() == 1) {
                exists = DateUtil.parseTime(qrtzs.get(0).getTriggerTime() + ":00");
            }
        }
        //修改
        else {
            Optional<String> optTime = qrtzs.stream().filter(q -> !q.getId().equals(qrtzId)).map(FeederQrtz::getTriggerTime).findFirst();
            if (optTime.isPresent()) {
                exists = DateUtil.parseTime(optTime.get() + ":00");
            }
        }
        if (null != exists && exists.between(newTime, DateUnit.MINUTE) <= 120) {
            throw new BaseException("同一条料线上的两个下料时间点应相差2小时以上");
        }
    }

    @Override
    public List<FeederTaskVo> findQrtzByHouseType(Integer houseType, boolean feedAgain) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        List<FeederQrtz> qrtzs = find(farmId, null, houseType, null, null, null, feedAgain);
        List<FeederTaskVo> vos = new ArrayList<>();
        //按栋舍分组
        Map<String, List<FeederQrtz>> qrtzMap = qrtzs.stream().collect(Collectors.groupingBy(FeederQrtz::getHouseName));
        qrtzMap.forEach((houseName, houseQrtzs) -> {
            List<FeederTaskForLineVo> lineVos = new ArrayList<>();
            FeederTaskVo.FeederTaskVoBuilder taskVoBuilder =  FeederTaskVo.builder().houseName(houseName);
            houseQrtzs.stream()
                    //按料线分组
                    .collect(Collectors.groupingBy(FeederQrtz::getMaterialLineId))
                    .forEach((line, lineQrtzs) -> {
                        FeederTaskForLineVo.FeederTaskForLineVoBuilder lineVoBuilder = FeederTaskForLineVo.builder();
                        lineVoBuilder.lineId(line)
                                .lineName(lineQrtzs.stream().findAny().orElse(new FeederQrtz()).getMaterialLineName())
                                .lineDetails(lineQrtzs.stream().sorted(Comparator.comparing(FeederQrtz::getTriggerTime))
                                        .map(qrtz -> FeederTaskDetail.builder()
                                                .qrtzId(qrtz.getId())
                                                .name(qrtz.getMaterialLineName() + " 时间点")
                                                .time(qrtz.getTriggerTime())
                                                .enable(qrtz.getJobEnable())
                                                .build())
                                        .collect(Collectors.toList()));
                        lineVos.add(lineVoBuilder.build());
                    });
            vos.add(taskVoBuilder.houseDetails(
                    lineVos.stream().sorted(Comparator.comparing(FeederTaskForLineVo::getLineName)).collect(Collectors.toList())
            ).build());
        });
        return vos;
    }

    @Override
    public Set<PigHouseTypeVo> feederHouseTypes() {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        List<FeederQrtz> qrtzs = find(farmId, null, null, null, null, null, false);
        return qrtzs.stream().map(qrtz ->
                PigHouseTypeVo.builder().typeId(qrtz.getHouseTypeId()).typeName(qrtz.getHouseType()).build()
        ).collect(Collectors.toSet());
    }

    @Override
    public void enable(Long id, Integer enable) {
        FeederQrtz qrtz = feederQrtzMapper.selectById(id);

        if (1 == enable) {
            checkLineTasks(id, qrtz.getHouseId(), qrtz.getMaterialLineId(), qrtz.getTriggerTime());
        }

        qrtz.setJobEnable(enable);
        feederQrtzMapper.updateById(qrtz);
    }

    @Override
    public void delete(Long id) {
        feederQrtzMapper.deleteById(id);
    }

    @Override
    public FeederQrtz findQrtzByBlendFeed() {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        return feederQrtzMapper.selectOne(
                Wrappers.lambdaQuery(FeederQrtz.class).eq(FeederQrtz::getFarmId, farmId).isNotNull(FeederQrtz::getBlendFeedId));
    }

    private List<FeederQrtz> find(Long farmId, Long houseId, Integer houseType,
                                  String taskTime, String taskName, String taskGroup, boolean feedAgain) {
        LambdaQueryWrapper<FeederQrtz> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(farmId)) {
            wrapper.eq(FeederQrtz::getFarmId, farmId);
        }
        if (ObjectUtil.isNotEmpty(houseId)) {
            wrapper.eq(FeederQrtz::getHouseId, houseId);
        }
        if (ObjectUtil.isNotEmpty(houseType)) {
            wrapper.eq(FeederQrtz::getHouseTypeId, houseType);
        }
        if (ObjectUtil.isNotEmpty(taskTime)) {
            wrapper.eq(FeederQrtz::getTriggerTime, taskTime);
        }
        if (ObjectUtil.isNotEmpty(taskName)) {
            wrapper.eq(FeederQrtz::getJobName, taskName);
        }
        if (ObjectUtil.isNotEmpty(taskGroup)) {
            wrapper.eq(FeederQrtz::getJobGroup, taskGroup);
        }
        if (feedAgain) {
            wrapper.gt(FeederQrtz::getBlendFeedId, 0);
        }
        return feederQrtzMapper.selectList(wrapper);
    }
}
