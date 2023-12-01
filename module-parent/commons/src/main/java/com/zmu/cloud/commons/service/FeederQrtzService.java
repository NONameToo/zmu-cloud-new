package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.FeederTaskParam;
import com.zmu.cloud.commons.entity.FeederQrtz;
import com.zmu.cloud.commons.vo.FeederTaskVo;
import com.zmu.cloud.commons.vo.PigHouseTypeVo;

import java.util.List;
import java.util.Set;

/**
 * 饲喂任务实际控制接口
 * @author YH
 */
public interface FeederQrtzService {

    void addFeederQrtz(FeederTaskParam param, String taskName, String taskGroup);
    List<FeederTaskVo> findQrtzByHouseType(Integer houseType, boolean feedAgain);
    FeederQrtz findQrtzByBlendFeed();
    Set<PigHouseTypeVo> feederHouseTypes();
    void enable(Long id, Integer enable);
    void delete(Long id);
    void checkLineTasks(Long qrtzId, Long houseId, Long materialLineId, String time);
}
