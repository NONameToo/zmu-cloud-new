package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.PigGroup;
import com.zmu.cloud.commons.vo.PigGroupListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PigGroupMapper extends BaseMapper<PigGroup> {
    Integer selectCountByColumnsId(Long pigHouseColumnsId);

    List<PigGroupListVO> list(@Param("pigHouseColumnsId") Long pigHouseColumnsId);

    Integer selectCountByHouseId(Long pigHouseId);

    List<PigGroupListVO> listNew(@Param("pigHouseId")Long pigHouseId);
}