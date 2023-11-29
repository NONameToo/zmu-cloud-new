package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.FeedTowerFeedType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@InterceptorIgnore(tenantLine = "true")
public interface FeedTowerFeedTypeMapper extends BaseMapper<FeedTowerFeedType> {

    List<FeedTowerFeedType> selectListByCompanyId(@Param("companyId") Long companyId,@Param("resourceType") String resourceType);
}