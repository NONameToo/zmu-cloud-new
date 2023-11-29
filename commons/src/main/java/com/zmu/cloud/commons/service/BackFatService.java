package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.entity.PigBackFat;
import com.zmu.cloud.commons.entity.PigBackFatTask;
import com.zmu.cloud.commons.enums.BackFatTaskStatus;
import com.zmu.cloud.commons.enums.CheckMode;
import com.zmu.cloud.commons.vo.BackFatScanVo;
import com.zmu.cloud.commons.vo.BackFatTaskVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author YH
 */
public interface BackFatService {

    /**
     * 生成背膘记录
     * @param pigId
     * @param backFat
     * @param backFatStage
     * @param operator
     * @return
     */
    @Transactional
    PigBackFat createBackFat(Long pigId, Integer backFat, Integer backFatStage, Long operator);

    /**
     * 最近的背膘记录
     * @param pigId
     * @return
     */
    PigBackFat lastBackFat(Long pigId);

}
