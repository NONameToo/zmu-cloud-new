package com.zmu.cloud.commons.service.impl;

import com.zmu.cloud.commons.mapper.PigMatingMapper;
import com.zmu.cloud.commons.mapper.PigWeanedTaskMapper;
import com.zmu.cloud.commons.service.IndexService;
import com.zmu.cloud.commons.service.PigPorkStockService;
import com.zmu.cloud.commons.service.PigPregnancyTaskService;
import com.zmu.cloud.commons.vo.ProductionTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {

    final PigMatingMapper pigMatingMapper;
    final PigPorkStockService pigPorkStockService;
    @Override
    public ProductionTaskVO task() {
        ProductionTaskVO productionTaskVO =   pigMatingMapper.selectProductionTask();
        productionTaskVO.setGoOut(pigPorkStockService.wantGoOutCount());
        return productionTaskVO;
    }
}
