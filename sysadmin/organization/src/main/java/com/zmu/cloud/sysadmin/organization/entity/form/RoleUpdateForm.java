package com.zmu.cloud.sysadmin.organization.entity.form;

import com.zmu.cloud.common.web.entity.form.BaseForm;
import com.zmu.cloud.sysadmin.organization.entity.po.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@ApiModel
@Data
public class RoleUpdateForm extends BaseForm<Role> {

    @ApiModelProperty(value = "角色编码")
    private String code;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色描述")
    private String description;

    @ApiModelProperty(value = "角色拥有的资源id列表")
    private Set<String> resourceIds;

}
