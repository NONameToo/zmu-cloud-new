package com.zmu.cloud.commons.dto.commons.oss;

import java.io.Serializable;
import lombok.Data;

@Data
public class OssCallbackResponse implements Serializable {
    private String autorizationInput;
    private String pubKeyInputp;
    private String uri;
    private String queryString;
    private String ossCallbackBody;

    public OssCallbackResponse(String autorizationInput, String pubKeyInputp, String uri, String queryString, String ossCallbackBody) {
        this.autorizationInput = autorizationInput;
        this.pubKeyInputp = pubKeyInputp;
        this.uri = uri;
        this.queryString = queryString;
        this.ossCallbackBody = ossCallbackBody;
    }

    public OssCallbackResponse() {
    }
}
