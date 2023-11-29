package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryAgingCheck;
import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import com.zmu.cloud.commons.vo.AgingDataPage;
import com.zmu.cloud.commons.vo.DeviceAgingCheckHandleVO;
import com.zmu.cloud.commons.vo.DeviceAgingCheckVO;
import com.zmu.cloud.commons.vo.DeviceCheckHistoryVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;

/**
 * 老化接口
 */
@Transactional(rollbackFor = Exception.class)
public interface DeviceAgingCheckService {

    void bind(String deviceNo, String wifiAccount, String wifiPwd) throws InterruptedException;

    void batchStart(@RequestBody List<Long> ids , Long duration);

    AgingDataPage list(QueryAgingCheck queryAgingCheck);

    PageInfo<DeviceAgingCheck> history(String deviceNum, Date startTime, Date endTime,Integer pageSize,Integer pageNum);

    DeviceAgingCheckHandleVO handle(Long id, Boolean pass, String remark,String aways);
    DeviceAgingCheckHandleVO handleAgingAgain(Long id, String remark,String aways);

    boolean saveOne(DeviceAgingCheck deviceAgingCheck);

    DeviceAgingCheck selectByTd(Long id);

    int updateOne(DeviceAgingCheck deviceAgingCheck);

    List<DeviceAgingCheck> listAll(QueryAgingCheck queryAgingCheck);
}
