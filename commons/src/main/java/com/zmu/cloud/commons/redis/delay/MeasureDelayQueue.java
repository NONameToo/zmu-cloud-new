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
public class MeasureDelayQueue {

    final RedissonClient redis;
    private RDelayedQueue<Long> delayedQueue;
    private RBlockingQueue<Long> blockingQueue;

    @PostConstruct
    private void initDelayQueue() {
        blockingQueue = redis.getBlockingQueue(CacheKey.Queue.measure.type);
        delayedQueue = redis.getDelayedQueue(blockingQueue);
    }

    public RBlockingQueue<Long> getBlockingQueue() {
        return blockingQueue;
    }

    public void offer(Long logId) {
        delayedQueue.offer(logId, CacheKey.Queue.measure.duration.getSeconds(), TimeUnit.SECONDS);
    }

}
