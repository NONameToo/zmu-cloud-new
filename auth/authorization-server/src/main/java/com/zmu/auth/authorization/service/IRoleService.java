package com.zmu.auth.authorization.service;

import com.zmu.auth.authorization.entity.Role;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface IRoleService {

    Set<Role> queryUserRolesByUserId(String userId);

}
