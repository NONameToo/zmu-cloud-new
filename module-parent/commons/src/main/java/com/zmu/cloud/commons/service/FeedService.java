package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.BaseFeedingDTO;
import com.zmu.cloud.commons.entity.FeederQrtz;

import java.util.List;
import java.util.Map;

/**
 * @author YH
 */
public interface FeedService {

    /**
     * 手动下料
     * @param amount 饲喂量
     */
    void manualFeeding(Integer amount);

    /**
     * 自动饲喂数据收集
     */
    Map<Long, List<BaseFeedingDTO>> autoFeedingDataCapture(List<FeederQrtz> qrtzs);

}
