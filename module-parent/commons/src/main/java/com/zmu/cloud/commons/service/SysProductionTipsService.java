package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.dto.commons.Page;
import com.zmu.cloud.commons.entity.SysProductionTips;
import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/24 11:58
 **/
public interface SysProductionTipsService {

    void initByCompanyCreated(Long companyId);

    PageInfo<SysProductionTips> list(BaseQuery query);

    void update(SysProductionTips sysProductionTips);

    int getWantGoOutDays(Long companyId);
}
