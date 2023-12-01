package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.*;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigWeanedVO;
import com.zmu.cloud.commons.vo.PigBreedingListVO;
import com.zmu.cloud.commons.vo.PigPiggyListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shining
 */
@Service
@RequiredArgsConstructor
public class PigWeanedServiceImpl extends ServiceImpl<PigWeanedMapper, PigWeaned> implements PigWeanedService {

    final PigBreedingMapper pigBreedingMapper;
    final PigBreedingService pigBreedingService;
    final PigPiggyMapper pigPiggyMapper;
    final PigPorkMapper pigPorkMapper;
    final PigPorkStockMapper pigPorkStockMapper;
    final PigGroupMapper pigGroupMapper;
    final PigWeanedTaskMapper pigWeanedTaskMapper;
    final ProductionDayService productionDayService;
    final PigPiggyLeaveService pigPiggyLeaveService;
    final PigPiggyService pigPiggyService;
    final PigTransferService transferService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PigWeaned pigWeaned) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigWeaned.getPigBreedingId());
        //是否离场
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        //如果当前状态不为“哺乳”
        if (pigBreeding.getPigStatus() != PigBreedingStatusEnum.LACTATION.getStatus()) {
            throw new BaseException("当前种猪状态不正确");
        }
        //断奶的数是否在到最大值
//        PigStockDTO pigStockDTO = pigBreedingMapper.count(pigWeaned.getPigHouseColumnsId());
//        if (pigStockDTO.getTotal() + pigWeaned.getWeanedNumber() > pigStockDTO.getMaxPerColumns()) {
//            throw new BaseException("当前存栏数达到最大值");
//        }
        //查询总产仔数量
        LambdaQueryWrapper<PigPiggy> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(PigPiggy::getPigBreedingId, pigBreeding.getId());
        PigPiggy pigPiggy = pigPiggyMapper.selectOne(queryWrapper1);
        if (ObjectUtils.isEmpty(pigPiggy)){
            throw new BaseException("系统异常，分娩小猪不存在，请联系管理员");
        }
        //如果当前断奶的数量和已断奶的数量大于总产仔数
        if (pigWeaned.getWeanedNumber() > pigPiggy.getNumber()) {
            throw new BaseException("断奶数超过总产仔数");
        }

        Long userId = RequestContextUtils.getUserId();

        //将断奶猪的数量减去，加入肉猪记录
        pigPiggy.setNumber(pigPiggy.getNumber() - pigWeaned.getWeanedNumber());
        pigPiggy.setUpdateBy(userId);
        pigPiggyMapper.updateById(pigPiggy);
        //如果断奶数量少于产仔数量，则剩下的仔猪做自动离场处理
        if (pigPiggy.getNumber() > 0) {
            PigPiggyLeave leave = PigPiggyLeave.builder()
                    .pigPiggyId(pigPiggy.getId())
                    .leaveTime(pigWeaned.getWeanedDate())
                    .number(pigPiggy.getNumber())
                    .type(1)
                    .leavingReason(13)
                    .weight(new BigDecimal(0))
                    .remark("未断奶的仔猪自动离场")
                    .operatorId(pigWeaned.getOperatorId())
                    .build();
            pigPiggyLeaveService.leave(leave);
        }

        //将猪的状态改为断奶
        pigBreeding.setPigStatus(PigBreedingStatusEnum.WEANING.getStatus());
        //进入断奶状态说明已经结束哺乳状态了，更新哺乳状态的状态天数
        productionDayService.insertOrUpdate(pigBreeding.getId(), pigBreeding.getStatusTime(), pigWeaned.getWeanedDate());
//        if (ObjectUtil.isNotEmpty(pigWeaned.getHouseId())) {
//            pigBreeding.setPigHouseId(pigWeaned.getHouseId());
//            pigBreeding.setPigHouseColumnsId(null);
//        }
        pigBreeding.setUpdateBy(userId);
        pigBreeding.setStatusTime(pigWeaned.getWeanedDate());
        pigBreedingMapper.update(pigBreeding, new UpdateWrapper<PigBreeding>()
                .lambda().set(PigBreeding::getPigHouseColumnsId, null).eq(PigBreeding::getId, pigBreeding.getId()));

        //如果groupId为空就新建一个猪群
