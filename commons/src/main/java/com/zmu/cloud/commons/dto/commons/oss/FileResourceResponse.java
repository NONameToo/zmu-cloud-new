package com.zmu.cloud.commons.dto.commons.oss;

import lombok.Data;

@Data
public class FileResourceResponse {

    //文件名
    private String name;

    private String url;

    private Long size;

    private Long height;

    private Long width;

    private String mimeType;

    private String format;
}
