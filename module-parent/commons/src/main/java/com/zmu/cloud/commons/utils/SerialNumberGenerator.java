package com.zmu.cloud.commons.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmu.cloud.commons.entity.FeedTowerDevice;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FeedTowerDeviceMapper;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SerialNumberGenerator {
    private static final String DATE_FORMAT = "yyyyMM";
    private static final int MAX_SEQUENCE = 9999;

    public static synchronized String generateSerialNumber(FeedTowerDeviceMapper feedTowerDeviceMapper) {
        // 查询最近一次的序列号
        LambdaQueryWrapper<FeedTowerDevice> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.orderByDesc(FeedTowerDevice::getSnTime);
        deviceLambdaQueryWrapper.isNotNull(FeedTowerDevice::getSn);
        deviceLambdaQueryWrapper.isNotNull(FeedTowerDevice::getSnTime);
        deviceLambdaQueryWrapper.last(" limit 1 ");
        FeedTowerDevice feedTowerDevice = feedTowerDeviceMapper.selectOne(deviceLambdaQueryWrapper);
        String serialNumber;
        if (ObjectUtil.isEmpty(feedTowerDevice)) {
            // 如果还没有序列号，就是新的今天年月+001序列号
            log.info("暂无序列号,新开序列号!");
            serialNumber = generateTodayInitSerialNum();
        } else {
            String sn = feedTowerDevice.getSn();
            Integer number = Integer.parseInt(sn.substring(sn.length() - 4));
            String parsedSerialDate = sn.substring(sn.length()-10,sn.length() - 4);
            Date currentDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
            Date parsedDate;
            try {
                parsedDate = format.parse(parsedSerialDate);
                if (isSameMonth(currentDate, parsedDate)) {
                    //在最后的序列号基础上+1
                    number+= 1;
                    if(number>MAX_SEQUENCE){
                        throw new BaseException(String.format("序列号越界,超过最大值:%d",MAX_SEQUENCE));
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    String formattedDate = dateFormat.format(currentDate);
                    serialNumber = formattedDate + String.format("%04d", number);
                } else {
                    log.info("和之前的序列号不属于同一月,新开序列号!");
                    serialNumber = generateTodayInitSerialNum();
                }
            } catch (ParseException e) {
                throw new BaseException("序列号生成出错,Invalid date format.");
            }
        }
        return serialNumber;
    }

    private static String  generateTodayInitSerialNum() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedDate = dateFormat.format(currentDate);
        String concat = formattedDate.concat("0001");
        return concat;
    }


    private static boolean isSameMonth(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
        return fmt.format(date1).equals(fmt.format(date2));
    }
}