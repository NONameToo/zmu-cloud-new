package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.admin.SysMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @Author gmail.com
 * @Date 2020-08-12 16:34
 */
@Data
@ApiModel("用户权限信息")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionVO {

    @ApiModelProperty("公司ID")
    private Long companyId;
    @ApiModelProperty("公司名称")
    private String companyName;
    @ApiModelProperty("默认猪场ID")
    private Long defaultFarmId;
    @ApiModelProperty("默认猪场名称")
    private String defaultFarmName;
    @ApiModelProperty("权限标识")
    private Set<String> permissions;
    @ApiModelProperty("菜单列表")
    private List<SysMenu> sysMenuList;
    @ApiModelProperty("角色Key列表")
    private Set<String> roleKeyList;
}
