package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.FinancialDataTypeDTO;
import com.zmu.cloud.commons.entity.FinancialDataType;

import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/28 11:00
 **/
public interface FinancialDataTypeService {

    void initByCompanyCreated(Long companyId);

    Long add(FinancialDataTypeDTO financialDataTypeDTO);

    void update(FinancialDataTypeDTO financialDataTypeDTO);

    List<FinancialDataType> list();
}
