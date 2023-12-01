package com.zmu.cloud.commons.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author kyle lqp0817@gmail.com
 * @date 2021/10/28 09:34
 **/
@Configuration
@Component
@ConditionalOnClass(RedissonClient.class)
public class RedissonConfigurationCustomizer {

  @Bean
  @ConditionalOnMissingBean(RedissonAutoConfigurationCustomizer.class)
  public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer() {
    ObjectMapper objectMapper = new ObjectMapper();
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    javaTimeModule.addSerializer(LocalDateTime.class,
        new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    javaTimeModule.addDeserializer(LocalDateTime.class,
        new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    javaTimeModule
        .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));
    javaTimeModule.addDeserializer(LocalDate.class,
        new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));
    javaTimeModule
        .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ISO_LOCAL_TIME));
    javaTimeModule.addDeserializer(LocalTime.class,
        new LocalTimeDeserializer(DateTimeFormatter.ISO_LOCAL_TIME));

    objectMapper.registerModule(javaTimeModule);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    return configuration -> configuration.setCodec(new JsonJacksonCodec(objectMapper));
  }
}
