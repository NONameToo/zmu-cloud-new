package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.PigPreventionCureDTO;
import com.zmu.cloud.commons.dto.QueryPigPreventionCure;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.PigPreventionCure;
import com.zmu.cloud.commons.mapper.PigPreventionCureMapper;
import com.zmu.cloud.commons.service.PigPreventionCureService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.PigPreventionCureVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PigPreventionCureServiceImpl extends ServiceImpl<PigPreventionCureMapper, PigPreventionCure> implements PigPreventionCureService {

    final PigPreventionCureMapper pigPreventionCureMapper;

    /**
     * 防治列表查询
     *
     * @param queryPigPreventionCure 防治列表
     * @return 防治列表
     */
    @Override
    public List<PigPreventionCureVO> pageQuery(QueryPigPreventionCure queryPigPreventionCure) {
        return pigPreventionCureMapper.pageQuery(queryPigPreventionCure);
    }

    /**
     * 新增防治
     *
     * @param pigPreventionCureDTO 新增防治
     */
    @Override
    public int add(PigPreventionCureDTO pigPreventionCureDTO) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        pigPreventionCureDTO.setCreateBy(info.getUserId() );
        pigPreventionCureDTO.setUpdateBy(info.getUserId() );
        pigPreventionCureDTO.setCreateTime(new Date());
        pigPreventionCureDTO.setUpdateTime(new Date());
        pigPreventionCureDTO.setPigFarmId(info.getPigFarmId() );
        pigPreventionCureDTO.setCompanyId(info.getCompanyId() );
        return pigPreventionCureMapper.add(pigPreventionCureDTO);
    }

    /**
     * 修改防治
     *
     * @param pigPreventionCureDTO 修改防治
     */
    @Override
    public int update(PigPreventionCureDTO pigPreventionCureDTO) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        pigPreventionCureDTO.setUpdateBy(info.getUserId());
        pigPreventionCureDTO.setUpdateTime(new Date());
        return pigPreventionCureMapper.update(pigPreventionCureDTO);
    }

    /**
     * 删除防治
     *
     * @param id 防治id
     */
    @Override
    public int del(Long id) {
        return pigPreventionCureMapper.del(id);
    }

    /**
     * 防治详情
     *
     * @param id 防治id
     * @return 防治详情
     */
    @Override
    public PigPreventionCureVO detail(Long id) {
        return pigPreventionCureMapper.detail(id);
    }
}
