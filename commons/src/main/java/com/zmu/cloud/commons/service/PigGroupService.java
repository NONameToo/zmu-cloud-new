package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.entity.PigGroup;
import com.zmu.cloud.commons.vo.PigGroupListVO;

import java.util.List;

public interface PigGroupService {
    List<PigGroupListVO> list(Long pigHouseColumnsId);

    List<PigGroupListVO> listNew(Long pigHouseId);
}
