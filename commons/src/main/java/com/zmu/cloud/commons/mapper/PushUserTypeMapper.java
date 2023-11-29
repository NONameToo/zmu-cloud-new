package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.PushUserType;
import com.zmu.cloud.commons.entity.admin.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface PushUserTypeMapper extends BaseMapper<PushUserType> {
    int deleteByPrimaryKey(@Param("userId") Long userId, @Param("typeId") Long typeId);

    int countUserByTypeId(@Param("typeId") Long typeId);

    int deleteByTypeId(@Param("typeId") Long typeId);

    int deleteByUserId(@Param("userId") Long userId);

    int batchInsert(@Param("records") List<PushUserType> records);

    Set<Long> listUserIdByTypeId(@Param("typeId") Long typeId);

    @InterceptorIgnore(tenantLine = "true")
    List<SysUser> getUserRidByFarmIdAndMessageTypeKey(@Param("farmId") Long farmId,@Param("messageTypeKey") String messageTypeKey);
}