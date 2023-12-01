package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigBreedingLeaveDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.PigBreedingLeaveService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigBreedingLeaveVO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author shining
 */
@Service
@RequiredArgsConstructor
public class PigBreedingLeaveServiceImpl
        extends ServiceImpl<PigBreedingLeaveMapper, PigBreedingLeave> implements PigBreedingLeaveService {
    final FinancialDataMapper financialDataMapper;
    final FinancialDataTypeMapper financialDataTypeMapper;
    final PigBreedingMapper pigBreedingMapper;
    final PigLaborTaskMapper pigLaborTaskMapper;
    final PigWeanedTaskMapper pigWeanedTaskMapper;
    final PigPregnancyTaskMapper pigPregnancyTaskMapper;
    final PigMatingTaskMapper pigMatingTaskMapper;
    final PigPiggyMapper pigPiggyMapper;
    final RedissonClient redis;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leave(PigBreedingLeaveDTO pigBreedingLeaveDTO) {
        if (CollectionUtils.isEmpty(pigBreedingLeaveDTO.getList())) {
            throw new BaseException("离场的猪不能为空");
        }
        Long userId = RequestContextUtils.getUserId();
        StringBuilder stringBuilder = new StringBuilder();
        for (Long pigBreedingId : pigBreedingLeaveDTO.getList()) {
            PigBreeding pigBreeding = pigBreedingMapper.selectById(pigBreedingId);
            if (pigBreeding.getPresenceStatus() == 2) {
                throw new BaseException("耳号：" + pigBreeding.getEarNumber() + "种猪已离场");
            }
            //判断是否有小猪未断奶
//            PigPiggy pigPiggy = pigPiggyMapper.selectByPigBreedingId(pigBreeding.getId());
//            if (!ObjectUtils.isEmpty(pigPiggy) && pigPiggy.getNumber() != 0) {
//                throw new BaseException("耳号：" + pigBreeding.getEarNumber() + "有小猪未断奶，无法离场");
//            }
            //耳号记录
            stringBuilder.append(pigBreeding.getEarNumber()).append("  ");
            //修改猪状态为离场
            pigBreeding.setPresenceStatus(2);
            pigBreeding.setUpdateBy(userId);
            pigBreedingMapper.updateById(pigBreeding);
            //添加离场记录
            PigBreedingLeave build = PigBreedingLeave.builder().pigBreedingId(pigBreedingId)
                    .leavingReason(pigBreedingLeaveDTO.getLeavingReason())
                    .price(pigBreedingLeaveDTO.getPrice())
                    .unitPrice(pigBreedingLeaveDTO.getUnitPrice()).leaveTime(pigBreedingLeaveDTO.getLeaveTime())
                    .type(pigBreedingLeaveDTO.getType()).weight(pigBreedingLeaveDTO.getWeight())
                    .createBy(userId).operatorId(pigBreedingLeaveDTO.getOperatorId())
                    .remark(pigBreedingLeaveDTO.getRemark()).build();
            baseMapper.insert(build);

            //将任务清空，配种表中有任务
            PigMatingTask pigMatingTask = pigMatingTaskMapper.selectByPigBreedingId(pigBreedingId);
            if (!ObjectUtil.isEmpty(pigMatingTask)) {
                pigMatingTask.setStatus(2);
                pigMatingTaskMapper.updateById(pigMatingTask);
            }
            //如果待妊娠任务表中有对应的任务，就改就对应的任务表修改为处理
            PigPregnancyTask pigPregnancyTask = pigPregnancyTaskMapper.selectByPigBreedingId(pigBreedingId);
            if (!ObjectUtils.isEmpty(pigPregnancyTask)) {
                pigPregnancyTask.setStatus(2);
                pigPregnancyTaskMapper.updateById(pigPregnancyTask);
            }
            //如果待分娩任务表中有对应的任务，就改就对应的任务表修改为处理
            PigLaborTask pigLaborTask = pigLaborTaskMapper.selectByPigBreedingId(pigBreedingId);
            if (!ObjectUtils.isEmpty(pigLaborTask)) {
                pigLaborTask.setStatus(2);
                pigLaborTaskMapper.updateById(pigLaborTask);
            }
            //如果待断奶任务表中有对应的任务，就改就对应的任务表修改为处理
            PigWeanedTask pigWeanedTask = pigWeanedTaskMapper.selectByPigBreedingId(pigBreeding.getId());
            if (!ObjectUtils.isEmpty(pigWeanedTask)) {
                pigWeanedTask.setStatus(2);
                pigWeanedTaskMapper.updateById(pigWeanedTask);
            }

        }
        //添加财务记录
        //查询财务类型为购买的Id
        if (!ObjectUtils.isEmpty(pigBreedingLeaveDTO.getPrice()) &&
                pigBreedingLeaveDTO.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            LambdaQueryWrapper<FinancialDataType> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(FinancialDataType::getDataType, 1);
            FinancialDataType financialDataType = financialDataTypeMapper.selectOne(queryWrapper2);
            //记算单价
            int size = pigBreedingLeaveDTO.getList().size();
            BigDecimal unitPrice = pigBreedingLeaveDTO.getPrice().divide(BigDecimal.valueOf(size),2, RoundingMode.HALF_UP);
            FinancialData build1 = FinancialData.builder().dataTypeId(financialDataType.getId())
                    .income(1).number(size).unitPrice(unitPrice)
                    .totalPrice(pigBreedingLeaveDTO.getPrice()).status(0).createBy(userId)
                    .remark("种猪离场，猪只耳号：" + stringBuilder).build();
            financialDataMapper.insert(build1);
        }

        redis.getSet(CacheKey.Web.simple_pig.key + RequestContextUtils.getRequestInfo().getPigFarmId());
    }

    @Override
    public PageInfo<EventPigBreedingLeaveVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigBreedingLeaveVO> list = baseMapper.event(queryPig);
        return PageInfo.of(list);
    }
}
