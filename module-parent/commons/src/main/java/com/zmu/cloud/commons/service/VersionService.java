package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.commons.Page;
import com.zmu.cloud.commons.entity.Version;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;

public interface VersionService {

    Version check(UserClientTypeEnum clientTypeEnum);

    Long add(Version version);

    Version getById(Long id);

    boolean update(Version version);

    PageInfo<Version> list(Page page);
}
