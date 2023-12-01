package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.dto.PigImmuneDTO;
import com.zmu.cloud.commons.dto.QueryPigImmune;
import com.zmu.cloud.commons.entity.PigImmune;
import com.zmu.cloud.commons.vo.PigImmuneVO;

import java.util.List;


public interface PigImmuneService extends IService<PigImmune> {

    /**
     * 免疫列表查询
     * @param queryPigImmune 免疫列表
     * @return 免疫集合
     */
    List<PigImmuneVO> pageQuery(QueryPigImmune queryPigImmune);

    /**
     * 新增免疫
     * @param pigImmuneDTO 新增免疫
     */
    int add(PigImmuneDTO pigImmuneDTO);

    /**
     * 编辑免疫
     * @param pigImmuneDTO 免疫
     */
    int update(PigImmuneDTO pigImmuneDTO);

    /**
     * 删除免疫
     * @param id 免疫id
     */
    int del(Long id);

    /**
     * 免疫详情
     * @param id 免疫id
     * @return 免疫详情
     */
    PigImmuneVO detail(Long id);
}
