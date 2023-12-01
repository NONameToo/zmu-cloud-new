package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.constants.Constants;
import com.zmu.cloud.commons.dto.UserPasswordUpdateDTO;
import com.zmu.cloud.commons.dto.admin.LoginRequest;
import com.zmu.cloud.commons.dto.admin.SysUserAddDTO;
import com.zmu.cloud.commons.dto.admin.SysUserQuery;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.entity.admin.*;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.redis.CacheKey.Admin;
import com.zmu.cloud.commons.service.PigFarmService;
import com.zmu.cloud.commons.service.SysUserService;
import com.zmu.cloud.commons.utils.*;
import com.zmu.cloud.commons.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @Author gmail.com
 * @Date 2020-08-11 19:06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final PushUserTypeMapper pushUserTypeMapper;
    private final PasswordEncoder passwordEncoder;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final RedissonClient redis;
    private final SysLoginInfoMapper sysLoginInfoMapper;
    private final ZmuCloudProperties zmuCloudProperties;
    private final CompanyMapper companyMapper;
    private final PigFarmService pigFarmService;
    private final PigTypeMapper pigTypeMapper;
    private final SysUserFarmMapper sysUserFarmMapper;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser add(SysUserAddDTO sysUser) {
        if (Validator.hasChinese(sysUser.getLoginName())) {
            throw new BaseException("登录账号不可包含中文");
        }
        if (ObjectUtil.isNull(sysUser.getCompanyId()) || sysUser.getCompanyId() == 0L) {
            throw new BaseException("当前登录用户为超级管理员，不属于任何公司，无法新增账号，请登录公司管理员操作！");
        }
        SysUser user = new SysUser();
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        BeanUtils.copyProperties(sysUser, user);
        if (baseMapper.getByLoginAccount(user.getLoginName()) != null) {
            throw new BaseException("账号已存在：" + sysUser.getLoginName());
        }
        if (user.getUserRoleType() == null) {
            user.setUserRoleType(UserRoleTypeEnum.COMMON_USER);
        }
        save(user);
        insertUserRole(user);
        insertPushUserType(user);
        if (CollectionUtils.isNotEmpty(sysUser.getFarmIds())) {
            bindPigFarmToUser(user.getId(), sysUser.getFarmIds());
        }
        return user;
    }

    private void insertUserRole(SysUser sysUser) {
        if (CollectionUtils.isNotEmpty(sysUser.getRoleIds())) {
            List<SysUserRole> list = new ArrayList<>();
            sysUser.getRoleIds()
                    .forEach(aLong -> list.add(SysUserRole.builder().roleId(aLong).userId(sysUser.getId()).build()));
            sysUserRoleMapper.batchInsert(list);
            cacheUserPermission(sysUser.getId());
        }
    }


    /**
     * 插入用户和订阅的消息类型关联
     *
     * @param sysUser
     */
    private void insertPushUserType(SysUser sysUser) {
        if (CollectionUtils.isNotEmpty(sysUser.getPushMessageTypeIds())) {
            List<PushUserType> list = new ArrayList<>();
            sysUser.getPushMessageTypeIds()
                    .forEach(aLong -> list.add(PushUserType.builder().typeId(aLong).userId(sysUser.getId()).build()));
            pushUserTypeMapper.batchInsert(list);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysUser sysUser) {
        //不能修改用户角色类型 只能初始化
        sysUser.setUserRoleType(null);
        SysUser select = baseMapper.selectById(sysUser.getId());
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        sysUser.setUpdateBy(requestInfo.getUserId());
        if (StringUtils.isNotBlank(sysUser.getPassword())) {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        }
        // 修改后的角色和数据库中的原始数据一样时才更新
        if (!SetUtils.disjunction(select.getRoleIds(), sysUser.getRoleIds()).toSet().isEmpty()) {
            sysUserRoleMapper.deleteByUserId(sysUser.getId());
            insertUserRole(sysUser);
        }

        /**
         * 如果修改了用户订阅消息类型
         */
        if (CollectionUtils.isNotEmpty(sysUser.getPushMessageTypeIds())) {
            boolean needUpdate = false;
            if (ObjectUtils.isNotEmpty(select.getPushMessageTypeIds())) {
                for (Long pushMessageTypeId : select.getPushMessageTypeIds()) {
                    if (!sysUser.getPushMessageTypeIds().contains(pushMessageTypeId)) {
                        needUpdate = true;
                        break;
                    }
                }
            } else {
                needUpdate = true;
            }
            if (needUpdate) {
                pushUserTypeMapper.deleteByUserId(sysUser.getId());
                insertPushUserType(sysUser);
            }
        }


        if (StringUtils.isNotBlank(sysUser.getLoginName()) && !sysUser.getLoginName().equals(select.getLoginName())) {
            if (baseMapper.getByLoginAccount(sysUser.getLoginName()) != null) {
                throw new BaseException("账号已存在：" + sysUser.getLoginName());
            }
            checkIfCompanyAdminAndThrow(sysUser);
            // 修改了登录账号后把老账号的token和权限缓存清除
            Long oldUserId = select.getId();
            clearTokenCache(oldUserId);
            clearPermissionCache(oldUserId);
        }
        // 如果被禁用就删除token缓存和权限缓存
        if (ObjectUtil.equals(sysUser.getStatus(), 0)) {
            clearTokenCache(sysUser.getId());
            clearPermissionCache(sysUser.getId());
        }
        if (CollectionUtils.isNotEmpty(sysUser.getFarmIds())) {
            bindPigFarmToUser(sysUser.getId(), sysUser.getFarmIds());
        }
        return baseMapper.updateById(sysUser) > 0;
    }

    @Override
    public PageInfo<SysUser> list(SysUserQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(baseMapper.list(query));
    }

    @Override
    public SysUser getById(Long id) {
        return super.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        checkIfCompanyAdminAndThrow(baseMapper.selectById(id));
        LambdaUpdateWrapper<SysUser> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(SysUser::getUpdateBy, RequestContextUtils.getUserId())
                .set(SysUser::isDel, true)
                .eq(SysUser::getId, id);
        boolean success = sysUserRoleMapper.deleteByUserId(id) > 0 && update(lambdaUpdateWrapper);
        if (success) {
            clearTokenCache(id);
            clearPermissionCache(id);
        }else{
            throw new BaseException("该用户没有角色,请配置角色后重试!");
        }
        return success;
    }

    @Override
    public UserLoginResultVO login(LoginRequest request) {
        if (Validator.hasChinese(request.getUserName())) {
            log(false, "登录账号不可包含中文", request.getUserName(), null);
            throw new BaseException("登录账号不可包含中文");
        }
        UserLoginResultVO.UserLoginResultVOBuilder loginVo = UserLoginResultVO.builder();
        loginVo.clientTypeEnum(request.getUserClientType());
        //首先检查是否为云慧养用户
        SysUser user = baseMapper.getByLoginAccount(request.getUserName());
        if (null == user) {
            loginForErp(request, loginVo);
        } else {
            loginForYhy(request, user, loginVo);
        }
        UserLoginResultVO vo = loginVo.build();
        if (ObjectUtil.isNotEmpty(vo.getDefaultFarmId())) {
            PigFarm farm = pigFarmService.findByCache(vo.getDefaultFarmId());
            PigType type = pigTypeMapper.selectById(farm.getPigTypeId());
            if (ObjectUtil.isNotEmpty(type)) {
                vo.setDefaultPigTypeName(type.getName());
            } else {
                vo.setDefaultPigTypeName("");
            }
        }

        user = vo.getUser();
        // 每种客户端只能有一个登录用户，不能多个设备同时登录
        redis.getBucket(CacheKey.Admin.TOKEN.key + user.getId() + ":" + request.getUserClientType().name())
                .set(vo.getToken(), zmuCloudProperties.getConfig().getTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
        redis.getBucket(CacheKey.Admin.TOKEN.key + vo.getToken())
                .set(vo, zmuCloudProperties.getConfig().getTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
        cacheUserPermission(user.getId());
        log(true, "登录成功", request.getUserName(), user.getId());
        vo.setManageIds(null);
        return vo;
    }

    public void loginForYhy(LoginRequest request, SysUser user, UserLoginResultVO.UserLoginResultVOBuilder loginVo) {
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log(false, "密码错误", request.getUserName(), user.getId());
            throw new BaseException("密码错误");
        }
        if (user.isDisabled()) {
            log(false, "用户被禁用", request.getUserName(), user.getId());
            throw new BaseException("您的账户已被禁用，请联系管理员");
        }
        Set<String> roleNames = sysRoleMapper.getRoleNameByUserId(user.getId());
        List<PigFarmVO> voList = null;
        Company company = null;
        if (!RoleUtils.isSuperAdmin(user.getId())) {
            if (user.getCompanyId() == null) {
                log(false, "当前账号未绑定公司", request.getUserName(), user.getId());
                throw new BaseException("当前账号未绑定公司，请联系管理员");
            }
            company = companyMapper.selectById(user.getCompanyId());
            if (!company.isEnabled()) {
                log(false, "公司被禁用", request.getUserName(), user.getId());
                throw new BaseException("您的公司已被禁用，请联系管理员");
            }
            // super_admin 应该不需要猪场列表
            voList = pigFarmService.listByUserId(user.getId(), user.getUserRoleType());
            if (CollectionUtils.isEmpty(voList)) {
                log(false, "当前账号未绑定猪场", request.getUserName(), user.getId());
                throw new BaseException("当前账号未绑定猪场，请联系管理员");
            }
            if (user.getUserRoleType() == UserRoleTypeEnum.COMMON_USER) {
                if (CollectionUtils.isEmpty(roleNames)) {
                    log(false, "当前账号未绑定角色", request.getUserName(), user.getId());
                    throw new BaseException("当前账号未绑定角色，请联系管理员");
                }
            }
            //  登录之后把当前用户拥有的猪场id写入redis中，后期拦截器中每次校验redis中的猪场是否包含header中传递的猪场id，避免用户越权访问非自己的猪场
            RSet<Long> userFarmSet = redis.getSet(CacheKey.Admin.USER_PIG_FARM.key + user.getId());
            userFarmSet.clear();
            userFarmSet.addAll(voList.stream().map(PigFarmVO::getId).collect(Collectors.toList()));
            Optional<PigFarmVO> def = voList.stream().filter(PigFarmVO::getIsDefault).findFirst();
            def.ifPresent(vo -> loginVo.defaultFarmId(vo.getId()).defaultFarmName(vo.getName()));
            loginVo.companyId(company.getId()).companyName(company.getName())
                    .pigFarms(voList)
                    .manageIds(String.join(",", voList.stream().map(PigFarmVO::getId).filter(ObjectUtil::isNotNull).map(Object::toString)
                            .collect(toSet())));
        }


        ZmuCloudProperties.Config config = zmuCloudProperties.getConfig();
        UserClientTypeEnum userClientType = request.getUserClientType();
        loginVo.user(user)
                .roles(roleNames)
                .token(JWTUtil.sign(config.getJwtSecret(),
                        user.getId(),
                        user.getLoginName(),
                        null==company?null:company.getId(),
                        userClientType,
                        user.getUserRoleType()))
                .resourceType(ResourceType.YHY)
                .roleTypeEnum(user.getUserRoleType());
    }

    public void loginForErp(LoginRequest request, UserLoginResultVO.UserLoginResultVOBuilder loginVo) {
        HttpResponse response = HttpUtil.createPost(zmuCloudProperties.getConfig().getJxAppBaseUrl().concat(Constants.LOGIN))
                .form(JSONUtil.createObj()
                        .set("logid", request.getUserName())
                        .set("pwd", request.getPassword())
                        .set("SystemType", "Android"))
                .execute();
        JSONObject obj = JSONUtil.parseObj(response.body());
        String token = obj.getStr("token");
        if (ObjectUtil.isEmpty(token)) {
            throw new BaseException(obj.getStr("message"));
        } else if (UserClientTypeEnum.Web.equals(request.getUserClientType())) {
            throw new BaseException("请使用巨星ERP系统!");
        }
        JSONObject info = obj.getJSONObject("usrinfo");
        SysUser user = new SysUser();
        user.setId(info.getLong("usrid"));
        user.setLoginName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setPwd(request.getPassword());
        user.setRealName(info.getStr("staff_name"));
        Long companyId;
        String companyName;
        Set<Long> manageIds = new HashSet<>();
        if (ObjectUtil.isEmpty(info.getStr("m_org_id")) || "0".equals(info.getStr("m_org_id"))) {
            try {
                companyId = Long.parseLong(info.getStr("managred_unit").split(",")[0]);
                companyName = info.getStr("managred_unit_nm").split(",")[0];
            } catch (Exception e) {
                throw new BaseException("用户公司不存在");
            }
        } else {
            companyId = info.getLong("m_org_id");
            companyName = info.getStr("m_org_nm");
        }
        Long farmId;
        String farmName;
        if (ObjectUtil.isEmpty(info.getStr("z_zc_id")) || "0".equals(info.getStr("z_zc_id"))) {
            try {
                farmId = info.getLong("dept_id");
                farmName = info.getStr("dept_nm");
                manageIds.add(farmId);
            } catch (Exception e) {
                throw new BaseException("用户管理实体不存在");
            }
        } else {
            farmId = info.getLong("z_zc_id");
            farmName = info.getStr("z_zc_nm");
            String[] zcs = info.getStr("manager_zc").split(",");
            manageIds.addAll(Arrays.stream(zcs).map(Long::parseLong).collect(toSet()));
        }
        JSONArray resinfo = (JSONArray) obj.get("resinfo");
        List<ResInfoVo> objs = resinfo.stream().map(o -> {
            JSONObject jsonObject = (JSONObject) o;
            ResInfoVo resInfoVo = new ResInfoVo();
            resInfoVo.setResid(new BigDecimal(jsonObject.get("resid").toString()).longValue());
            resInfoVo.setUpresid(new BigDecimal(jsonObject.get("upresid").toString()).longValue());
            return resInfoVo;
        }).collect(Collectors.toList());
        loginVo.token(token).user(user)
                .companyId(companyId).companyName(companyName)
                .defaultFarmId(farmId).defaultFarmName(farmName)
                .deptNm(info.get("dept_nm").toString())
                .managerZc(info.get("manager_zc").toString())
                .managerZcNm(info.get("manager_zc_nm").toString())
                .mOrgId(info.get("m_org_id").toString())
                .mOrgNm(info.get("m_org_nm").toString())
                .resInfo(objs)
                .manageIds(manageIds.stream().filter(ObjectUtil::isNotNull).map(Object::toString).collect(Collectors.joining(",")))
//                .menus(obj.getJSONArray("resinfo"))
                .resourceType(ResourceType.JX);
    }

    @Override
    public void logout(UserClientTypeEnum clientTypeEnum) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        redis.getBucket(CacheKey.Admin.TOKEN.key + info.getToken()).delete();
    }

    @Override
    public void register(String code, String password) {
        SysUser user = baseMapper.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getLoginName, "18628333127"));
        if (ObjectUtils.isNotEmpty(user)) {
            SysUserFarm defaultFarm = sysUserFarmMapper.selectOne(Wrappers.lambdaQuery(SysUserFarm.class)
                    .eq(SysUserFarm::getUserId, user.getId()).eq(SysUserFarm::getIsDefault, 1));
            SysUser exists = baseMapper.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getLoginName, code));
            if (ObjectUtil.isNotEmpty(exists)) {
                throw new BaseException("用户名已存在！");
            }
            String uu = UUIDUtils.getUUIDShort();
            SysUser register = new SysUser();
            BeanUtils.copyProperties(user, register);
            register.setId(null);
            register.setLoginName(code);
            register.setNickName(uu);
            register.setRealName(uu);
            register.setEmail("");
            register.setPhone("");
            register.setPassword(passwordEncoder.encode(password));
            register.setCreateTime(new Date());
            baseMapper.insert(register);
            sysUserFarmMapper.insert(SysUserFarm.builder().userId(register.getId()).farmId(defaultFarm.getFarmId()).isDefault(1).build());
        }
    }

    private void log(boolean success, String msg, String loginAccount, Long userId) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) attributes).getRequest();
        String browser = "-";
        try {
            browser = UserAgentUtil.parse(httpServletRequest.getHeader("User-Agent")).getBrowser().getName();
        } catch (Exception e) {
        }
        String finalBrowser = browser;
        taskExecutor.execute(() -> {
            try {
                RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
                SysLoginInfo build = SysLoginInfo.builder()
                        .companyId(requestInfo.getCompanyId())
                        .pigFarmId(requestInfo.getPigFarmId())
                        .userId(userId)
                        .createBy(userId)
                        .clientType(requestInfo.getClientType().name())
                        .ip(requestInfo.getIp())
                        .browser(finalBrowser)
                        .os(requestInfo.getOs())
                        .loginName(loginAccount)
                        .status(success ? 1 : 0)
                        .msg(msg)
                        .loginTime(new Date())
                        .loginLocation(IPUtils.addr(requestInfo.getIp()))
                        .build();
                sysLoginInfoMapper.insert(build);
            } catch (Exception e) {
                log.error("记录登录日志失败：{}", e.getMessage());
                return;
            }
            log.info("用户：{} 登录，result= {}", loginAccount, msg);
        });
    }

    public void cacheUserPermission(Long userId) {
        if (RoleUtils.isSuperAdmin(userId)) {
            return;
        }
        SysUser sysUser = baseMapper.selectById(userId);
        if (sysUser == null) {
            return;
        }
        boolean companyAdmin = sysUser.getUserRoleType() == UserRoleTypeEnum.COMPANY_ADMIN;
        Set<String> permission =
                companyAdmin ? sysMenuMapper.listPermissionForAdmin(UserRoleTypeEnum.COMPANY_ADMIN.name())
                        : sysMenuMapper.listPermissionByUserId(null, userId);
        RMap<String, String> map = redis.getMap(CacheKey.Admin.PERMISSION_MAP.key + userId);
        map.forEach((k, v) -> {
            if (!permission.contains(k)) {
                map.remove(k);
            }
        });
        permission.stream().filter(StringUtils::isNotBlank).forEach(s -> map.put(s, ""));
        log.info("更新用户：{} - {}  权限缓存", userId, sysUser.getLoginName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disable(Long id) {
        SysUser sysUser = getById(id);
        if (sysUser == null) {
            return false;
        }
        checkIfCompanyAdminAndThrow(sysUser);
        sysUser.setStatus(sysUser.getStatus() == 0 ? 1 : 0);
        sysUser.setUpdateBy(RequestContextUtils.getUserId());
        if (sysUser.getStatus() == 0) {
            // 禁用之后删除token缓存和权限缓存
            clearTokenCache(id);
            clearPermissionCache(id);
        }
        return baseMapper.updateById(sysUser) > 0;
    }

    @Override
    public void clearTokenCache(Long userId) {
        for (UserClientTypeEnum value : UserClientTypeEnum.values()) {
            redis.getBucket(CacheKey.Admin.TOKEN.key + userId + ":" + value).delete();
        }
    }

    @Override
    public void clearPermissionCache(Long userId) {
        redis.getMap(CacheKey.Admin.PERMISSION_MAP.key + userId).delete();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO) {
        Long userId = RequestContextUtils.getUserId();
        SysUser user = getById(userId);
        if (!passwordEncoder.matches(userPasswordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new BaseException("旧密码错误");
        }
        user.setUpdateBy(userId);
        user.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.getNewPassword()));
        baseMapper.updateById(user);
        //修改密码后删除token缓存，让用户必须重新登录
        clearTokenCache(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id, String password) {
        SysUser sysUser = getById(id);
        if (sysUser == null) {
            return false;
        }
        sysUser.setUpdateBy(RequestContextUtils.getUserId());
        sysUser.setPassword(passwordEncoder.encode(password));
        //重置密码后删除token缓存，让用户必须重新登录
        clearTokenCache(id);
        return baseMapper.updateById(sysUser) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPass(Long id) {

        UserRoleTypeEnum userRoleType = RequestContextUtils.getRequestInfo().getUserRoleType();

        if (!UserRoleTypeEnum.SUPER_ADMIN.equals(userRoleType) && !UserRoleTypeEnum.COMPANY_ADMIN.equals(userRoleType)){
            throw new BaseException("当前登录用户不是管理员，无权重置密码！");
        }

        SysUser sysUser = getById(id);
        if (sysUser == null) {
            return false;
        }
        sysUser.setUpdateBy(RequestContextUtils.getUserId());
        sysUser.setUpdateTime(new Date());
        sysUser.setPassword(passwordEncoder.encode(sysUser.getLoginName()));
        //重置密码后删除token缓存，让用户必须重新登录
        clearTokenCache(id);
        return baseMapper.updateById(sysUser) > 0;
    }

    @Override
    public UserProfileVO profile(Long userId) {
        SysUser user = getById(userId);
        Set<String> names = sysRoleMapper.getRoleNameByUserId(userId);
        return UserProfileVO.builder().sysUser(user).roles(names).role(user.getUserRoleType()).build();
    }

    @Override
    public UserPermissionVO permissionInfo(Long moduleId) {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        Long userId = requestInfo.getUserId();
        //super_admin 有所有的权限，company_admin和公司普通员工只有可见的菜单以及自己对应的权限
        List<SysMenu> sysMenus = sysMenuMapper.listByUserId(moduleId, userId, requestInfo.getUserRoleType().name());
        Set<String> permissions = new HashSet<>();
        switch (requestInfo.getUserRoleType()) {
            case COMMON_USER:
                permissions = sysMenuMapper.listPermissionByUserId(moduleId, userId);
                break;
            case COMPANY_ADMIN:
            case SUPER_ADMIN:
                // super_admin有所有的权限，company_admin只有菜单visible为true的权限，即 可以设置部分菜单对公司隐藏
                permissions = sysMenuMapper.listPermissionForAdmin(requestInfo.getUserRoleType().name());
                break;
            default:
        }
        Company company = companyMapper.selectById(requestInfo.getCompanyId());
        PigFarm farm = pigFarmService.findByCache(requestInfo.getPigFarmId());
        return UserPermissionVO.builder().companyId(ObjectUtil.isEmpty(company)?null:company.getId())
                .companyName(ObjectUtil.isEmpty(company)?null:company.getName())
                .defaultFarmId(ObjectUtil.isEmpty(farm)?null:farm.getId())
                .defaultFarmName(ObjectUtil.isEmpty(farm)?null:farm.getName())
                .permissions(permissions).sysMenuList(sysMenus).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPigFarmToUser(Long userId, List<Long> farmIds) {
        // 当前传入的第一个作为默认
        Long defaultPigFarmId = farmIds.stream().findFirst().get();
        LambdaUpdateWrapper<SysUserFarm> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(SysUserFarm::getUserId, userId);
        sysUserFarmMapper.delete(deleteWrapper);
        farmIds.forEach(
                farmId -> sysUserFarmMapper.insert(SysUserFarm.builder().userId(userId).farmId(farmId).isDefault(ObjectUtil.equals(defaultPigFarmId, farmId)?1:0).build()));
        RSet<Long> set = redis.getSet(Admin.USER_PIG_FARM.key + userId);
        set.removeIf(id -> !farmIds.contains(id));
        set.addAll(farmIds);
    }


    @Override
    public void bindRidToUser(Long userId, String rid) {
        //用户登录之后,之前拥有该rid的用户重置
//        SysUserQuery sysUserQuery = new SysUserQuery();
//        List<SysUser> list = baseMapper.list(sysUserQuery);
//        list.stream().filter(oneUser->
//                oneUser.getId() != userId
//        ).forEach(onUser->{
//            onUser.setRid(null);
//            baseMapper.updateById(onUser);
//        });
//        baseMapper.cleanOtherUserRid(rid);
//        SysUser user = getById(userId);
//        user.setRid(rid);
//        user.setUpdateBy(RequestContextUtils.getUserId());
//        baseMapper.updateById(user);
    }


    @Override
    public List<OperatorVO> getOperators(String employ) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        return baseMapper.getOperators(info.getCompanyId(), employ);
    }

    @Override
    public Set<Long> userFarms(Long userId) {
        RSet<Long> userFarms = redis.getSet(CacheKey.Admin.USER_PIG_FARM.key + userId);
        if (userFarms.isEmpty()) {
            userFarms.addAll(
                    pigFarmService.listByUserId(userId, null)
                            .stream().map(PigFarmVO::getId).collect(toSet())
            );
        }
        return userFarms;
    }

    private void checkIfCompanyAdminAndThrow(SysUser sysUser) {
        if (RequestContextUtils.getRequestInfo().getUserRoleType() == UserRoleTypeEnum.SUPER_ADMIN) {
            // super_admin不校验
            return;
        }
        if (sysUser.getUserRoleType() == UserRoleTypeEnum.COMPANY_ADMIN) {
            throw new BaseException("无法操作公司默认管理员");
        }
    }
}
