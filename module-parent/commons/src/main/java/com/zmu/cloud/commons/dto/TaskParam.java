package com.zmu.cloud.commons.dto;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zmu.cloud.commons.enums.TaskOperateEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TaskParam implements Serializable {

    private String name;
    private String group;
    private String cron;
    private JSONObject data;
    private TaskOperateEnum operate;
    private String aClass;

}
