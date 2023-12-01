package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.PigPreventionCureDTO;
import com.zmu.cloud.commons.dto.QueryPigPreventionCure;
import com.zmu.cloud.commons.entity.PigPreventionCure;
import com.zmu.cloud.commons.vo.PigPreventionCureVO;

import java.util.List;


public interface PigPreventionCureMapper extends BaseMapper<PigPreventionCure> {

    /**
     * 防治列表
     * @param queryPigPreventionCure 防治列表
     * @return 防治列表集合
     */
    List<PigPreventionCureVO> pageQuery(QueryPigPreventionCure queryPigPreventionCure);

    /**
     * 防治详情
     * @param id 防治id
     * @return 防治详情
     */
    PigPreventionCureVO detail(Long id);

    /**
     * 新增防治
     * @param pigPreventionCureDTO 防治
     */
    int add(PigPreventionCureDTO pigPreventionCureDTO);

    /**
     * 修改防治
     * @param pigPreventionCureDTO 防治
     */
    int update(PigPreventionCureDTO pigPreventionCureDTO);

    /**
     * 删除防治
     * @param id 防治id
     */
    int del(Long id);
}