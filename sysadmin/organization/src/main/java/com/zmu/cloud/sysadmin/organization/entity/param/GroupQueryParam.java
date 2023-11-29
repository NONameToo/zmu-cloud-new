package com.zmu.cloud.sysadmin.organization.entity.param;

import com.zmu.cloud.common.web.entity.param.BaseParam;
import com.zmu.cloud.sysadmin.organization.entity.po.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupQueryParam extends BaseParam<Group> {
    private String name;
}
