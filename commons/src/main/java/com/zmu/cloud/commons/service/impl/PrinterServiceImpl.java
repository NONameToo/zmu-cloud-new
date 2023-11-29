package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.Printer;
import com.zmu.cloud.commons.entity.PrinterUser;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PrinterMapper;
import com.zmu.cloud.commons.mapper.PrinterUserMapper;
import com.zmu.cloud.commons.mapper.SphEmployMapper;
import com.zmu.cloud.commons.mapper.SysUserMapper;
import com.zmu.cloud.commons.service.PrinterService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.PrinterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PrinterServiceImpl implements PrinterService {

    final RedissonClient redis;
    final PrinterMapper printerMapper;
    final PrinterUserMapper printerUserMapper;
    final SphEmployMapper employMapper;
    final SysUserMapper sysUserMapper;

    @Override
    public List<PrinterVO> list() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        return printerMapper.listPinterByUser(info.getUserId());
    }

    //设置默认打印机
    @Override
    public List<PrinterVO> setDefaultPrinter(Long printerId) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        //删除所有的关联,加入新关联
        printerMapper.deleteByUserId(info.getUserId());
        PrinterUser printerUser = new PrinterUser();
        printerUser.setUserId(info.getUserId());
        printerUser.setPrinterId(printerId);
        printerUser.setIsDefault(1);
        printerUserMapper.insert(printerUser);
        return printerMapper.listPinterByUser(info.getUserId());
    }

    @Override
    public Printer save(Printer printer) {

        try {
            if(!NetUtil.isInnerIP(printer.getPrinterIp())){
                throw new BaseException("无效ip地址!");
            }
        }catch (Exception e){
            throw new BaseException("无效ip地址!");
        }
        try {
            if(!NetUtil.isValidPort(printer.getPrinterPort())){
                throw new BaseException("无效端口!");
            }
        }catch (Exception e){
            throw new BaseException("无效端口!");
        }

        try {
            if(ObjectUtil.isEmpty(printer.getId())){
                printer.setCreateTime(LocalDateTime.now());
                printerMapper.insert(printer);
            }else{
                printerMapper.updateById(printer);
            }
        }catch (DuplicateKeyException e){
            throw new BaseException("打印机名称重复！");
        }
        return printer;
    }

    @Override
    public Printer myDefaultPrinter() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        LambdaQueryWrapper<PrinterUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrinterUser::getUserId,info.getUserId());
        PrinterUser printerUser = printerUserMapper.selectOne(wrapper);
        if(ObjectUtil.isNotEmpty(printerUser)){
            Printer printer = printerMapper.selectById(printerUser.getPrinterId());
            return printer;
        }else{
            return null;
        }
    }

    @Override
    public void del(Long printerId) {
        LambdaQueryWrapper<PrinterUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrinterUser::getPrinterId,printerId);
        printerUserMapper.delete(wrapper);
        printerMapper.deleteById(printerId);
    }
}
