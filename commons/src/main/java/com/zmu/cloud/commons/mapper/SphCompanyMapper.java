package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.SphCompany;

import java.util.List;

public interface SphCompanyMapper extends BaseMapper<SphCompany> {

    void sync(List<SphCompany> companies);
}