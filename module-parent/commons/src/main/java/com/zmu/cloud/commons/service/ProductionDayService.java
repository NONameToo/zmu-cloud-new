package com.zmu.cloud.commons.service;

import java.util.Date;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/30 15:10
 **/
public interface ProductionDayService {

    void insertOrUpdate(Long pigId, Date startDate, Date endDate);
}
