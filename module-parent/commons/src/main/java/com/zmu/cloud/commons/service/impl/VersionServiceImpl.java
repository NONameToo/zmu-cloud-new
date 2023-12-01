package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.commons.Page;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.BaseEntity;
import com.zmu.cloud.commons.entity.Version;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.VersionMapper;
import com.zmu.cloud.commons.service.VersionService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @author YH
 * @DESCRIPTION: VersionServiceImpl
 * @Date 2019-12-13 11:16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VersionServiceImpl extends ServiceImpl<VersionMapper, Version> implements VersionService {

    final VersionMapper versionMapper;

    @Override
    public Version check(UserClientTypeEnum clientTypeEnum) {
        LambdaQueryWrapper<Version> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Version::getStatus, 1)
                .eq(Version::getOs, clientTypeEnum.getType())
                .orderByDesc(Version::getCreateTime)
                .last("limit 1");
        Version version = versionMapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(version)) {
            throw new BaseException("版本信息有误！");
        }
        if (clientTypeEnum.getType() == 2) {
            version.setMd5(MD5.create().digestHex16(new File("/data/zmu_cloud/version/zmu_cloud.apk")));
        }else if (clientTypeEnum.getType() == 4) {
            version.setMd5(MD5.create().digestHex16(new File("/data/zmu_cloud/version/zm_pig.apk")));
        }
        return version;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(Version version) {
        version.setStatus(1);
        version.setCreateBy(RequestContextUtils.getUserId());
        try {
            versionMapper.insert(version);
        } catch (DuplicateKeyException e) {
            throw new BaseException("该版本号已存在");
        }
        return version.getId();
    }

    @Override
    public Version getById(Long id) {
        return versionMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Version version) {
        version.setUpdateBy(RequestContextUtils.getUserId());
        try {
            return versionMapper.updateById(version) > 0;
        } catch (DuplicateKeyException e) {
            throw new BaseException("该系统类型下已存在此版本");
        }
    }

    @Override
    public PageInfo<Version> list(Page page) {
        PageHelper.startPage(page.getPage(), page.getSize());
        return PageInfo.of(versionMapper.selectList(new LambdaQueryWrapper<Version>().orderByDesc(Version::getCreateTime)));
    }
}
