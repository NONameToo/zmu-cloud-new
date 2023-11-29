package com.zmu.auth.authorization.entity;

import com.zmu.cloud.common.web.entity.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false )
@NoArgsConstructor
public class Role extends BasePo {
    private String code;
    private String name;
    private String description;
}
