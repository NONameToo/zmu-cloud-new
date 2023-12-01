package com.zmu.cloud.commons.config;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Locale;
import java.util.TimeZone;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: JacksonConfig
 * @Author
 * @Date 2019-03-30 13:26
 */
@Configuration
public class JacksonConfig {

    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    @Getter
    @Setter
    private String pattern;

    @Autowired(required = false)
    private Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(objectMapper());
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = jackson2ObjectMapperBuilder.build();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        mapper.setLocale(Locale.SIMPLIFIED_CHINESE);
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
