package com.zmu.cloud.commons.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.Filter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@ConditionalOnClass(Filter.class)
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsOriginsFilterConfig extends CorsFilter {

    public CorsOriginsFilterConfig() {
        super(configurationSource());
    }

    private static UrlBasedCorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        List<String> allowedHeaders = Collections.singletonList("*");
        List<String> exposedHeaders = Arrays.asList("x-auth-token", "content-type", "X-Requested-With", "XMLHttpRequest", "Content-Disposition");
        List<String> allowedMethods = Arrays.asList("POST", "GET", "DELETE", "PUT", "OPTIONS");
        List<String> allowedOrigins = Collections.singletonList("*");
        corsConfig.setAllowedHeaders(allowedHeaders);
        corsConfig.setAllowedMethods(allowedMethods);
        corsConfig.setAllowedOriginPatterns(allowedOrigins);
        corsConfig.setExposedHeaders(exposedHeaders);
        corsConfig.setMaxAge(36000L);
        corsConfig.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}