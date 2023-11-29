package com.zmu.cloud.auth.authentication.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.zmu.cloud.auth.authentication.provider.ResourceProvider;
import com.zmu.cloud.auth.authentication.service.IGroupService;
import com.zmu.cloud.common.core.entity.vo.Result;
import com.zmu.cloud.sysadmin.facade.dto.GroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupService implements IGroupService {

    @Qualifier("com.zmu.cloud.auth.authentication.provider.ResourceProvider")
    @Autowired
    ResourceProvider resourceProvider;

    @Override
    @Cached(name = "group4User::", key = "#username", cacheType = CacheType.BOTH, expire = 5)
    public List<GroupDTO> queryGroupsByUsername(String username) {
        Result<List<GroupDTO>> groups = resourceProvider.groups(username);
        log.info("username:{},groups:{}", username, groups);
        return groups.getData();
    }
}
