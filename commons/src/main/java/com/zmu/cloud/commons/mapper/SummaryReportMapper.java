package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.SummaryReportDto;
import com.zmu.cloud.commons.entity.SummaryReport;

import java.util.List;

/**
 * @author YH
 */
public interface SummaryReportMapper extends BaseMapper<SummaryReport> {

    List<SummaryReportDto> findFeederCount();

}