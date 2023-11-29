package com.zmu.cloud.sysadmin.organization.entity.param;

import com.zmu.cloud.common.web.entity.param.BaseParam;
import com.zmu.cloud.sysadmin.organization.entity.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryParam extends BaseParam<User> {
    private String name;
    private String mobile;
    private String username;
}
