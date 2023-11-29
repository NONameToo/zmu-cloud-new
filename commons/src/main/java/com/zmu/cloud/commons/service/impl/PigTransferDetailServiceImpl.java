package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.entity.PigTransferDetail;
import com.zmu.cloud.commons.mapper.PigTransferDetailMapper;
import com.zmu.cloud.commons.service.PigTransferDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PigTransferDetailServiceImpl extends ServiceImpl<PigTransferDetailMapper, PigTransferDetail>
        implements PigTransferDetailService {
}