//        if (ObjectUtils.isEmpty(pigWeaned.getPigGroupId())) {
//            if (ObjectUtils.isEmpty(pigWeaned.getPigGroupName())) {
//                //如果猪群的名字为空，就自动生成一个,查询今天生成的数量
//                Integer count = pigGroupMapper.selectCountByColumnsId(pigWeaned.getPigHouseColumnsId());
//                String name = DateUtil.date().toString("yyyyMMdd") + "_" + count;
//                pigWeaned.setPigGroupName(name);
//            }
//            PigGroup pigGroup = PigGroup.builder()
//                    .pigHouseColumnsId(pigWeaned.getPigHouseColumnsId()).name(pigWeaned.getPigGroupName())
//                    .createBy(userId).build();
//            pigGroupMapper.insert(pigGroup);
//            pigWeaned.setPigGroupId(pigGroup.getId());
//        }
        //添加断奶记录
        if (ObjectUtil.isEmpty(pigWeaned.getOperatorId())) {
            pigWeaned.setOperatorId(userId);
        }
        pigWeaned.setCreateBy(userId);
        pigWeaned.setParity(pigBreeding.getParity());
        baseMapper.insert(pigWeaned);
        //添加肉猪库存数,如果存在，就直接修改在场（type=1）的库存，如果不存在就添加一条记录
//        LambdaQueryWrapper<PigPorkStock> queryWrapper2 = new LambdaQueryWrapper<>();
//        queryWrapper2.eq(PigPorkStock::getPigHouseColumnsId, pigWeaned.getPigHouseColumnsId());
//        queryWrapper2.eq(PigPorkStock::getType, 1);
//        queryWrapper2.eq(PigPorkStock::getPigGroupId, pigWeaned.getPigGroupId());
//
//        PigPorkStock pigPorkStock = pigPorkStockMapper.selectOne(queryWrapper2);
//        if (ObjectUtils.isEmpty(pigPorkStock)) {
//            PigPorkStock build = PigPorkStock.builder()
//                    .porkNumber(pigWeaned.getWeanedNumber()).pigHouseColumnsId(pigWeaned.getPigHouseColumnsId())
//                    .type(1).createBy(userId).pigGroupId(pigWeaned.getPigGroupId()).build();
//            pigPorkStockMapper.insert(build);
//        } else {
//            pigPorkStock.setPorkNumber(pigPorkStock.getPorkNumber() + pigWeaned.getWeanedNumber());
//            pigPorkStock.setUpdateBy(userId);
//            pigPorkStockMapper.updateById(pigPorkStock);
//        }
        //添加肉猪入场记录
