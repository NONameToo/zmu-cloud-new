package com.zmu.cloud.admin.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.FirmwareVersionDto;
import com.zmu.cloud.commons.dto.QueryFirmware;
import com.zmu.cloud.commons.entity.FirmwareVersion;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author YH
 */
public interface FirmwareVersionService {

    PageInfo<FirmwareVersion> page(QueryFirmware queryFirmware);

    @Transactional
    void add(MultipartFile file, FirmwareVersionDto dto) throws IOException;

    @Transactional
    void del(Long id);

}
