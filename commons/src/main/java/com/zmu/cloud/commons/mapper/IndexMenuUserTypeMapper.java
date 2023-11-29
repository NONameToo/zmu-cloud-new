package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.IndexMenuUserType;
import com.zmu.cloud.commons.enums.ZmuApp;
import org.apache.ibatis.annotations.Param;import java.util.List;import java.util.Set;

public interface IndexMenuUserTypeMapper extends BaseMapper<IndexMenuUserType> {
    int countUserByTypeId(@Param("typeId") Long typeId);

    int deleteByTypeId(@Param("typeId") Long typeId);

    int deleteByUserId(@Param("userId") Long userId, @Param("app") String app);

    int batchInsert(@Param("records") List<IndexMenuUserType> records);

    Set<Long> listUserIdByTypeId(@Param("typeId") Long typeId);
}