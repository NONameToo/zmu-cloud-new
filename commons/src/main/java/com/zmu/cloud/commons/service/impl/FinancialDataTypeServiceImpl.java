package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.FinancialDataTypeDTO;
import com.zmu.cloud.commons.entity.FinancialDataType;
import com.zmu.cloud.commons.mapper.FinancialDataTypeMapper;
import com.zmu.cloud.commons.service.FinancialDataTypeService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/28 11:14
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialDataTypeServiceImpl extends ServiceImpl<FinancialDataTypeMapper, FinancialDataType> implements FinancialDataTypeService {

    @Override
    public void initByCompanyCreated(Long companyId) {
        LambdaQueryWrapper<FinancialDataType> wrapper = new LambdaQueryWrapper<>();
        // companyId=0 的是基础数据
        wrapper.eq(FinancialDataType::getCompanyId, 0);
        List<FinancialDataType> list = baseMapper.selectList(wrapper);
        List<FinancialDataType> collect = list.stream().peek(sysProductionTips -> {
            sysProductionTips.setId(null);
            sysProductionTips.setCompanyId(companyId);
        }).collect(Collectors.toList());
        saveBatch(collect);
        log.info("创建公司时给公司初始化默认的财务数据类型，size={}", collect.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(FinancialDataTypeDTO financialDataTypeDTO) {
        FinancialDataType financialDataType = BeanUtil.copyProperties(financialDataTypeDTO, FinancialDataType.class);
        financialDataType.setCreateBy(RequestContextUtils.getUserId());
        save(financialDataType);
        return financialDataType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FinancialDataTypeDTO financialDataTypeDTO) {
        FinancialDataType financialDataType = BeanUtil.copyProperties(financialDataTypeDTO, FinancialDataType.class);
        financialDataType.setUpdateBy(RequestContextUtils.getUserId());
        super.updateById(financialDataType);
    }

    @Override
    public List<FinancialDataType> list() {
        return super.list();
    }
}
