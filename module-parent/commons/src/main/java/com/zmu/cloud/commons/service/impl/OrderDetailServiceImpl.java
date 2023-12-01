package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.zmu.cloud.commons.mapper.OrderDetailMapper;
import com.zmu.cloud.commons.entity.OrderDetail;
import com.zmu.cloud.commons.service.OrderDetailService;
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
        implements OrderDetailService{

}
