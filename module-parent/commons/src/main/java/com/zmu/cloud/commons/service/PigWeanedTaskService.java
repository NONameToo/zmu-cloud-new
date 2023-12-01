package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.vo.PigWeanedTaskListVO;

public interface PigWeanedTaskService {
    void add();
    PageInfo<PigWeanedTaskListVO> page(QueryPig queryPig);
}
