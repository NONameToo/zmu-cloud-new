package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.UserPasswordUpdateDTO;
import com.zmu.cloud.commons.dto.admin.LoginRequest;
import com.zmu.cloud.commons.dto.admin.SysUserAddDTO;
import com.zmu.cloud.commons.dto.admin.SysUserQuery;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.vo.OperatorVO;
import com.zmu.cloud.commons.vo.UserLoginResultVO;
import com.zmu.cloud.commons.vo.UserPermissionVO;
import com.zmu.cloud.commons.vo.UserProfileVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @Author log.infogmail.com
 * @Date 2020-08-11 17:47
 */
public interface SysUserService {

    SysUser add(SysUserAddDTO sysUser);

    boolean update(SysUser sysUser);

    PageInfo<SysUser> list(SysUserQuery query);

    SysUser getById(Long id);

    boolean delete(Long id);

    UserLoginResultVO login(LoginRequest request);

    void logout(UserClientTypeEnum clientTypeEnum);

    @Transactional
    void register(String code, String password);

    boolean disable(Long id);

    boolean resetPassword(Long id, String password);

    boolean resetPass(Long id);

    UserProfileVO profile(Long userId);

    UserPermissionVO permissionInfo(Long moduleId);

    void clearTokenCache(Long userId);

    void clearPermissionCache(Long userId);

    void updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO);

    void bindPigFarmToUser(Long userId, List<Long> farmIds);

    void bindRidToUser(Long userId, String rid);

    List<OperatorVO> getOperators(String employ);

    Set<Long> userFarms(Long userId);
}
