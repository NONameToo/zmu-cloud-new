package com.zmu.cloud.commons.dto.commons.sms;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.Data;

@Data
@ApiModel
public class SmsSendResponse implements Serializable {
    private static final long serialVersionUID = -5762242124144642361L;


    /**
	 * 响应时间
	 */
	private String time;
	/**
	 * 消息id
	 */
	private String msgId;
	/**
	 * 状态码说明（成功返回空）
	 */
	private String errorMsg;
	/**
	 * 状态码（详细参考提交响应状态码）
	 */
	private String code;

}
