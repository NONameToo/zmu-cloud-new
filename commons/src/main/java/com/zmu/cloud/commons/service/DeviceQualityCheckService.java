package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryDeviceCheck;
import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import com.zmu.cloud.commons.entity.DeviceQualityCheck;
import com.zmu.cloud.commons.vo.DeviceCheckHandleVO;
import com.zmu.cloud.commons.vo.DeviceCheckHistoryVO;
import com.zmu.cloud.commons.vo.DeviceQualityCheckVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;


@Transactional(rollbackFor = Exception.class)
public interface DeviceQualityCheckService {

    void bind(String deviceNo, String wifiAccount, String wifiPwd) throws InterruptedException;

    void batchStart(@RequestBody List<Long> ids);

    List<DeviceQualityCheckVO> list(QueryDeviceCheck queryDeviceCheck);

    PageInfo<DeviceCheckHistoryVO> history(String deviceNum, Date startTime, Date endTime, Integer pageSize, Integer pageNum);

    DeviceQualityCheck handle(Long id, Boolean pass, String remark);

    DeviceCheckHandleVO passQuality(Long id, Boolean pass, String remark);
    DeviceCheckHandleVO goOut(Long id, String remark);

    DeviceQualityCheck selectById(Long qualityId);

    int saveOne(DeviceQualityCheck deviceQualityCheck);
}
