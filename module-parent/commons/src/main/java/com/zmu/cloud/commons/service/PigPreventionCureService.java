package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.dto.PigImmuneDTO;
import com.zmu.cloud.commons.dto.PigPreventionCureDTO;
import com.zmu.cloud.commons.dto.QueryPigImmune;
import com.zmu.cloud.commons.dto.QueryPigPreventionCure;
import com.zmu.cloud.commons.entity.PigImmune;
import com.zmu.cloud.commons.entity.PigPreventionCure;
import com.zmu.cloud.commons.vo.PigImmuneVO;
import com.zmu.cloud.commons.vo.PigPreventionCureVO;

import java.util.List;


public interface PigPreventionCureService extends IService<PigPreventionCure> {

    /**
     * 防治列表查询
     * @param queryPigPreventionCure 防治列表
     * @return 防治集合
     */
    List<PigPreventionCureVO> pageQuery(QueryPigPreventionCure queryPigPreventionCure);

    /**
     * 新增防治
     * @param pigPreventionCureDTO 新增防治
     */
    int add(PigPreventionCureDTO pigPreventionCureDTO);

    /**
     * 编辑防治
     * @param pigPreventionCureDTO 编辑防治
     */
    int update(PigPreventionCureDTO pigPreventionCureDTO);

    /**
     * 删除防治
     * @param id 防治id
     */
    int del(Long id);

    /**
     * 防治详情
     * @param id 防治id
     * @return 防治详情
     */
    PigPreventionCureVO detail(Long id);
}
