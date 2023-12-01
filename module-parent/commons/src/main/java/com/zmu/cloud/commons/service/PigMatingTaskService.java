package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigMatingTask;
import com.zmu.cloud.commons.vo.PigMatingTaskListVO;

/**
 * @author shining
 */
public interface PigMatingTaskService {
    void add();

    PageInfo<PigMatingTaskListVO> page(QueryPig queryPig);
}
