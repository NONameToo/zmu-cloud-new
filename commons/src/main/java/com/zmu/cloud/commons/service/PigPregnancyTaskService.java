package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigMatingTask;
import com.zmu.cloud.commons.entity.PigPregnancyTask;
import com.zmu.cloud.commons.vo.PigMatingTaskListVO;
import com.zmu.cloud.commons.vo.PigPregnancyTaskListVO;

/**
 * @author shining
 */
public interface PigPregnancyTaskService {
    void add();

    PageInfo<PigPregnancyTaskListVO> page(QueryPig queryPig);
}
