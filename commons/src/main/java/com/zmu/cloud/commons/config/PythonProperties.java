package com.zmu.cloud.commons.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * python计算体积脚本
 *
 */
@Component
@ConfigurationProperties(prefix = "python")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PythonProperties {
    private String windows;

    private String linux;

    private String scriptPathW;
    private String scriptPathL;

    private String method;

    private String windowsFileBasePath;

    private String linuxFileBasePath;


}
