package com.zmu.cloud.commons.redis;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * redis Stream 监听消息
 */
@Slf4j
//@Component
@AllArgsConstructor
public class RedisStreamListenerMessage implements StreamListener<String, ObjectRecord<String, Object>> {

    /**
     * redis Stream 工具类
     */
    final RedisStream redisStream;

    @PostConstruct
    @Order(999999)
    public void test() {
        redisStream.add(CacheKey.RedisStreamKey, "test", "val");
    }

    /**
     * 消息监听
     *
     * @param message e
     */
    @SneakyThrows
    @Override
    public void onMessage(ObjectRecord<String, Object> message) {
        log.info("接受redisStream: {},监听到消息：{}", message.getStream(), message);
        if (CacheKey.RedisStreamKey.equals(message.getStream())) {
            JSONObject jsonObject = JSONUtil.parseObj(message.getValue());
            System.out.println(jsonObject);
            // TODO: 2023/2/22 消息处理

            // 消费完成后确认消费（ACK）
            redisStream.ack(String.valueOf(message.getStream()), CacheKey.RedisStreamGroup, String.valueOf(message.getId()));
            log.info("确认消费key: {}, 群组：{}, 消息Id：{}", message.getStream(), CacheKey.RedisStreamGroup, message.getId());
        }
    }
}
