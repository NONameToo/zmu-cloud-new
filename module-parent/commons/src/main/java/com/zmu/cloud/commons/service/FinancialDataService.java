package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.FinanceDataExcel;
import com.zmu.cloud.commons.dto.FinanceIncomeAndExpenditureExcel;
import com.zmu.cloud.commons.dto.FinancialDataQuery;
import com.zmu.cloud.commons.vo.FinancialDataProfitVO;
import com.zmu.cloud.commons.vo.FinancialDataVO;

import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @date 2022/5/2 20:29
 **/
public interface FinancialDataService {

    PageInfo<FinancialDataVO> list(FinancialDataQuery query);

    List<FinancialDataProfitVO> profitAnalysis(Integer year);

    List<FinanceIncomeAndExpenditureExcel> exportIncomeAndExpenditure(FinancialDataQuery query);

    List<FinanceDataExcel> exportDataExcel(FinancialDataQuery query);
}
