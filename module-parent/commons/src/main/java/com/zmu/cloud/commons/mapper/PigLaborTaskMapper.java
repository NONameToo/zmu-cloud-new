package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigLaborTask;
import com.zmu.cloud.commons.vo.PigLaborTaskListVO;
import com.zmu.cloud.commons.vo.PushMessageTaskCountVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PigLaborTaskMapper extends BaseMapper<PigLaborTask> {
    PigLaborTask selectByPigBreedingId(Long pigBreedingId);

    List<PigLaborTaskListVO> page(QueryPig queryPig);

    @InterceptorIgnore(tenantLine = "true")
    List<PushMessageTaskCountVO> selectPushMessageTaskCount(@Param("messageTypeKey") String messageTypeKey);

}