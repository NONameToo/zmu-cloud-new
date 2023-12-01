package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.PigFarmQuery;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.vo.FarmDetail;
import com.zmu.cloud.commons.vo.FarmStatisticPigHouseColumnsVO;import com.zmu.cloud.commons.vo.PigFarmVO;
import com.zmu.cloud.commons.vo.TowerFarmLogVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;import java.util.Set;

public interface PigFarmMapper extends BaseMapper<PigFarm> {
    PigFarmVO get(@Param("id") Long id, @Param("userId") Long userId);

    List<PigFarmVO> list(PigFarmQuery pigFarmQuery);

    Set<Long> listUserPigFarmIdsAdmin();

    List<PigFarmVO> listByIds(@Param("ids") Set<Long> ids, @Param("userId") Long userId);

    List<PigFarmVO> listPigFarmVOsByUserId(@Param("userId") Long userId);

    List<FarmStatisticPigHouseColumnsVO> statisticPigHouseColumns();

    Set<Long> listPigFarmIds();

    @InterceptorIgnore(tenantLine = "true")
    PigFarm byId(Long farmId);


    @InterceptorIgnore(tenantLine = "true")
    List<PigFarm> selectFarmByName(@Param("name") String name);

    @InterceptorIgnore(tenantLine = "true")
    List<FarmDetail>  selectFarmsIds(@Param("list") Set<Long> farms);

    @InterceptorIgnore(tenantLine = "true")
    List<TowerFarmLogVo> selectFarmsByName(String farmName);

    @InterceptorIgnore(tenantLine = "true")
    List<PigFarm> listIn(@Param("deviceNo") String deviceNo,@Param("name") String name,@Param("start") Integer start,@Param("end") Integer end);

    @InterceptorIgnore(tenantLine = "true")
    Long listInCount(@Param("deviceNo") String deviceNo,@Param("name") String name);
}