package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.SysOperationLogQuery;
import com.zmu.cloud.commons.entity.admin.SysOperationLog;

public interface OperationLogService {

    PageInfo<SysOperationLog> list(SysOperationLogQuery query);
}
