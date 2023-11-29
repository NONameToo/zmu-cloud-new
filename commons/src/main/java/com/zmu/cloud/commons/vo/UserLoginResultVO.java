package com.zmu.cloud.commons.vo;

import cn.hutool.json.JSONArray;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @Author gmail.com
 * @Date 2020-08-12 13:56
 */
@Data
@ApiModel("登录结果")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResultVO {

    @ApiModelProperty("token")
    private String token;
    @ApiModelProperty("用户")
    private SysUser user;
    @ApiModelProperty("公司ID")
    private Long companyId;
    @ApiModelProperty("公司名称")
    private String companyName;
    @ApiModelProperty("默认猪场ID")
    private Long defaultFarmId;
    @ApiModelProperty("默认猪场名称")
    private String defaultFarmName;
    @ApiModelProperty("默认猪场猪种")
    private String defaultPigTypeName;
    @ApiModelProperty("角色名")
    private Set<String> roles;
    @ApiModelProperty("当前用户拥有的猪场列表")
    private List<PigFarmVO> pigFarms;
    @ApiModelProperty("巨星用户的菜单")
    private JSONArray menus;
    @ApiModelProperty("当前用户管理的实体列表（猪场、饲料厂、其他）")
    private String manageIds;
    @ApiModelProperty("用户来源")
    private ResourceType resourceType;
    @ApiModelProperty("登录客户端")
    private UserClientTypeEnum clientTypeEnum;
    @ApiModelProperty("用户角色")
    private UserRoleTypeEnum roleTypeEnum;

    @ApiModelProperty("巨星-deptNm")
    private String deptNm;
    @ApiModelProperty("巨星-managerZc")
    private String managerZc;
    @ApiModelProperty("巨星-managerZcNm")
    private String managerZcNm;
    @ApiModelProperty("巨星-mOrgId")
    private String mOrgId;
    @ApiModelProperty("巨星-mOrgNm")
    private String mOrgNm;
    @ApiModelProperty("巨星-resInfo")
    private List<ResInfoVo> resInfo;
}
