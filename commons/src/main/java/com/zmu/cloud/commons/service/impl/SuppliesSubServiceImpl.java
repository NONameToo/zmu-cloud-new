package com.zmu.cloud.commons.service.impl;

import java.util.Date;
import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.Supplies;
import com.zmu.cloud.commons.entity.SuppliesSub;
import com.zmu.cloud.commons.mapper.SuppliesSubMapper;
import com.zmu.cloud.commons.service.ISuppliesService;
import com.zmu.cloud.commons.service.ISuppliesSubService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 疫苗/药品出库Service业务层处理
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SuppliesSubServiceImpl extends ServiceImpl<SuppliesSubMapper, SuppliesSub> implements ISuppliesSubService{

    final SuppliesSubMapper suppliesSubMapper;
    final ISuppliesService suppliesService;

    /**
     * 查询疫苗/药品出库
     * 
     * @param id 疫苗/药品出库主键
     * @return 疫苗/药品出库
     */
    @Override
    public SuppliesSub selectSuppliesSubById(Long id)
    {
        return suppliesSubMapper.selectSuppliesSubById(id);
    }

    /**
     * 查询疫苗/药品出库列表
     * 
     * @param suppliesSub 疫苗/药品出库
     * @return 疫苗/药品出库
     */
    @Override
    public List<SuppliesSub> selectSuppliesSubList(SuppliesSub suppliesSub)
    {
        return suppliesSubMapper.selectSuppliesSubList(suppliesSub);
    }

    /**
     * 新增疫苗/药品出库
     * 
     * @param suppliesSub 疫苗/药品出库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSuppliesSub(SuppliesSub suppliesSub)
    {
        if (ObjectUtil.isNotNull(suppliesSub.getSuppliesId())){
            Supplies supplies = suppliesService.selectSuppliesById(suppliesSub.getSuppliesId());
            supplies.setNum(supplies.getNum() - suppliesSub.getNum());
            suppliesService.updateSupplies(supplies);
        }
        RequestInfo info = RequestContextUtils.getRequestInfo();
        suppliesSub.setCreateBy(info.getUserId());
        suppliesSub.setUpdateBy(info.getUserId());
        suppliesSub.setCreateTime(new Date());
        suppliesSub.setUpdateTime(new Date());
        suppliesSub.setCompanyId(info.getCompanyId());
        suppliesSub.setPigFarmId(info.getPigFarmId());
        return suppliesSubMapper.insertSuppliesSub(suppliesSub);
    }

    /**
     * 修改疫苗/药品出库
     * 
     * @param suppliesSub 疫苗/药品出库
     * @return 结果
     */
    @Override
    public int updateSuppliesSub(SuppliesSub suppliesSub)
    {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        suppliesSub.setUpdateTime(new Date());
        suppliesSub.setUpdateBy(info.getUserId());
        return suppliesSubMapper.updateSuppliesSub(suppliesSub);
    }

    /**
     * 批量删除疫苗/药品出库
     * 
     * @param ids 需要删除的疫苗/药品出库主键
     * @return 结果
     */
    @Override
    public int deleteSuppliesSubByIds(Long[] ids)
    {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        return suppliesSubMapper.deleteSuppliesSubByIds(ids,info.getUserId());
    }

    /**
     * 删除疫苗/药品出库信息
     * 
     * @param id 疫苗/药品出库主键
     * @return 结果
     */
    @Override
    public int deleteSuppliesSubById(Long id)
    {
        return suppliesSubMapper.deleteSuppliesSubById(id);
    }
}
