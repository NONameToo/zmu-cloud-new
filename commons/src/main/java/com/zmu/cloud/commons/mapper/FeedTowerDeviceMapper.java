package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.FeedTowerDevice;
import org.apache.ibatis.annotations.Param;

@InterceptorIgnore(tenantLine = "true")
public interface FeedTowerDeviceMapper extends BaseMapper<FeedTowerDevice> {

    void unBindFeedTower(String deviceNo);

    String getTowerNameByTowerId(Long towerId);

    int chooseNetMode(@Param("deviceNo") String deviceNo, @Param("netMode") Long netMode);

    Long selectNetModeByDeviceNo(String deviceNo);
}