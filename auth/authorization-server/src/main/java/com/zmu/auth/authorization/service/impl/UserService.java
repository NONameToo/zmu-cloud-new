package com.zmu.auth.authorization.service.impl;

import com.zmu.auth.authorization.entity.User;
import com.zmu.auth.authorization.provider.OrganizationProvider;
import com.zmu.auth.authorization.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private OrganizationProvider organizationProvider;

    @Override
    public User getByUniqueId(String uniqueId) {
        return organizationProvider.getUserByUniqueId(uniqueId).getData();
    }
}
