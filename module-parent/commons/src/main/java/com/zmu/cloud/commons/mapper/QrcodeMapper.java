package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.Qrcode;import org.apache.ibatis.annotations.Param;import java.util.List;

public interface QrcodeMapper extends BaseMapper<Qrcode> {
    List<Qrcode> selectByCode(@Param("code") String code);
}