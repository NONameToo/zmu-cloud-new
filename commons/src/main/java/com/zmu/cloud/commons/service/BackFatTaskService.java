package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.BackFatDto;
import com.zmu.cloud.commons.entity.PigBackFatTask;
import com.zmu.cloud.commons.enums.BackFatTaskStatus;
import com.zmu.cloud.commons.enums.CheckMode;
import com.zmu.cloud.commons.vo.BackFatScanVo;
import com.zmu.cloud.commons.vo.BackFatTaskReportVo;
import com.zmu.cloud.commons.vo.BackFatTaskVo;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author YH
 */
public interface BackFatTaskService {

    /**
     * 单个扫码记录背膘
     * @param content
     */
    BackFatScanVo single(String content);

    /**
     * 任务提示
     * @return
     */
    BackFatTaskVo tip();

    /**
     * 只会存在一个进行中的任务
     * @param farmId
     * @param mode
     * @param statuses
     * @return
     */
    List<PigBackFatTask> findTaskByFarmAndModel(Long farmId, CheckMode mode, List<BackFatTaskStatus> statuses);

    /**
     * 生成背膘测试任务
     * @param colId
     * @param endPosition
     * @return
     */
    @Transactional
    List<BackFatScanVo> batchTask(Long colId, String endPosition);

    /**
     * 保存背膘数据
     * @param backFatDto
     */
    @Transactional
    void save(BackFatDto backFatDto, HttpServletRequest request);

    /**
     * 测膘报告
     * @param taskId
     * @return
     */
    BackFatTaskReportVo report(Long taskId);
}
