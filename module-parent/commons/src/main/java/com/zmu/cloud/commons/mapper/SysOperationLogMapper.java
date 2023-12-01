package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.admin.SysOperationLogQuery;
import com.zmu.cloud.commons.entity.admin.SysOperationLog;
import java.util.List;

public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    List<SysOperationLog> list(SysOperationLogQuery query);
}