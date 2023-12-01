package com.zmu.cloud.commons.service;


import com.zmu.cloud.commons.entity.Printer;
import com.zmu.cloud.commons.vo.PrinterVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(rollbackFor = Exception.class)
public interface PrinterService {
    List<PrinterVO> list();

    List<PrinterVO> setDefaultPrinter(Long printerId);

    Printer save(Printer printer);

    void del(Long carId);

    Printer myDefaultPrinter();
}
