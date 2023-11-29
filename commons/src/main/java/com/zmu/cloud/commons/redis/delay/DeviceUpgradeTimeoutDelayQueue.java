package com.zmu.cloud.commons.redis.delay;

import com.zmu.cloud.commons.redis.CacheKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author YH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceUpgradeTimeoutDelayQueue {

    final RedissonClient redis;
    private RDelayedQueue<String> delayedQueue;
    private RBlockingQueue<String> blockingQueue;
    public static long TIMEOUT = 5;

    @PostConstruct
    private void initDelayQueue() {
        blockingQueue = redis.getBlockingQueue(CacheKey.Queue.deviceUpgradeTimeout.type);
        delayedQueue = redis.getDelayedQueue(blockingQueue);
    }

    public RBlockingQueue<String> getBlockingQueue() {
        return blockingQueue;
    }

    public void offer(String deviceNo) {
        delayedQueue.offer(deviceNo, TIMEOUT, TimeUnit.MINUTES);
    }

}
