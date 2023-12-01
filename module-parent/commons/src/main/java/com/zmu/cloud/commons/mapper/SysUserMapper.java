package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.admin.SysUserQuery;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.vo.OperatorVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @InterceptorIgnore(tenantLine = "true")
    SysUser findUserById(Long id);

    List<SysUser> list(SysUserQuery query);

    @InterceptorIgnore(tenantLine = "true")
    void cleanOtherUserRid(String rid);

    @InterceptorIgnore(tenantLine = "true")
    SysUser getByLoginAccount(@Param("loginName") String loginName);

    int updateStatus(@Param("userId") Long userId);

    List<Long> listUserId(@Param("companyId") Long companyId);

    List<OperatorVO> getOperators(@Param("companyId") Long companyId, @Param("employ") String employ);
}