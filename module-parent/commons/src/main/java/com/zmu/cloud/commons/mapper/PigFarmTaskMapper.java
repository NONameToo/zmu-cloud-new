package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.PigFarmTask;import com.zmu.cloud.commons.vo.PigHouseTypeVo;import java.util.List;

public interface PigFarmTaskMapper extends BaseMapper<PigFarmTask> {
    List<PigHouseTypeVo> feedingHouseTypes(Long farmId);
}