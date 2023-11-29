package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.vo.FarmTowerDeviceVo;
import com.zmu.cloud.commons.vo.TowerDetailLogVo;
import com.zmu.cloud.commons.vo.TowerExceptionViewVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface FeedTowerMapper extends BaseMapper<FeedTower> {

    @InterceptorIgnore(tenantLine = "true")
    List<FeedTower> findByCompanyId(@Param("companyId") Long companyId, @Param("feedTypeId") Long feedTypeId);

    @InterceptorIgnore(tenantLine = "true")
    void updateFeedTypeById(@Param("towerId") Long towerId, @Param("feedTypeId") Long feedTypeId,
                                       @Param("feedType") String feedType, @Param("density") Long density,@Param("residualWeight")Long residualWeight);

    @InterceptorIgnore(tenantLine = "true")
    List<FeedTower> findTowerByFarmIdAndDeviceNo(@Param("farmId") Long farmId,@Param("deviceNo") String deviceNo);

    @InterceptorIgnore(tenantLine = "true")
    Long getCountByFarmId(Long farmId);

    @InterceptorIgnore(tenantLine = "true")
    List<FeedTower> getybdByFarmId(Long farmId);
    @InterceptorIgnore(tenantLine = "true")
    List<FeedTower> getywbdByFarmId(Long farmId);

    @InterceptorIgnore(tenantLine = "true")
    void unBindFeedTower(@Param("deviceNo") String deviceNo,@Param("isReset") Integer isReset);

    @InterceptorIgnore(tenantLine = "true")
    List<TowerExceptionViewVo> exceptionView(@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("start")Integer start,@Param("end")Integer end);

    @InterceptorIgnore(tenantLine = "true")
    Long exceptionViewConut(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    @InterceptorIgnore(tenantLine = "true")
    List<TowerDetailLogVo> selectTowerDetails(@Param("deviceNo")String deviceNo,@Param("farmId")Long farmId);

    @InterceptorIgnore(tenantLine = "true")
    List<TowerDetailLogVo> selectTowerDetailsByDate(@Param("deviceNo")String deviceNo,@Param("farmId")Long farmId,@Param("startTime") String startTime,@Param("endTime") String endTime);

    @InterceptorIgnore(tenantLine = "true")
    void updateTowerById(FeedTower tower);

    @InterceptorIgnore(tenantLine = "true")
    void updateTowerAllInfoByIdIn(FeedTower tower);


    @InterceptorIgnore(tenantLine = "true")
    FeedTower selectTowerById(Long id);
    @InterceptorIgnore(tenantLine = "true")
    List<FeedTower> listIn(@Param("deviceNo") String deviceNo,@Param("name") String name,@Param("farmId") Long farmId);

    @InterceptorIgnore(tenantLine = "true")
    FeedTower selectByIdIn(Long towerId);

    @InterceptorIgnore(tenantLine = "true")
    FarmTowerDeviceVo deviceInfoByNo(String deviceNo);
}