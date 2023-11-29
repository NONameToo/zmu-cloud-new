package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.PigImmuneDTO;
import com.zmu.cloud.commons.dto.QueryPigImmune;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.PigImmune;
import com.zmu.cloud.commons.mapper.PigImmuneMapper;
import com.zmu.cloud.commons.service.PigImmuneService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.PigImmuneVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PigImmuneServiceImpl extends ServiceImpl<PigImmuneMapper, PigImmune> implements PigImmuneService {

    final PigImmuneMapper pigImmuneMapper;

    /**
     * 免疫列表查询
     *
     * @param queryPigImmune 免疫列表
     * @return 免疫列表
     */
    @Override
    public List<PigImmuneVO> pageQuery(QueryPigImmune queryPigImmune) {
        return pigImmuneMapper.pageQuery(queryPigImmune);
    }

    /**
     * 新增免疫
     *
     * @param pigImmuneDTO 新增免疫
     */
    @Override
    public int add(PigImmuneDTO pigImmuneDTO) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        pigImmuneDTO.setCreateBy( info.getUserId() );
        pigImmuneDTO.setUpdateBy(info.getUserId()  );
        pigImmuneDTO.setCreateTime(new Date());
        pigImmuneDTO.setUpdateTime(new Date());
        pigImmuneDTO.setPigFarmId(info.getPigFarmId() );
        pigImmuneDTO.setCompanyId(info.getCompanyId() );
        return pigImmuneMapper.add(pigImmuneDTO);
    }

    /**
     * 修改免疫
     *
     * @param pigImmuneDTO 修改免疫
     */
    @Override
    public int update(PigImmuneDTO pigImmuneDTO) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        pigImmuneDTO.setUpdateBy(info.getUserId()  );
        pigImmuneDTO.setUpdateTime(new Date());
        return pigImmuneMapper.update(pigImmuneDTO);
    }

    /**
     * 删除免疫
     *
     * @param id 免疫id
     */
    @Override
    public int del(Long id) {
        return pigImmuneMapper.del(id);
    }

    /**
     * 免疫详情
     *
     * @param id 免疫id
     * @return 免疫详情
     */
    @Override
    public PigImmuneVO detail(Long id) {
        return pigImmuneMapper.detail(id);
    }
}
