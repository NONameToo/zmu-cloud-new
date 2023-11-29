package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.FinanceDataExcel;
import com.zmu.cloud.commons.dto.FinanceIncomeAndExpenditureExcel;
import com.zmu.cloud.commons.dto.FinancialDataQuery;
import com.zmu.cloud.commons.entity.FinancialData;
import com.zmu.cloud.commons.mapper.FinancialDataMapper;
import com.zmu.cloud.commons.service.FinancialDataService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.FinancialDataProfitVO;
import com.zmu.cloud.commons.vo.FinancialDataVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author lqp0817@gmail.com
 * @date 2022/5/2 20:33
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialDataServiceImpl extends ServiceImpl<FinancialDataMapper, FinancialData> implements FinancialDataService {

    @Override
    public PageInfo<FinancialDataVO> list(FinancialDataQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(baseMapper.list(query));
    }

    @Override
    public List<FinancialDataProfitVO> profitAnalysis(Integer year) {
        List<FinancialDataProfitVO> list = IntStream.range(1, 13).boxed()
                .map(month -> FinancialDataProfitVO.builder().build())
                .collect(Collectors.toList());
        List<FinancialDataProfitVO> vos = baseMapper.profitAnalysis(year);
        vos.forEach(v -> list.set(v.getMonth() - 1, v));
        return list;
    }

    @Override
    public List<FinanceIncomeAndExpenditureExcel> exportIncomeAndExpenditure(FinancialDataQuery query) {
        List<FinanceIncomeAndExpenditureExcel> list = baseMapper.exportIncomeAndExpenditure(query);
        Set<Long> ids = list.stream().filter(f -> ObjectUtil.equals(0, f.getStatus())).map(FinanceIncomeAndExpenditureExcel::getId).collect(Collectors.toSet());
        updateExportStatus(ids);
        return list;
    }

    @Override
    public List<FinanceDataExcel> exportDataExcel(FinancialDataQuery query) {
        List<FinanceDataExcel> list = baseMapper.exportDataExcel(query);
        Set<Long> ids = list.stream().filter(f -> ObjectUtil.equals(0, f.getStatus())).map(FinanceDataExcel::getId).collect(Collectors.toSet());
        updateExportStatus(ids);
        return list;
    }

    private void updateExportStatus(Set<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            LambdaUpdateWrapper<FinancialData> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(FinancialData::getUpdateBy, RequestContextUtils.getUserId())
                    .set(FinancialData::getStatus, 1)
                    .in(FinancialData::getId, ids);
            update(updateWrapper);
        }
    }
}
