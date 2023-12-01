package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.extra.pinyin.engine.pinyin4j.Pinyin4jEngine;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.sql.visitor.functions.Char;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.dto.PigFarmDTO;
import com.zmu.cloud.commons.dto.PigFarmQuery;
import com.zmu.cloud.commons.dto.admin.LoginRequest;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.entity.PigType;
import com.zmu.cloud.commons.entity.SysUserFarm;
import com.zmu.cloud.commons.entity.admin.Company;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.exception.admin.UnauthorizedException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.redis.CacheKey.Admin;
import com.zmu.cloud.commons.service.FeedingStrategyRecordService;
import com.zmu.cloud.commons.service.PigFarmService;
import com.zmu.cloud.commons.service.SysUserService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.CompanyAndFarmVo;
import com.zmu.cloud.commons.vo.PigFarmVO;
import com.zmu.cloud.commons.vo.PigTypeVO;
import com.zmu.cloud.commons.vo.UserLoginResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zmu.cloud.commons.constants.Constants.getZCList;
import static com.zmu.cloud.commons.constants.Constants.saveZC;
import static java.util.stream.Collectors.toList;

/**
 * 智慧猪家、云慧养共用PigFarm表，巨星猪场数据定时同步到该表中，ID值做预留
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class PigFarmServiceImpl extends ServiceImpl<PigFarmMapper, PigFarm> implements PigFarmService {

    private final PigTypeMapper pigTypeMapper;
    private final RedissonClient redis;
    final ZmuCloudProperties config;
    private final SysUserFarmMapper sysUserFarmMapper;
    private final SysUserMapper sysUserMapper;
    final CompanyMapper companyMapper;
    final SysUserService sysUserService;
    final PigFarmMapper farmMapper;
    private final FeedingStrategyRecordService feedingStrategyRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(PigFarmDTO pigFarmDTO) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        // 猪场要关联猪只类型id，猪只类型也要关联猪场id，所以先插入猪场，手动设置猪场id到threadLocal中，给后续的插入猪只类型使用
        PigFarm pigFarm = new PigFarm();
        BeanUtils.copyProperties(pigFarmDTO, pigFarm);
        pigFarm.setCompanyId(info.getCompanyId());
        pigFarm.setPigTypeId(checkPigType(pigFarmDTO));
        pigFarm.setCreateBy(RequestContextUtils.getUserId());
        baseMapper.insert(pigFarm);
        // 手动设置猪场id，给下面的insert pigType时 tenantFilter时使用
        info.setPigFarmId(pigFarm.getId());
        //添加该猪场的饲喂策略
//        feedingStrategyRecordService.add(pigFarm);
        //添加猪场id到公司的缓存中，给后续LoginInterceptor中校验使用
        RSet<Long> set = redis.getSet(
                CacheKey.Admin.COMPANY_PIG_FARM.key + RequestContextUtils.getRequestInfo().getCompanyId());
        //更新APP用户管理猪场
        redis.getKeys().getKeysByPattern(CacheKey.Admin.TOKEN.key + "*").forEach(k -> {
            RBucket<Object> bucket = redis.getBucket(k);
            if (bucket.isExists() && bucket.get() instanceof UserLoginResultVO) {
                UserLoginResultVO vo = (UserLoginResultVO) bucket.get();
                if (ObjectUtil.equals(vo.getCompanyId(), info.getCompanyId())) {
                    vo.setManageIds(vo.getManageIds() + "," + pigFarm.getId());
                    bucket.set(vo);
                }
            }
        });

        set.add(pigFarm.getId());
        //新加的猪场给公司默认管理员也加上
        sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserRoleType, UserRoleTypeEnum.COMPANY_ADMIN.name()))
                .forEach(sysUser -> sysUserFarmMapper.insert(SysUserFarm.builder().userId(sysUser.getId()).farmId(pigFarm.getId()).isDefault(0).build()));
        return pigFarm.getId();
    }

    private Long checkPigType(PigFarmDTO pigFarmDTO) {
        Long companyId = RequestContextUtils.getRequestInfo().getCompanyId();
        if (ObjectUtil.isEmpty(pigFarmDTO.getPigTypeId())) {
            if (ObjectUtil.isEmpty(pigFarmDTO.getPigTypeName())) {
                return null;
            }
            if (pigTypeMapper.list(companyId).stream().anyMatch(ty -> ty.getName().equals(pigFarmDTO.getPigTypeName()))) {
                throw new BaseException("猪种名称【%s】已存在，请直接选择", pigFarmDTO.getPigTypeName());
            }
            PigType pigType = PigType.builder().companyId(companyId).name(pigFarmDTO.getPigTypeName())
                    .createBy(RequestContextUtils.getUserId()).build();
            pigTypeMapper.insert(pigType);
            return pigType.getId();
        } else {
            return pigFarmDTO.getPigTypeId();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long pigFarmId) {
        long count = count();
        if (count == 1) {
            throw new BaseException("至少保留一个猪场");
        }
        // 设置为删除
        LambdaUpdateWrapper<PigFarm> pigFarmLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        pigFarmLambdaUpdateWrapper
                .set(PigFarm::getDel, true)
                .eq(PigFarm::getId, pigFarmId);
        update(pigFarmLambdaUpdateWrapper);
        List<SysUserFarm> sysUserFarms = sysUserFarmMapper.selectList(
                new LambdaUpdateWrapper<SysUserFarm>().eq(SysUserFarm::getFarmId, pigFarmId));
        Set<Long> userIds = sysUserFarms.stream().map(SysUserFarm::getUserId).collect(Collectors.toSet());
        //如果删除的是默认猪场，则在剩下的猪场中任意选择一个猪场为默认猪场
        Optional<SysUserFarm> opt = sysUserFarms.stream()
                .filter(uf -> ObjectUtil.equals(uf.getIsDefault(), 1) && uf.getFarmId().equals(pigFarmId)).findFirst();
        if (opt.isPresent()) {
            PigFarm other = list().stream().findFirst().get();
            SysUserFarm uf = new SysUserFarm();
            uf.setUserId(opt.get().getUserId());
            uf.setFarmId(other.getId());
            uf.setIsDefault(1);
            LambdaUpdateWrapper<SysUserFarm> ufWrapper = new LambdaUpdateWrapper<>();
            ufWrapper.eq(SysUserFarm::getUserId, opt.get().getUserId()).eq(SysUserFarm::getFarmId, other.getId());
            sysUserFarmMapper.update(uf, ufWrapper);
        }

        // 删除 用户绑定的猪场
        LambdaUpdateWrapper<SysUserFarm> sysUserFarmLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        sysUserFarmLambdaUpdateWrapper.eq(SysUserFarm::getFarmId, pigFarmId);
        sysUserFarmMapper.delete(sysUserFarmLambdaUpdateWrapper);
        // 删除猪场时从公司缓存中清除猪场id
        RSet<Long> set = redis.getSet(
                CacheKey.Admin.COMPANY_PIG_FARM.key + RequestContextUtils.getRequestInfo().getCompanyId());
        set.remove(pigFarmId);
        userIds.forEach(userId -> {
            RSet<Long> userFarmSet = redis.getSet(Admin.USER_PIG_FARM.key + userId);
            userFarmSet.removeIf(farmId -> ObjectUtil.equals(pigFarmId, farmId));
        });
        delCache(pigFarmId);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(PigFarmDTO pigFarmDTO) {
        PigFarm pigFarm = new PigFarm();
        BeanUtils.copyProperties(pigFarmDTO, pigFarm);
        pigFarm.setUpdateBy(RequestContextUtils.getUserId());
        if (pigFarmDTO.getPigTypeId() == null && StringUtils.isNotBlank(pigFarmDTO.getPigTypeName())) {
            Long companyId = RequestContextUtils.getRequestInfo().getCompanyId();
            if (pigTypeMapper.list(companyId).stream().anyMatch(ty -> ty.getName().equals(pigFarmDTO.getPigTypeName()))) {
                throw new BaseException("猪种名称【%s】已存在，请直接选择", pigFarmDTO.getPigTypeName());
            }
            //猪只类型id为空，但猪只类型名称不为空，说明此时为新增猪只类型的操作
            PigType pigType = PigType.builder().companyId(companyId).name(pigFarmDTO.getPigTypeName())
                    .createBy(RequestContextUtils.getUserId()).build();
            //mybatis-plus的多租户filter会字段配置猪场id
            pigTypeMapper.insert(pigType);
            pigFarm.setPigTypeId(pigType.getId());
        }
        delCache(pigFarm.getId());
        return baseMapper.updateById(pigFarm) > 0;
    }

    @Override
    public PigFarmVO get(Long pigFarmId, Long userId) {
        return baseMapper.get(pigFarmId, userId);
    }

    @Override
    public PageInfo<PigFarmVO> list(PigFarmQuery pigFarmQuery) {
        PageHelper.startPage(pigFarmQuery.getPage(), pigFarmQuery.getSize());
        return PageInfo.of(baseMapper.list(pigFarmQuery));
    }

    @Override
    public List<PigTypeVO> listPigTypes() {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        return pigTypeMapper.list(requestInfo.getCompanyId());
    }

    @Override
    public List<PigFarmVO> listByUserId(Long userId, UserRoleTypeEnum userRoleType) {
        if (userRoleType == UserRoleTypeEnum.SUPER_ADMIN) {
            Set<Long> ids = baseMapper.listUserPigFarmIdsAdmin();
            if (CollectionUtils.isEmpty(ids)) {
                return Collections.emptyList();
            }
            return baseMapper.listByIds(ids, userId);
        }
        return baseMapper.listPigFarmVOsByUserId(userId);
    }

    @Override
    public void setDefaultPigFarm(Long userId, Long defaultPigFarmId) {
        LambdaQueryWrapper<SysUserFarm> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserFarm::getUserId, userId)
                .eq(SysUserFarm::getFarmId, defaultPigFarmId);
        // 自己得有这个猪场才能设置默认
        if (sysUserFarmMapper.selectCount(queryWrapper) > 0) {
            //先把之前的都设置为false
            sysUserFarmMapper.clearDefault(userId);
            //再把当前传入的设置为true
            sysUserFarmMapper.setDefault(userId, defaultPigFarmId);
        }
    }

    @Override
    public PigFarm findByCache(Long farmId) {
        RBucket<PigFarm> bucket = redis.getBucket(CacheKey.Web.farm.key + farmId);
        if (bucket.isExists()) {
            return bucket.get();
        }
        PigFarm farm = farmMapper.byId(farmId);
        if (ObjectUtil.isNotEmpty(farm)) {
            bucket.set(farm);
            bucket.expire(CacheKey.Web.farm.duration);
        }
        return farm;
    }

    @Override
    public void updateFarm(PigFarm farm) {
        baseMapper.updateById(farm);
        delCache(farm.getId());
    }

    void delCache(Long farmId) {
        redis.getBucket(CacheKey.Web.farm.key + farmId).delete();
    }

    @Override
    public List<CompanyAndFarmVo> companyAndFarms(String farmName) {
        if (ObjectUtil.isEmpty(farmName)) {
            return ListUtil.empty();
        }
        RequestInfo info = RequestContextUtils.getRequestInfo();
        String token = info.getToken();
        RBucket<UserLoginResultVO> bucket = redis.getBucket(CacheKey.Admin.TOKEN.key + token);
        UserLoginResultVO loginVo;
        if (bucket.isExists()) {
            loginVo = bucket.get();
        } else {
            throw new UnauthorizedException(401, "请先登录");
        }
        if (info.getResourceType().equals(ResourceType.JX)) {
            return searchByJx(loginVo, farmName);
        } else if (info.getResourceType().equals(ResourceType.YHY)) {
            return searchByYhy(loginVo, farmName);
        }
        return ListUtil.empty();
    }

    private List<CompanyAndFarmVo> searchByJx(UserLoginResultVO loginVo, String farmName) {
        HttpResponse response = HttpUtil.createRequest(Method.POST, config.getConfig().getJxAppBaseUrl().concat(getZCList))
                .header("token", loginVo.getToken())
                .form(JSONUtil.createObj().set("manager_zc", loginVo.getManageIds()).set("start", 1).set("rcount", 999))
                .execute();
        JSONArray arr = (JSONArray)resultSelect(response.body());
        List<CompanyAndFarmVo> vos = arr.stream().map(o -> {
            JSONObject js = (JSONObject) o;
            String name = js.getStr("z_zc_nm");
            return CompanyAndFarmVo.builder().company(js.getStr("z_org_nm"))
                    .farmId(js.getStr("z_zc_id")).farmName(name)
                    .farmNameInitials(PinyinUtil.getEngine().getFirstLetter(name, ""))
                    .rn(js.getStr("rn")).build();
        }).collect(toList());
        return vos.stream().filter(vo ->  vo.getFarmName().contains(farmName) || vo.getFarmNameInitials().contains(farmName.toLowerCase()))
                .collect(toList());
    }

    private List<CompanyAndFarmVo> searchByYhy(UserLoginResultVO loginVo, String farmName) {
        if (ObjectUtil.isEmpty(farmName)) {
            return ListUtil.empty();
        }
        LambdaQueryWrapper<PigFarm> wrapper = Wrappers.lambdaQuery(PigFarm.class);
        if (loginVo.getRoleTypeEnum() != UserRoleTypeEnum.SUPER_ADMIN) {
            wrapper.eq(PigFarm::getCompanyId, loginVo.getCompanyId());
        }
        if (ObjectUtil.isNotEmpty(loginVo.getManageIds())) {
            Set<Long> fids = Arrays.stream(loginVo.getManageIds().split(",")).map(Long::parseLong).collect(Collectors.toSet());
            wrapper.in(PigFarm::getId, fids);
        }
        List<PigFarm> fs = baseMapper.selectList(wrapper.eq(PigFarm::getJx, 0));
        return fs.stream().map(farm ->
                CompanyAndFarmVo.builder().farmId(farm.getId().toString()).farmName(farm.getName())
                        .farmNameInitials(PinyinUtil.getEngine().getFirstLetter(farm.getName(), "")).build()
        ).filter(vo ->  vo.getFarmName().contains(farmName) || vo.getFarmNameInitials().contains(farmName.toLowerCase()))
                .collect(toList());
    }

    @Override
    public List<CompanyAndFarmVo> commonUseFarms() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        RList<CompanyAndFarmVo> commons = redis.getList(CacheKey.Web.user_common_use_farm.key + info.getUserId());
        List<CompanyAndFarmVo> vos = commons.stream().sorted(Comparator.comparing(CompanyAndFarmVo::getTimes)).collect(toList());
        return CollUtil.reverse(vos).stream().limit(10).collect(toList());
    }

    @Override
    public void cleanCommonUseFarms() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        redis.getList(CacheKey.Web.user_common_use_farm.key + info.getUserId()).delete();
    }

    @Override
    public UserLoginResultVO change(String farmId, String farmName) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        RBucket<UserLoginResultVO> bucket = redis.getBucket(CacheKey.Admin.TOKEN.key + info.getToken());
        //加入用户常用猪场
        addCommonUseFarms(info.getUserId(), farmId, farmName);
        if (ResourceType.YHY.equals(info.getResourceType())) {
            UserLoginResultVO vo = bucket.get();
            vo.setDefaultFarmId(Long.parseLong(farmId));
            vo.setDefaultFarmName(farmName);
            PigFarm farm = baseMapper.selectById(farmId);
            Company c = companyMapper.selectById(farm.getCompanyId());
            vo.setCompanyId(farm.getCompanyId());
            vo.setCompanyName(c.getName());
            PigType type = pigTypeMapper.selectById(farm.getPigTypeId());
            if (ObjectUtil.isNotEmpty(type)) {
                vo.setDefaultPigTypeName(type.getName());
            } else {
                vo.setDefaultPigTypeName("");
            }
            return vo;
        }
        HttpResponse response = HttpUtil.createRequest(Method.POST, config.getConfig().getJxAppBaseUrl().concat(saveZC))
                .header("token", info.getToken())
                .form(JSONUtil.createObj().set("usrid", info.getUserId()).set("z_zc_id", farmId).set("z_zc_nm", farmName))
                .execute();

        log.info("巨星用户{}切换猪场{}:{}, 结果：{}", info.getUserId(), farmId, farmName, response.body());
        //切换猪场
        if ("true".equals(response.body())) {
            LoginRequest request = new LoginRequest();
            request.setUserClientType(UserClientTypeEnum.Android);
            request.setUserName(info.getLoginAccount());
            request.setPassword(bucket.get().getUser().getPwd());
            return sysUserService.login(request);
        }
        else if (response.body().contains("登录失效")) {
            throw new UnauthorizedException(401, "请先登录");
        }
        else {
            throw new BaseException("切换失败！");
        }
    }

    private void addCommonUseFarms(Long userId, String farmId, String farmName) {
        RList<CompanyAndFarmVo> commons = redis.getList(CacheKey.Web.user_common_use_farm.key + userId);
        if (commons.isEmpty() || commons.stream().noneMatch(vo -> ObjectUtil.equals(vo.getFarmId(), farmId))) {
            commons.add(CompanyAndFarmVo.builder().farmId(farmId).farmName(farmName).times(1).build());
        } else {
            List<CompanyAndFarmVo> vos = commons.stream().peek(vo -> {
                if (ObjectUtil.equals(vo.getFarmId(), farmId)) {
                    vo.setTimes(vo.getTimes() + 1);
                }
            }).collect(toList());
            commons.clear();
            commons.addAll(vos);
        }
    }

    public Object resultSelect(String body) {
        log.info("调试：[{}]", body);
        if (ObjectUtil.isNotEmpty(body)) {
            JSONObject obj;
            try {
                obj = JSONUtil.parseObj(body);
            } catch (Exception e) {
                log.info(String.format("巨星接口异常：%s", body), e);
                throw new BaseException("巨星接口异常！请联系系统管理员");
            }
            if (body.contains("登录失效")) {
                throw new UnauthorizedException(401, "请先登录");
            }
            if (Boolean.FALSE.equals(obj.getBool("flag"))) {
                throw new BaseException(body);
            }
            return obj.get("info");
        }
        throw new BaseException("巨星接口异常！请联系系统管理员");
    }

    /*@Override
    public Set<Long> listPigFarmIdsByUserId(Long userId, UserRoleTypeEnum userRoleType) {
        switch (userRoleType) {
            case COMMON_USER:
                return baseMapper.listUserPigFarmIds(userId);
            case COMPANY_ADMIN:
                // 等于company_admin 时会在多租户的过滤器中自动加入 company_id 的过滤字段
                //所以这两种角色用同一个方法即可
            case SUPER_ADMIN:
                // 等于 super_admin 时 在多租户过滤器中会去除company_id的过滤字段
                return baseMapper.listUserPigFarmIdsAdmin();
            default:
        }
        return new HashSet<>();
    }*/
}
