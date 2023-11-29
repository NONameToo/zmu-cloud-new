package com.zmu.cloud.sysadmin.organization.entity.param;

import com.zmu.cloud.common.web.entity.param.BaseParam;
import com.zmu.cloud.sysadmin.organization.entity.po.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleQueryParam extends BaseParam<Role> {
    private String code;
    private String name;
}
