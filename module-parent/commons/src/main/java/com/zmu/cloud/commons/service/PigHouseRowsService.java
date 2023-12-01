package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.PigHouseRowsDTO;
import com.zmu.cloud.commons.entity.PigHouseRows;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.vo.ViewRowVo;

import java.util.List;

public interface PigHouseRowsService {

    List<PigHouseRows> list(Long houseId,boolean tree);

//    List<ViewRowVo> viewHouseRows(Long houseId, CacheKey.Web operationType);

    Long add(PigHouseRowsDTO dto);

    void update(PigHouseRowsDTO dto);

    void delete(Long id);

    List<PigHouseRows> saveBatch(List<PigHouseRows> pigHouseRows);

    PigHouseRows get(Long pigHouseRowsId);

    PigHouseRows findByCache(Long rowId);
}
