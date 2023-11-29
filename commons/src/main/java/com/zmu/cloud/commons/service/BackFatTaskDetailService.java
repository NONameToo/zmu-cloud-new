package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.entity.ManualFeedingRecord;
import com.zmu.cloud.commons.entity.PigBackFatTaskDetail;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.enums.BackFatTaskDetailStatus;

import java.util.List;

public interface BackFatTaskDetailService {

    void saveBatch(List<PigBackFatTaskDetail> details);

    /**
     * 获取任务明细
     * @param taskId
     * @param col
     * @param status
     * @return
     */
    PigBackFatTaskDetail findListByColId(Long taskId, PigHouseColumns col, BackFatTaskDetailStatus status);

    /**
     * 查找未处理的任务明细
     * @param taskId
     * @return
     */
    String listByUndetectedField(Long taskId);

    /**
     * 获取任务明细
     * @param taskId
     * @param colId
     * @param status
     * @param page
     * @param size
     * @return
     */
    List<PigBackFatTaskDetail> findList(Long taskId, Long colId, BackFatTaskDetailStatus status,
                                                 Integer page, Integer size);

}
