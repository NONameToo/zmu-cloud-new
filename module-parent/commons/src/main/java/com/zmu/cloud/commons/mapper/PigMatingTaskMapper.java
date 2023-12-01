package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigMatingTask;
import com.zmu.cloud.commons.vo.PigMatingTaskListVO;
import com.zmu.cloud.commons.vo.PushMessageTaskCountVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PigMatingTaskMapper extends BaseMapper<PigMatingTask> {
    PigMatingTask selectByPigBreedingId(Long pigBreedingId);

    List<PigMatingTaskListVO> page(QueryPig queryPig);

    @InterceptorIgnore(tenantLine = "true")
    List<PushMessageTaskCountVO> selectPushMessageTaskCount(@Param("messageTypeKey") String messageTypeKey);

}