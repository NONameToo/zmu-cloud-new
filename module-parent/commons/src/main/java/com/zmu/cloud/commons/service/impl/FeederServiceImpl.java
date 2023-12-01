package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.entity.Feeder;
import com.zmu.cloud.commons.mapper.FeederMapper;
import com.zmu.cloud.commons.service.FeederService;
import com.zmu.cloud.commons.service.ManualFeedingRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Slf4j
@Service
@AllArgsConstructor
public class FeederServiceImpl extends ServiceImpl<FeederMapper, Feeder>
        implements FeederService {

    FeederMapper feederMapper;
    ManualFeedingRecordService manualFeedingRecordService;

    @Override
    public List<Feeder> feeders(Long clientId, Integer feederCode) {
        LambdaQueryWrapper<Feeder> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(clientId)) {
            wrapper.eq(Feeder::getClientId, clientId);
        }
        if (ObjectUtil.isNotEmpty(feederCode)) {
            wrapper.eq(Feeder::getFeederCode, feederCode);
        }
        return feederMapper.selectList(wrapper);
    }

    @Override
    public void saveFeederStatus(Long clientId, String value) {
        List<String> feederValues = new ArrayList<>();
        resolveValue(value.substring(2), feederValues);
        //转化
        List<Feeder> receivedFeeders = feederValues.stream()
                .map(str -> {
                    Feeder feeder = new Feeder();
                    feeder.setClientId(clientId);
                    feeder.setFeederCode(Integer.parseInt(str.substring(0, 2), 16));
                    feeder.setStatus(str.substring(2, 4));
                    feeder.setError(str.substring(4));
                    feeder.setDate(LocalDateTime.now());
                    //如果是手动下料异步变更手动下料状态
                    if ("04".equals(feeder.getStatus())) {
                        manualFeedingRecordService.modifyStatus(clientId, feeder.getFeederCode(), feeder.getStatus());
                    }

                    return feeder;
                }).collect(toList());
        List<Integer> codes = receivedFeeders.stream().map(Feeder::getFeederCode).collect(toList());
        //已存在的记录
        List<Feeder> exists = findByClientAndCodes(clientId, codes)
                .stream().map(f -> {
                    Feeder feeder = receivedFeeders.stream()
                            .filter(rf -> rf.getFeederCode().equals(f.getFeederCode())).findFirst().get();
                    f.setStatus(feeder.getStatus());
                    f.setError(feeder.getError());
                    f.setDate(LocalDateTime.now());
                    return f;
                }).collect(toList());
        //不存在，则新增
        List<Feeder> notExists = receivedFeeders.stream()
                .filter(rf -> !exists.stream().anyMatch(e -> e.getFeederCode().equals(rf.getFeederCode())))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(notExists)) {
            saveBatch(notExists);
        }
        if (!CollectionUtils.isEmpty(exists)) {
            updateBatchById(exists);
        }
    }

    @Override
    public List<Feeder> findByClientAndCodes(Long clientId, List<Integer> feederCodes) {
        LambdaQueryWrapper<Feeder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feeder::getClientId, clientId);
        if (ObjectUtil.isNotEmpty(feederCodes)) {
            wrapper.in(Feeder::getFeederCode, feederCodes);
        }
        wrapper.orderByAsc(Feeder::getFeederCode);
        return feederMapper.selectList(wrapper);
    }

    @Override
    public void saveFeederSensorWeight(Long clientId, String value) {
        Integer code = Integer.parseInt(value.substring(0, 2), 16);
        Integer weight = Integer.parseInt(value.substring(2), 16);
        Feeder feeder = findFeeder(clientId, code);
        feeder.setSensorWeight(weight);
        feederMapper.updateById(feeder);
    }

    private Feeder findFeeder(Long clientId, Integer code) {
        LambdaQueryWrapper<Feeder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feeder::getClientId, clientId);
        wrapper.eq(Feeder::getFeederCode, code);
        List<Feeder> feeders = feeders(clientId, code);
        Feeder feeder;
        if (CollectionUtils.isEmpty(feeders)) {
            feeder = new Feeder();
            feeder.setFeederCode(code);
            feeder.setClientId(clientId);
            feederMapper.insert(feeder);
        } else {
            feeder = feeders.get(0);
        }

        return feeder;
    }

    private static void resolveValue(String subValue, List<String> feeders) {
        feeders.add(subValue.substring(0, 6));
        String temp = subValue.substring(6);
        if (!StringUtils.isEmpty(temp)) {
            resolveValue(temp, feeders);
        }
    }
}
