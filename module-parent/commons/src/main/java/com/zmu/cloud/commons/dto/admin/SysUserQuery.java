package com.zmu.cloud.commons.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @DESCRIPTION: SysDeptQuery
 * @Date 2020-05-08 20:49
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class SysUserQuery extends BaseQuery {

    @ApiModelProperty("用户姓名")
    private String realName;
    @ApiModelProperty("用户昵称")
    private String nickName;
    @ApiModelProperty("登录账户")
    private String loginName;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("创建时间开始，格式：yyyy-MM-dd HH:mm:ss")
    private String createTimeStart;
    @ApiModelProperty("创建时间结束，格式：yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;
    @ApiModelProperty("状态：（0 停用 1 正常）")
    private Integer status;
    @ApiModelProperty("状态：（0 停用 1 正常）")
    private String rid;
}
