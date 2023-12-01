package com.zmu.cloud.commons.config;

import com.zmu.cloud.commons.entity.FeedTowerLog;
import com.zmu.cloud.commons.entity.TowerProperty;
import com.zmu.cloud.commons.redis.CacheKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TowerConfig {

    final RedissonClient redissonClient;

    @Bean
    public TowerProperty towerProperty() {
        TowerProperty towerProperty;
        // token放redis里面方便修改
        RBucket<TowerProperty> tokenBucket = redissonClient.getBucket(CacheKey.Admin.tower_property.key);
        if (tokenBucket.isExists()) {
            towerProperty = tokenBucket.get();
        }else{
            log.warn("料塔配置不存在,使用系统默认配置到缓存中!");
            RBucket<TowerProperty> towerPointCache = redissonClient.getBucket(CacheKey.Admin.tower_property.key);
            // 执行您需要的操作，例如添加、删除、迭代等
            towerProperty = new TowerProperty();
            towerPointCache.set(towerProperty);
        }
        return towerProperty;
    }
}