//        PigPork build2 = PigPork.builder().approachTime(DateUtil.date())
//                .pigHouseColumnsId(pigWeaned.getPigHouseColumnsId())
//                .approachType(3)
//                .pigGroupId(pigWeaned.getPigGroupId())
//                .birthDate(DateUtil.date())
//                .number(pigWeaned.getWeanedNumber())
//                .variety(pigBreeding.getVariety())
//                .weight(pigWeaned.getWeanedWeight())
//                .createBy(userId)
//                .operatorId(userId).build();
//        pigPorkMapper.insert(build2);
        //修改任务，状态，这里需要判断这只猪是否都断奶完成，全部断奶，完成后,才会把状态把种猪的状态改为断奶
        // 所以修改任务的时候，如果没有全部奶，这个任务也会一直挂起，只有断奶完成后，才会把任务改为已处理
        if (pigBreeding.getPigStatus().equals(PigBreedingStatusEnum.WEANING.getStatus())) {
            PigWeanedTask pigWeanedTask = pigWeanedTaskMapper.selectByPigBreedingId(pigBreeding.getId());
            if (ObjectUtil.isNotEmpty(pigWeanedTask)) {
                pigWeanedTask.setStatus(2);
                pigWeanedTaskMapper.updateById(pigWeanedTask);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void weaned(WeanedDto dto) {
        //待断奶的母猪
        List<PigBreeding> waitWeaned = pigBreedingMapper.selectList(Wrappers.lambdaQuery(PigBreeding.class)
                .eq(PigBreeding::getPigHouseId, dto.getHouseId())
                .eq(PigBreeding::getPigStatus, PigBreedingStatusEnum.LACTATION.getStatus())
                .eq(PigBreeding::getPresenceStatus, 1)
                .eq(PigBreeding::isDel, 0));
        if (ObjectUtil.isEmpty(waitWeaned)) {
            throw new BaseException("该栋舍已断完！");
        }
        //待断奶的仔猪
//        PigPiggy pigPiggy = pigPiggyService.findByHouse(dto.getHouseId());
//        if (ObjectUtil.isEmpty(pigPiggy)) {
//            throw new BaseException("该栋舍已没有仔猪！");
//        }

        //不断奶总数
//        Integer count = dto.getUnWeanedPigs().stream().filter(un -> ObjectUtil.isNotEmpty(un) && ObjectUtil.isNotEmpty(un.getAmount()))
//                .mapToInt(UnWeanedPig::getAmount).sum();
        //待断奶的仔猪数
//        Integer num = pigPiggy.getNumber();
//        if (count > num) {
//            throw new BaseException("不断奶仔猪总数已超过待断奶仔猪总数，请确认修改！");
//        } else if (ObjectUtil.equals(count, num)) {
//            throw new BaseException("不断奶仔猪总数 等于 待断奶仔猪总数，无需断奶！");
//        }

        Long userId = RequestContextUtils.getUserId();

        waitWeaned = waitWeaned.stream()
                .filter(vo -> dto.getUnWeanedPigs().stream().noneMatch(pig -> ObjectUtil.equals(pig.getPigId(), vo.getId())))
                .collect(Collectors.toList());

//        pigPiggy.setNumber(count);
//        pigPiggyMapper.updateById(pigPiggy);
        //修改断奶母猪状态
        waitWeaned.forEach(pig -> {
            //断奶栋舍
            if (ObjectUtil.isEmpty(dto.getHouseId())) {
                dto.setHouseId(pig.getPigHouseId());
            }
            //将猪的状态改为断奶
            pig.setPigStatus(PigBreedingStatusEnum.WEANING.getStatus());
            //进入断奶状态说明已经结束哺乳状态了，更新哺乳状态的状态天数
            productionDayService.insertOrUpdate(pig.getId(), pig.getStatusTime(), dto.getWeanedDate());
            //母猪转入
//            if (ObjectUtil.isNotEmpty(dto.getInHouseId())) {
//                pig.setPigHouseId(dto.getInHouseId());
//            }
            pig.setUpdateBy(userId);
            pig.setStatusTime(dto.getWeanedDate());
            pigBreedingMapper.update(pig, new UpdateWrapper<PigBreeding>()
                    .lambda().set(PigBreeding::getPigHouseColumnsId, null).eq(PigBreeding::getId, pig.getId()));

            //转猪
            if (ObjectUtil.isNotEmpty(dto.getInHouseId()) && ObjectUtil.notEqual(dto.getInHouseId(), pig.getPigHouseId())) {
                TransferPigDTO transferPigDTO = new TransferPigDTO();
                transferPigDTO.setPigs(Collections.singletonList(pig.getId()));
                transferService.transferPig(dto.getInHouseId(), Collections.singletonList(transferPigDTO));
            }
        });
        //生成断奶记录
        PigWeaned weaned = PigWeaned.builder()
                .pigHouseId(dto.getHouseId()).weanedDate(dto.getWeanedDate())
//                .weanedNumber(num - count)
                .pigIds(waitWeaned.stream().map(PigBreeding::getId).map(String::valueOf).collect(Collectors.joining(",")))
                .pigEarNumbers(waitWeaned.stream().map(PigBreeding::getEarNumber).collect(Collectors.joining(",")))
                .createBy(userId).operatorId(dto.getOperatorId()).remark(dto.getRemark()).build();
        baseMapper.insert(weaned);
    }

    @Override
    public Integer selectNumber(Long pigBreedingId) {
        LambdaQueryWrapper<PigPiggy> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(PigPiggy::getPigBreedingId, pigBreedingId);
        PigPiggy pigPiggy = pigPiggyMapper.selectOne(queryWrapper1);
        if (ObjectUtils.isEmpty(pigPiggy)) {
            return 0;
        }
        return pigPiggy.getNumber();
    }

    @Override
    public PageInfo<EventPigWeanedVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigWeanedVO> eventPigWeanedVOList = baseMapper.event(queryPig);
        return PageInfo.of(eventPigWeanedVOList);
    }

    @Override
    public void singleWeaned(Long pigBreedingId) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigBreedingId);
        //是否离场
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        //如果当前状态不为“哺乳”
        if (pigBreeding.getPigStatus() != PigBreedingStatusEnum.LACTATION.getStatus()) {
            throw new BaseException("当前种猪状态不正确");
        }
        //修改母猪状态
        pigBreeding.setPigStatus(PigBreedingStatusEnum.WEANING.getStatus());
        pigBreedingMapper.updateById(pigBreeding);
    }
}
