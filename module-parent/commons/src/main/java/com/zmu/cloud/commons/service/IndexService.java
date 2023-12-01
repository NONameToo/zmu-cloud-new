package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.vo.ProductionTaskVO;
import org.springframework.stereotype.Service;

@Service
public interface IndexService {
    ProductionTaskVO task();
}
