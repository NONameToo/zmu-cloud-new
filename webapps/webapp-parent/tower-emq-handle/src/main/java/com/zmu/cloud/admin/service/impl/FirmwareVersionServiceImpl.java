package com.zmu.cloud.admin.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.db.Page;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.admin.service.FirmwareVersionService;
import com.zmu.cloud.commons.dto.FirmwareVersionDto;
import com.zmu.cloud.commons.dto.QueryFirmware;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.FirmwareUpgradeConfig;
import com.zmu.cloud.commons.entity.FirmwareVersion;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FirmwareUpgradeConfigMapper;
import com.zmu.cloud.commons.mapper.FirmwareVersionMapper;
import com.zmu.cloud.commons.mapper.SysUserMapper;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirmwareVersionServiceImpl implements FirmwareVersionService {

    final FirmwareVersionMapper firmwareVersionMapper;
    final FirmwareUpgradeConfigMapper upgradeConfigMapper;
    final SysUserMapper sysUserMapper;

    @Value("${file.firmware.upgrade.save.linux}")
    private String firmwareUpgradeSaveLinux;
    @Value("${file.firmware.upgrade.save.windows}")
    private String firmwareUpgradeSaveWindows;

    @Override

    public PageInfo<FirmwareVersion> page(QueryFirmware queryFirmware) {
        PageHelper.startPage(queryFirmware.getPage(), queryFirmware.getSize());
        return PageInfo.of(firmwareVersionMapper.selectList(Wrappers.lambdaQuery(FirmwareVersion.class)
                .eq(FirmwareVersion::getCategory, queryFirmware.getCategory().name())
                .like(FirmwareVersion::getVersion, queryFirmware.getVersion())
                .orderByDesc(FirmwareVersion::getCreateTime)
        ).stream().map(v -> {
            v.setCreateUser(sysUserMapper.findUserById(v.getCreateBy()).getRealName());
            return v;
        }).collect(Collectors.toList()));
    }

    @Override
    public void add(MultipartFile file, FirmwareVersionDto dto) throws IOException {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        byte[] bts = file.getBytes();
        String name = file.getOriginalFilename();
        String prefix = name.substring(0, name.lastIndexOf("."));
        if (!new String(bts).contains(prefix)) {
            throw new BaseException("文件名与固件内置版本号不一致");
        }
        boolean exists = firmwareVersionMapper.exists(Wrappers.lambdaQuery(FirmwareVersion.class)
                .eq(FirmwareVersion::getCategory, dto.getCategory()).eq(FirmwareVersion::getVersion, prefix));
        if (exists) {
            throw new BaseException("版本已存在！");
        }
        String path = SystemUtil.getOsInfo().isWindows()?firmwareUpgradeSaveWindows:firmwareUpgradeSaveLinux;
        FileUtil.writeBytes(bts, path + File.separator + name);
        firmwareVersionMapper.insert(FirmwareVersion.builder()
                .category(dto.getCategory().name())
                .version(prefix)
                .fileName(name)
                .savePath(path)
                .remark(dto.getRemark())
                .createBy(info.getUserId())
                .build());
    }

    @Override
    public void del(Long id) {
        firmwareVersionMapper.deleteById(id);
        upgradeConfigMapper.update(null, new UpdateWrapper<FirmwareUpgradeConfig>().lambda()
                .set(FirmwareUpgradeConfig::getVersionId, null)
                .set(FirmwareUpgradeConfig::getVersion, null)
                .set(FirmwareUpgradeConfig::getVersionFile, null)
                .eq(FirmwareUpgradeConfig::getVersionId, id));
    }

}
