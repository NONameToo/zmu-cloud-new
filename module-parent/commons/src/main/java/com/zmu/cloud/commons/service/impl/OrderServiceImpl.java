package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.entity.BaseOrder;
import com.zmu.cloud.commons.mapper.BaseOrderMapper;
import org.springframework.stereotype.Service;
import com.zmu.cloud.commons.service.OrderService;

@Service
public class OrderServiceImpl extends ServiceImpl<BaseOrderMapper, BaseOrder>
        implements OrderService {

}



