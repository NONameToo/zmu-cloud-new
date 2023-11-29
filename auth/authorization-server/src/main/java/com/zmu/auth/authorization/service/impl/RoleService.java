package com.zmu.auth.authorization.service.impl;

import com.zmu.auth.authorization.entity.Role;
import com.zmu.auth.authorization.provider.OrganizationProvider;
import com.zmu.auth.authorization.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private OrganizationProvider organizationProvider;

    @Override
    public Set<Role> queryUserRolesByUserId(String userId) {
        return organizationProvider.queryRolesByUserId(userId).getData();
    }

}
