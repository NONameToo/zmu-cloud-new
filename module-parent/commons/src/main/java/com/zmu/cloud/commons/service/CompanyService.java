package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.CompanyDTO;
import com.zmu.cloud.commons.dto.admin.CompanyQuery;
import com.zmu.cloud.commons.vo.CompanyVO;

import javax.validation.constraints.Future;
import java.util.List;
import java.util.Set;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 10:44
 **/
public interface CompanyService {

    Long add(CompanyDTO c);

    boolean update(CompanyDTO c);

    boolean delete(Long companyId);

    CompanyVO get(Long companyId);

    PageInfo<CompanyVO> list(CompanyQuery q);

    List<CompanyVO> listAndFarms(CompanyQuery q);

    Set<Long> companyFarms(Long companyId);
}
