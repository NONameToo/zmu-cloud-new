package com.zmu.cloud.commons.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.Supplies;
import com.zmu.cloud.commons.mapper.SuppliesMapper;
import com.zmu.cloud.commons.service.ISuppliesService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 疫苗/药品库存Service业务层处理
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SuppliesServiceImpl extends ServiceImpl<SuppliesMapper, Supplies> implements ISuppliesService {

    final SuppliesMapper suppliesMapper;

    /**
     * 查询疫苗/药品库存
     * 
     * @param id 疫苗/药品库存主键
     * @return 疫苗/药品库存
     */
    @Override
    public Supplies selectSuppliesById(Long id)
    {
        return suppliesMapper.selectSuppliesById(id);
    }

    /**
     * 查询疫苗/药品库存列表
     * 
     * @param supplies 疫苗/药品库存
     * @return 疫苗/药品库存
     */
    @Override
    public List<Supplies> selectSuppliesList(Supplies supplies)
    {
        return suppliesMapper.selectSuppliesList(supplies);
    }

    /**
     * 新增疫苗/药品库存
     * 
     * @param supplies 疫苗/药品库存
     * @return 结果
     */
    @Override
    public int insertSupplies(Supplies supplies)
    {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        supplies.setCreateBy(info.getUserId());
        supplies.setUpdateBy(info.getUserId());
        supplies.setCreateTime(new Date());
        supplies.setUpdateTime(new Date());
        supplies.setCompanyId(info.getCompanyId());
        supplies.setPigFarmId(info.getPigFarmId());
        return suppliesMapper.insertSupplies(supplies);
    }

    /**
     * 修改疫苗/药品库存
     * 
     * @param supplies 疫苗/药品库存
     * @return 结果
     */
    @Override
    public int updateSupplies(Supplies supplies)
    {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        supplies.setUpdateTime(new Date());
        supplies.setUpdateBy(info.getUserId());
        return suppliesMapper.updateSupplies(supplies);
    }

    /**
     * 批量删除疫苗/药品库存
     * 
     * @param ids 需要删除的疫苗/药品库存主键
     * @return 结果
     */
    @Override
    public int deleteSuppliesByIds(Long[] ids)
    {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        return suppliesMapper.deleteSuppliesByIds(ids,info.getUserId());
    }

    /**
     * 删除疫苗/药品库存信息
     * 
     * @param id 疫苗/药品库存主键
     * @return 结果
     */
    @Override
    public int deleteSuppliesById(Long id)
    {
        return suppliesMapper.deleteSuppliesById(id);
    }

    @Override
    public Supplies selectSuppliesByNameAndVender(String name, String vender) {
        return suppliesMapper.selectSuppliesByNameAndVender(name,vender);
    }

    @Override
    public List<Supplies> listed(Supplies supplies) {
        return suppliesMapper.listed(supplies);
    }

}
