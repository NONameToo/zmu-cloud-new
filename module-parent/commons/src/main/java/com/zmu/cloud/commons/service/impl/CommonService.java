package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.zmu.cloud.commons.enums.FileType;
import com.zmu.cloud.commons.enums.SmsTypeEnum;
import com.zmu.cloud.commons.enums.app.ErrorMessageEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class CommonService {

    @Value("${file.upload.images}")
    private String images;
    @Value("${file.upload.docs}")
    private String docs;

    @Autowired
    private SmsService smsService;

    public boolean verifyCode(String account, String code, boolean throwsOnFalse, SmsTypeEnum smsTypeEnum) {
        if (StringUtils.isBlank(account)) {
            if (throwsOnFalse) {
                throw new BaseException(ErrorMessageEnum.VERIFY_CODE_INVALID);
            } else {
                return false;
            }
        }
        boolean verify = false;
        if (account.contains("@")) {
        } else {
            verify = smsService.verify(account, smsTypeEnum == null ? SmsTypeEnum.COMMON.getType() : smsTypeEnum.getType(), code);
        }
        if (!verify && throwsOnFalse) {
            throw new BaseException(ErrorMessageEnum.VERIFY_CODE_INVALID);
        }
        return verify;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String ext = Objects
                .requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName;
        if (Objects.requireNonNull(contentType).startsWith("image")) {
            fileName = Objects.requireNonNull(DateUtil.format(new Date(), "yyyyMMddHHmmss"))
                    .concat(UUID.fastUUID().toString().split("-")[0])
                    .concat(ext);
            file.transferTo(new File(images, fileName));
        } else if (contentType.contains("excel") || contentType.contains("sheet")) {
            fileName = Objects.requireNonNull(DateUtil.format(new Date(), "yyyyMMddHHmmss"))
                    .concat(UUID.fastUUID().toString().split("-")[0])
                    .concat(ext);
            file.transferTo(new File(docs, fileName));
        } else {
            throw new BaseException("文件类型不支持");
        }
        return File.separator.concat(fileName);
    }
}
