package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.SaleAfter;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhaojian
 * @create 2023/10/31 10:20
 * @Description
 */
@InterceptorIgnore(tenantLine = "true")
public interface SaleAfterMapper extends BaseMapper<SaleAfter> {

    void batchPass(@Param("ids") Long[] ids);
}
