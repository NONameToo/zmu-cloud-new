package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.FeedTowerDevice;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FeedTowerDeviceMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.TowerDeviceService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class TowerDeviceServiceImpl implements TowerDeviceService {

    final FeedTowerDeviceMapper deviceMapper;
    final RedissonClient redis;

    @Override
    public Optional<FeedTowerDevice> findByCache(String deviceNo) {
        RBucket<FeedTowerDevice> bucket = redis.getBucket(CacheKey.Admin.tower_device.key + deviceNo);
        if (bucket.isExists()) {
            return Optional.of(bucket.get());
        }
        FeedTowerDevice device = deviceMapper.selectOne(Wrappers.lambdaQuery(FeedTowerDevice.class)
                .eq(FeedTowerDevice::getDeviceNo, deviceNo).eq(FeedTowerDevice::getDel, 0));
        if (ObjectUtil.isNotNull(device)) {
            bucket.set(device);
        }
        return Optional.ofNullable(device);
    }

    @Override
    public void delByCache(String deviceNo) {
        redis.getBucket(CacheKey.Admin.tower_device.key + deviceNo).delete();
    }
}
