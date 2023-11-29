package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.entity.Qrcode;
import com.zmu.cloud.commons.vo.ColumnVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author YH
 */
public interface QrcodeService {

    /**
     * 二维码解析
     * @param content
     */
    PigHouseColumns scan(String content);
    void generate(Integer begin, Integer end) throws Exception;
    @Transactional
    void generate(Long houseId);
    Qrcode findByCode(String code);

}
