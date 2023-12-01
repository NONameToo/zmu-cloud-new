package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @Author gmail.com
 * @Date 2020-08-12 15:58
 */
@Data
@ApiModel("用户个人资料")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileVO {

    @ApiModelProperty("用户信息")
    private SysUser sysUser;
    @ApiModelProperty("角色名")
    private Set<String> roles;
    @ApiModelProperty("用户角色类型")
    private UserRoleTypeEnum role;
}
