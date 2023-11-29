package com.zmu.cloud.commons.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.Supplies;
import com.zmu.cloud.commons.entity.SuppliesAdd;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.SuppliesAddMapper;
import com.zmu.cloud.commons.service.ISuppliesAddService;
import com.zmu.cloud.commons.service.ISuppliesService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 疫苗/药品入库Service业务层处理
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SuppliesAddServiceImpl extends ServiceImpl<SuppliesAddMapper, SuppliesAdd> implements ISuppliesAddService {

    final SuppliesAddMapper suppliesAddMapper;
    final ISuppliesService suppliesService;

    /**
     * 查询疫苗/药品入库
     * 
     * @param id 疫苗/药品入库主键
     * @return 疫苗/药品入库
     */
    @Override
    public SuppliesAdd selectSuppliesAddById(Long id)
    {
        return suppliesAddMapper.selectSuppliesAddById(id);
    }

    /**
     * 查询疫苗/药品入库列表
     * 
     * @param suppliesAdd 疫苗/药品入库
     * @return 疫苗/药品入库
     */
    @Override
    public List<SuppliesAdd> selectSuppliesAddList(SuppliesAdd suppliesAdd)
    {
        return suppliesAddMapper.selectSuppliesAddList(suppliesAdd);
    }

    /**
     * 新增疫苗/药品入库
     * 
     * @param suppliesAdd 疫苗/药品入库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSuppliesAdd(SuppliesAdd suppliesAdd) {
        if (ObjectUtil.isNotNull(suppliesAdd.getName())){
            if (ObjectUtil.isNotNull(suppliesAdd.getSuppliesId())){
                Supplies supplies = suppliesService.selectSuppliesById(suppliesAdd.getSuppliesId());
                supplies.setNum(supplies.getNum() + suppliesAdd.getNum());
                suppliesService.updateSupplies(supplies);
            }else {
                Supplies supplies = suppliesService.selectSuppliesByNameAndVender(suppliesAdd.getName(), suppliesAdd.getVender());
                if (ObjectUtil.isNotNull(supplies)){
                    throw new BaseException("当前饲料品类已存在，请选择已有品类！");
                }else {
                    Supplies sup = new Supplies();
                    BeanUtil.copyProperties(suppliesAdd,sup);
                    suppliesService.insertSupplies(sup);
                }
            }
        }
        RequestInfo info = RequestContextUtils.getRequestInfo();
        suppliesAdd.setCreateBy(info.getUserId());
        suppliesAdd.setUpdateBy(info.getUserId());
        suppliesAdd.setCreateTime(new Date());
        suppliesAdd.setUpdateTime(new Date());
        suppliesAdd.setCompanyId(info.getCompanyId());
        suppliesAdd.setPigFarmId(info.getPigFarmId());
        return suppliesAddMapper.insertSuppliesAdd(suppliesAdd);
    }

    /**
     * 修改疫苗/药品入库
     * 
     * @param suppliesAdd 疫苗/药品入库
     * @return 结果
     */
    @Override
    public int updateSuppliesAdd(SuppliesAdd suppliesAdd)
    {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        suppliesAdd.setUpdateTime(new Date());
        suppliesAdd.setUpdateBy(info.getUserId());
        return suppliesAddMapper.updateSuppliesAdd(suppliesAdd);
    }

    /**
     * 批量删除疫苗/药品入库
     * 
     * @param ids 需要删除的疫苗/药品入库主键
     * @return 结果
     */
    @Override
    public int deleteSuppliesAddByIds(Long[] ids)
    {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        return suppliesAddMapper.deleteSuppliesAddByIds(ids,info.getUserId());
    }

    /**
     * 删除疫苗/药品入库信息
     * 
     * @param id 疫苗/药品入库主键
     * @return 结果
     */
    @Override
    public int deleteSuppliesAddById(Long id)
    {
        return suppliesAddMapper.deleteSuppliesAddById(id);
    }
}
