package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.FinanceDataExcel;
import com.zmu.cloud.commons.dto.FinanceIncomeAndExpenditureExcel;
import com.zmu.cloud.commons.dto.FinancialDataQuery;
import com.zmu.cloud.commons.entity.FinancialData;
import com.zmu.cloud.commons.vo.FinancialDataProfitVO;
import com.zmu.cloud.commons.vo.FinancialDataVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FinancialDataMapper extends BaseMapper<FinancialData> {

    List<FinancialDataVO> list(FinancialDataQuery query);

    List<FinancialDataProfitVO> profitAnalysis(@Param("year") Integer year);

    List<FinanceDataExcel> exportDataExcel(FinancialDataQuery query);

    List<FinanceIncomeAndExpenditureExcel> exportIncomeAndExpenditure(FinancialDataQuery query);
}