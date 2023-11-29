package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.SaleAfterQuery;
import com.zmu.cloud.commons.entity.SaleAfter;

import java.text.ParseException;

/**
 * @author zhaojian
 * @create 2023/10/31 10:18
 * @Description 售后
 */
public interface SaleAfterService {

    void add(SaleAfter saleAfter);

    PageInfo<SaleAfter> list(SaleAfterQuery saleAfterQuery) throws ParseException;

    void batchPass(Long[] ids);
}
