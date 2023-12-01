package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigFarmQuery;
import com.zmu.cloud.commons.dto.admin.CompanyDTO;
import com.zmu.cloud.commons.dto.admin.CompanyQuery;
import com.zmu.cloud.commons.dto.admin.SysUserAddDTO;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.entity.SysUserFarm;
import com.zmu.cloud.commons.entity.admin.Company;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.CompanyMapper;
import com.zmu.cloud.commons.mapper.PigFarmMapper;
import com.zmu.cloud.commons.mapper.SysUserFarmMapper;
import com.zmu.cloud.commons.mapper.SysUserMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.PhoneUtils;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.CompanyVO;
import com.zmu.cloud.commons.vo.PigFarmVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 10:52
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private final SysUserService sysUserService;
    private final PigFarmMapper pigFarmMapper;
    private final SysUserFarmMapper sysUserFarmMapper;
    private final RedissonClient redis;
    private final SysUserMapper sysUserMapper;
    private final SysProductionTipsService sysProductionTipsService;
    private final FinancialDataTypeService financialDataTypeService;
    private final FeedingStrategyRecordService feedingStrategyRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(CompanyDTO c) {
        if (!PhoneUtils.verifyPhone(c.getPhone())) {
            throw new BaseException("手机号格式不正确");
        }
        if (c.getEnabled() == null) {
            c.setEnabled(false);
        }
        Company company = new Company();
        BeanUtils.copyProperties(c, company);
        company.setCreateBy(RequestContextUtils.getUserId());
        checkIfExist(company.getPhone());
        baseMapper.insert(company);

        // 创建公司时 默认创建一个该公司的管理员账号
        SysUser user = sysUserService.add(SysUserAddDTO.builder()
                // 设置为默认管理员
                .userRoleType(UserRoleTypeEnum.COMPANY_ADMIN)
                .companyId(company.getId())
                .createBy(company.getCreateBy())
                .loginName(company.getPhone())
                .phone(company.getPhone())
                .realName(company.getContactName())
                .status(1)
                .password(company.getPhone())
                .build());
        // 必须创建一个默认猪场，否则登录之后没有猪场，无法通过LoginInterceptor校验
        PigFarm pigFarm = PigFarm.builder()
                .companyId(company.getId())
                .name("默认猪场")
                .type(1)
                .level(1)
                .createBy(company.getCreateBy())
                .principalId(user.getId())
                .build();
        pigFarmMapper.insert(pigFarm);
        // 创建公司时同时创建默认的生产提示
        sysProductionTipsService.initByCompanyCreated(company.getId());
        // 创建公司时同时创建默认的财务数据类型
        financialDataTypeService.initByCompanyCreated(company.getId());
        //把创建的猪场加入新建用户的猪场集合
        sysUserService.bindPigFarmToUser(user.getId(), Collections.singletonList(pigFarm.getId()));
        //添加猪场id到公司的缓存中，给后续LoginInterceptor中校验使用
        RSet<Long> set = redis.getSet(CacheKey.Admin.COMPANY_PIG_FARM.key + company.getId());
        set.add(pigFarm.getId());
        return company.getId();
    }

    private void checkIfExist(String phone) {
        Long count = baseMapper.selectCount(
                new LambdaQueryWrapper<Company>().eq(Company::getPhone, phone));
        if (count > 0 || sysUserMapper.getByLoginAccount(phone) != null) {
            throw new BaseException("手机号已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(CompanyDTO c) {
        Company company = new Company();
        BeanUtils.copyProperties(c, company);
        company.setUpdateBy(RequestContextUtils.getUserId());
        String phone = baseMapper.selectById(c.getId()).getPhone();
        if (StringUtils.isNotBlank(c.getPhone()) && !c.getPhone().equals(phone)) {
            if (!PhoneUtils.verifyPhone(c.getPhone())) {
                throw new BaseException("手机号格式不正确");
            }
            checkIfExist(c.getPhone());
        }
        if (Boolean.FALSE.equals(c.getEnabled())) {
            redis.getSet(CacheKey.Admin.COMPANY_PIG_FARM.key + company.getId()).delete();
            sysUserMapper.listUserId(company.getId()).forEach(userId -> {
                Arrays.stream(UserClientTypeEnum.values())
                        .forEach(userClientTypeEnum -> redis.getBucket(CacheKey.Admin.TOKEN.key + userId + ":" + userClientTypeEnum).deleteAsync());
                sysUserService.clearPermissionCache(userId);
            });
        }
        if (Boolean.TRUE.equals(c.getEnabled())) {
            RSet<Long> set = redis.getSet(CacheKey.Admin.COMPANY_PIG_FARM.key + company.getId());
            Set<Long> ids = pigFarmMapper.listPigFarmIds();
            set.addAll(ids);
        }
        return updateById(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long companyId) {
        LambdaUpdateWrapper<Company> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Company::isDel, true).eq(Company::getId, companyId);
        boolean update = update(updateWrapper);
        if (update) {
            //公司被禁用或删除时 把公司旗下所有 用户、权限、猪场 缓存都清空
            sysUserMapper.listUserId(companyId).forEach(userId -> {
                Arrays.stream(UserClientTypeEnum.values())
                        .forEach(userClientTypeEnum -> redis.getBucket(CacheKey.Admin.TOKEN.key + userId + ":" + userClientTypeEnum).deleteAsync());
                sysUserService.clearPermissionCache(userId);
            });
            redis.getSet(CacheKey.Admin.COMPANY_PIG_FARM.key + companyId).delete();
            // 删除用户
            LambdaUpdateWrapper<SysUser> sysUserLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            sysUserLambdaUpdateWrapper.eq(SysUser::getCompanyId, companyId);
            sysUserMapper.delete(sysUserLambdaUpdateWrapper);
        }
        return update;
    }

    @Override
    public CompanyVO get(Long companyId) {
        return convertToVO(baseMapper.selectById(companyId));
    }

    @Override
    public PageInfo<CompanyVO> list(CompanyQuery q) {
        PageHelper.startPage(q.getPage(), q.getSize());
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(q.getName())) {
            queryWrapper.like(Company::getName, q.getName());
        }
        if (StringUtils.isNotBlank(q.getPhone())) {
            queryWrapper.like(Company::getPhone, q.getPhone());
        }
        if (q.getEnabled() != null) {
            queryWrapper.eq(Company::isEnabled, q.getEnabled());
        }
        if (StringUtils.isNotBlank(q.getContactName())) {
            queryWrapper.like(Company::getContactName, q.getContactName());
        }
        queryWrapper.orderByDesc(Company::getCreateTime);
        //分页
        PageInfo<Company> source = new PageInfo<>(baseMapper.selectList(queryWrapper));
        PageInfo<CompanyVO> target = new PageInfo<>();
        //copy分页信息
        BeanUtils.copyProperties(source, target);
        return target;
    }

    @Override
    public List<CompanyVO> listAndFarms(CompanyQuery q) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        List<PigFarm> farms;
        LambdaQueryWrapper<PigFarm> wrapper = Wrappers.lambdaQuery(PigFarm.class);
        if (info.getUserRoleType() != UserRoleTypeEnum.SUPER_ADMIN) {
            wrapper.eq(PigFarm::getCompanyId, info.getCompanyId());
            List<Object> userFarms = sysUserFarmMapper.selectObjs(Wrappers.lambdaQuery(SysUserFarm.class)
                    .select(SysUserFarm::getFarmId).eq(SysUserFarm::getUserId, info.getUserId()));
            farms = pigFarmMapper.selectList(wrapper.like(PigFarm::getName, ObjectUtil.isEmpty(q.getFarmName())?"":q.getFarmName()).in(PigFarm::getId, userFarms));
        } else {
            wrapper.in(PigFarm::getCompanyId, baseMapper.selectList(Wrappers.emptyWrapper()).stream().map(Company::getId).collect(Collectors.toSet()));
            farms = pigFarmMapper.selectList(wrapper.like(PigFarm::getName, ObjectUtil.isEmpty(q.getFarmName())?"":q.getFarmName()));
        }
        Map<Long, List<PigFarm>> cfs = farms.stream().collect(Collectors.groupingBy(PigFarm::getCompanyId));
        List<CompanyVO> cs = new ArrayList<>();
        cfs.forEach((cId, fms) -> {
            Company company = baseMapper.selectById(cId);
            CompanyVO vo = new CompanyVO();
            vo.setId(company.getId());
            vo.setName(company.getName());
            vo.setFarms(fms);
            cs.add(vo);
        });
        return cs;
    }

    @Override
    public Set<Long> companyFarms(Long companyId) {
        RSet<Long> companyFarms = redis.getSet(CacheKey.Admin.COMPANY_PIG_FARM.key + companyId);
        if (companyFarms.isEmpty()) {
            PigFarmQuery query = new PigFarmQuery();
            query.setSize(100000);
            query.setCompanyId(companyId);
            companyFarms.addAll(pigFarmMapper.list(query).stream().map(PigFarmVO::getId).collect(Collectors.toSet()));
        }
        return companyFarms;
    }

    private CompanyVO convertToVO(Company c) {
        CompanyVO vo = new CompanyVO();
        BeanUtils.copyProperties(c, vo);
        return vo;
    }
}
