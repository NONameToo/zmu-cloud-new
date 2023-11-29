package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.vo.PigLaborTaskListVO;

import java.util.List;

/**
 * @author shining
 */
public interface PigLaborTaskService {
    void add();
    PageInfo<PigLaborTaskListVO> page(QueryPig queryPig);

}
