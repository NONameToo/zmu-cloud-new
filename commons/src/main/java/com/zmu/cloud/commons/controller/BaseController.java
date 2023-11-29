package com.zmu.cloud.commons.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.constant.HttpStatus;
import com.zmu.cloud.commons.dto.PageDomain;
import com.zmu.cloud.commons.dto.TableDataInfo;
import com.zmu.cloud.commons.dto.TableSupport;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.utils.SqlUtil;
import com.zmu.cloud.commons.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author YH
 */
@Controller
@NoArgsConstructor
@AllArgsConstructor
public class BaseController{

    HttpServletRequest request;
    HttpServletResponse response;

    public Long getUserId(){
        return RequestContextUtils.getUserId();
    }

    public Long getCompanyId(){
        return RequestContextUtils.getRequestInfo().getCompanyId();
    }

    public Long getFarmId(){
        return RequestContextUtils.getRequestInfo().getPigFarmId();
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

}
