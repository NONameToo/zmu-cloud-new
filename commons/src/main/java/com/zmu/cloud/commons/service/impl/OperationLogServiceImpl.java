package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.SysOperationLogQuery;
import com.zmu.cloud.commons.entity.admin.SysOperationLog;
import com.zmu.cloud.commons.mapper.SysOperationLogMapper;
import com.zmu.cloud.commons.service.OperationLogService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper,SysOperationLog> implements OperationLogService {

    @Override
    public PageInfo<SysOperationLog> list(SysOperationLogQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        List<SysOperationLog> list = baseMapper.list(query);
        return PageInfo.of(list);
    }
}
