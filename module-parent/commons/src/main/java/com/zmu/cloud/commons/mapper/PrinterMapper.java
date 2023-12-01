package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.Printer;
import com.zmu.cloud.commons.vo.PrinterVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrinterMapper extends BaseMapper<Printer> {
    List<PrinterVO> listPinterByUser(@Param("userId") Long userId );

    int deleteByUserId(@Param("userId") Long userId);
}