package com.zmu.cloud.commons.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.Objects;

/**
 * redis 监听器配置
 */
@Slf4j
//@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

    final RedisStreamListenerMessage streamListener;
    final RedisStreamListenerMessageAutoAck streamListenerAutoAck;
    final RedisStream redisStream;
    final ThreadPoolTaskExecutor taskExecutor;


    /**
     * 收到消息后不自动确认，需要用户选择合适的时机确认
     * <p>
     * 当某个消息被ACK，PEL列表就会减少
     * 如果忘记确认（ACK），则PEL列表会不断增长占用内存
     * 如果服务器发生意外，重启连接后将再次收到PEL中的消息ID列表
     *
     * @param factory
     * @return
     */
    @Bean
    public StreamMessageListenerContainer subscription(RedisConnectionFactory factory) {
        // 创建Stream消息监听容器配置
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, Object>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                //一次最多处理10条数据
                .batchSize(10)
                //批量处理线程池
                .executor(taskExecutor)
                // 设置阻塞时间
                .pollTimeout(Duration.ofSeconds(1))
                // 配置消息类型
                .targetType(Object.class)
                .build();
        // 创建Stream消息监听容器
        StreamMessageListenerContainer<String, ObjectRecord<String, Object>> listenerContainer = StreamMessageListenerContainer.create(factory, options);
        // 设置消费手动Ack配置
        checkGroup(CacheKey.RedisStreamKey, CacheKey.RedisStreamGroup);
        listenerContainer.receive(
                // 设置消费者分组和名称
                Consumer.from(CacheKey.RedisStreamGroup, "consumer-1"),
                // 设置订阅Stream的key和获取偏移量，以及消费处理类
                StreamOffset.create(CacheKey.RedisStreamKey, ReadOffset.lastConsumed()),
                streamListener);

        // 设置消费自动Ack配置
        checkGroup(CacheKey.RedisStreamKey, CacheKey.RedisStreamAutoAckGroup);
        listenerContainer.receiveAutoAck(
                // 设置消费者分组和名称
                Consumer.from(CacheKey.RedisStreamAutoAckGroup, "consumer-1"),
                // 设置订阅Stream的key和获取偏移量，以及消费处理类
                StreamOffset.create(CacheKey.RedisStreamKey, ReadOffset.lastConsumed()),
                streamListenerAutoAck);
        // 监听容器启动
        listenerContainer.start();
        return listenerContainer;
    }

    /**
     * 由于订阅需要先有stream，先做下检查
     */
    private void checkGroup(String key, String group) {
        StreamInfo.XInfoConsumers infoGroups = null;
        try {
            // 获取Stream的所有组信息
            infoGroups = redisStream.consumers(key, group);
        } catch (RedisSystemException | InvalidDataAccessApiUsageException ex) {
            log.error("group key not exist or commend error", ex);
        }

        boolean consumerExist = Objects.nonNull(infoGroups);
        // 创建不存在的分组
        if (!consumerExist) {
            redisStream.creatGroup(key, group);
        }
    }
}
