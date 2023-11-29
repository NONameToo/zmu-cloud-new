package com.zmu.cloud.sysadmin.organization.entity.param;

import com.zmu.cloud.common.web.entity.param.BaseParam;
import com.zmu.cloud.sysadmin.organization.entity.po.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceQueryParam extends BaseParam<Resource> {
    private String name;
    private String code;
    private String type;
    private String url;
    private String method;
}
