package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.dto.TransferPigDTO;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.PigHouseService;
import com.zmu.cloud.commons.service.PigPregnancyService;
import com.zmu.cloud.commons.service.PigTransferService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigPregnancyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class PigPregnancyServiceImpl extends ServiceImpl<PigPregnancyMapper, PigPregnancy> implements PigPregnancyService {

    final PigHouseService houseService;
    final PigBreedingMapper pigBreedingMapper;
    final PigMatingMapper pigMatingMapper;
    final PigPregnancyTaskMapper pigPregnancyTaskMapper;
    final PigMatingTaskMapper pigMatingTaskMapper;
    final PigLaborTaskMapper pigLaborTaskMapper;
    final PigTransferService transferService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PigPregnancy pigPregnancy) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigPregnancy.getPigBreedingId());
        //是否离场
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        //如果当前状态不为“妊娠”，“流产”，“返情”，“配种”
        if (pigBreeding.getPigStatus() != PigBreedingStatusEnum.PREGNANCY.getStatus()
                && pigBreeding.getPigStatus() != PigBreedingStatusEnum.ABORTION.getStatus()
                && pigBreeding.getPigStatus() != PigBreedingStatusEnum.RETURN.getStatus()
                && pigBreeding.getPigStatus() != PigBreedingStatusEnum.MATING.getStatus()) {
            throw new BaseException("当前种猪状态不正确，无法妊检");
        }
        //设置种猪状态
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        pigBreeding.setUpdateBy(userId);
        if (ObjectUtil.isNotEmpty(pigPregnancy.getHouseId())) {
            pigBreeding.setPigHouseId(pigPregnancy.getHouseId());
            pigBreeding.setPigHouseColumnsId(null);
        }

        if (pigPregnancy.getPregnancyResult() == 1) {
            pigBreeding.setPigStatus(PigBreedingStatusEnum.PREGNANCY.getStatus());
        } else if (pigPregnancy.getPregnancyResult() == 2) {
            pigBreeding.setPigStatus(PigBreedingStatusEnum.ABORTION.getStatus());
        } else if (pigPregnancy.getPregnancyResult() == 3) {
            pigBreeding.setPigStatus(PigBreedingStatusEnum.RETURN.getStatus());
        } else if (pigPregnancy.getPregnancyResult() == 4) {
            pigBreeding.setPigStatus(PigBreedingStatusEnum.EMPTY.getStatus());
        }
        pigBreeding.setStatusTime(pigPregnancy.getPregnancyDate());
        pigBreedingMapper.update(pigBreeding, new UpdateWrapper<PigBreeding>()
                .lambda().set(PigBreeding::getPigHouseColumnsId, null).eq(PigBreeding::getId, pigBreeding.getId()));

        //查询该猪最后一次配种的信息，找到对应配种的公猪
        PigMating pigMating = pigMatingMapper.selectByPigBreedingId(pigBreeding.getId());
        if (!ObjectUtils.isEmpty(pigMating)) {
            pigPregnancy.setBoarId(pigMating.getBoarId());
            pigPregnancy.setMatingDate(pigMating.getMatingDate());
        }
        if (ObjectUtil.isEmpty(pigPregnancy.getOperatorId())) {
            pigPregnancy.setOperatorId(userId);
        }
        if (ObjectUtil.isNotEmpty(pigBreeding.getPigHouseId())) {
            PigHouse house = houseService.findByCache(pigBreeding.getPigHouseId());
            pigPregnancy.setPigHouseId(house.getId());
            pigPregnancy.setPigHouseName(house.getName());
        }
        //添加数据“妊娠”，“流产”，“返情”，空怀记录
        pigPregnancy.setParity(pigBreeding.getParity());
        pigPregnancy.setCreateBy(userId);
        baseMapper.insert(pigPregnancy);

        //如果状态为“流产”，“返情”，空怀”，还要看配种里是否有任务，如果有的话，要改为处理状态
        if (pigBreeding.getPigStatus() == PigBreedingStatusEnum.ABORTION.getStatus()
                || pigBreeding.getPigStatus() == PigBreedingStatusEnum.RETURN.getStatus()
                || pigBreeding.getPigStatus() == PigBreedingStatusEnum.EMPTY.getStatus()) {
            PigMatingTask pigMatingTask = pigMatingTaskMapper.selectByPigBreedingId(pigMating.getPigBreedingId());
            if (!ObjectUtil.isEmpty(pigMatingTask)) {
                pigMatingTask.setStatus(2);
                pigMatingTaskMapper.updateById(pigMatingTask);
            }
        }
        //如果状态为妊娠，就要看妊娠任务是否存在，如果存在话，就要把状态改为处理
        if (pigBreeding.getPigStatus() == PigBreedingStatusEnum.PREGNANCY.getStatus()) {
            PigLaborTask pigLaborTask = pigLaborTaskMapper.selectByPigBreedingId(pigBreeding.getId());
            if (!ObjectUtils.isEmpty(pigLaborTask)) {
                pigLaborTask.setStatus(2);
                pigLaborTaskMapper.updateById(pigLaborTask);
            }
        }
        //如果待妊娠任务表中有对应的任务，就改就对应的任务表修改为处理
        PigPregnancyTask pigPregnancyTask = pigPregnancyTaskMapper.selectByPigBreedingId(pigBreeding.getId());
        if (!ObjectUtils.isEmpty(pigPregnancyTask)) {
            pigPregnancyTask.setStatus(2);
            pigPregnancyTaskMapper.updateById(pigPregnancyTask);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(List<PigPregnancy> pigPregnancy) {
        pigPregnancy = pigPregnancy.stream().filter(m -> ObjectUtil.isNotEmpty(m.getPigBreedingId())).collect(Collectors.toList());
        Map<Long, List<PigPregnancy>> pigMaps = pigPregnancy.stream().collect(Collectors.groupingBy(PigPregnancy::getPigBreedingId));
        pigMaps.forEach((key, value) -> {
            if (value.size() > 1) {
                throw new BaseException("猪只[ %s ]重复", pigBreedingMapper.selectById(value.get(0).getPigBreedingId()).getEarNumber());
            }
        });
        pigPregnancy.forEach(this::add);
    }

    @Override
    public void update(PigPregnancy pigPregnancy) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigPregnancy.getPigBreedingId());
        //是否离场
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        //如果当前状态不为“妊娠”，“流产”，“返情”
        if (pigBreeding.getPigStatus() != PigBreedingStatusEnum.PREGNANCY.getStatus()
                && pigBreeding.getPigStatus() != PigBreedingStatusEnum.ABORTION.getStatus()
                && pigBreeding.getPigStatus() != PigBreedingStatusEnum.RETURN.getStatus()) {
            throw new BaseException("当前种猪状态不正确，无法妊检");
        }
        //设置种猪状态
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        if (pigPregnancy.getPregnancyResult() == 1) {
            pigBreeding.setPigStatus(PigBreedingStatusEnum.PREGNANCY.getStatus());
        } else if (pigPregnancy.getPregnancyResult() == 2) {
            pigBreeding.setPigStatus(PigBreedingStatusEnum.ABORTION.getStatus());
        } else if (pigPregnancy.getPregnancyResult() == 3) {
            pigBreeding.setPigStatus(PigBreedingStatusEnum.RETURN.getStatus());
        } else if (pigPregnancy.getPregnancyResult() == 4) {
            pigBreeding.setPigStatus(PigBreedingStatusEnum.EMPTY.getStatus());
        }
        pigBreeding.setUpdateBy(userId);
        pigBreeding.setStatusTime(pigPregnancy.getPregnancyDate());
        pigBreedingMapper.updateById(pigBreeding);
        //修改数据
        pigPregnancy.setUpdateBy(userId);
        baseMapper.updateById(pigPregnancy);
    }

    @Override
    public PageInfo<EventPigPregnancyVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigPregnancyVO> eventPigPregnancyVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigPregnancyVOS);
    }
}
