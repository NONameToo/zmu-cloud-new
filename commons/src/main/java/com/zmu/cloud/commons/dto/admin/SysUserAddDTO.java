package com.zmu.cloud.commons.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 11:04
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserAddDTO {

    @ApiModelProperty(value = "登录账号", required = true)
    @NotBlank
    private String loginName;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "真实姓名", required = true)
    @NotBlank
    private String realName;

    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ApiModelProperty(value = "手机号码"/*, required = true*/)
//    @NotBlank
    private String phone;

    @ApiModelProperty(value = "用户性别（0男 1女 2未知）")
    private Integer sex;

    @ApiModelProperty(value = "头像路径")
    private String avatar;

    @ApiModelProperty(value = "密码", required = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String password;

    @ApiModelProperty(value = "帐号状态（1 正常 0 停用）", required = true)
    @Range(min = 0, max = 1)
    @NotNull
    private Integer status;

    @ApiModelProperty("角色id")
    private Set<Long> roleIds;

    @ApiModelProperty("消息推送类型id")
    private Set<Long> pushMessageTypeIds;

    @ApiModelProperty("猪场id列表")
    private List<Long> farmIds;

    @JsonIgnore
    private Long companyId;
    @JsonIgnore
    private Long createBy;
    @JsonIgnore
    private UserRoleTypeEnum userRoleType;
}
