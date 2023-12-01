package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.zmu.cloud.commons.service.PigLaborService;
import com.zmu.cloud.commons.service.PigTransferService;
import com.zmu.cloud.commons.service.ProductionDayService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigLaborVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class PigLaborServiceImpl extends ServiceImpl<PigLaborMapper, PigLabor> implements PigLaborService {

    final PigHouseService houseService;
    final PigBreedingMapper pigBreedingMapper;
    final PigPiggyMapper pigPiggyMapper;
    final PigLaborTaskMapper pigLaborTaskMapper;
    final PigPregnancyTaskMapper pigPregnancyTaskMapper;
    final ProductionDayService productionDayService;
    final PigTransferService transferService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PigLabor pigLabor) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigLabor.getPigBreedingId());
        //是否离场
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        //如果当前状态不为“妊娠”
        if (pigBreeding.getPigStatus() != PigBreedingStatusEnum.PREGNANCY.getStatus()) {
            throw new BaseException("当前种猪状态不正确，无法分娩");
        }
        //检查分娩位置是否为分娩舍
        PigHouse laborHouse = houseService.findByCache(pigLabor.getPigHouseId());
        if (ObjectUtil.notEqual(laborHouse.getType(), 1)) {
            throw new BaseException("请在分娩舍进行分娩");
        }
        Integer count = pigLabor.getHealthyNumber();
        if (ObjectUtil.isNotEmpty(pigLabor.getWeakNumber())) {
            count = count + pigLabor.getWeakNumber();
        }
        if (ObjectUtil.isNotEmpty(pigLabor.getDeformityNumber())) {
            count = count + pigLabor.getDeformityNumber();
        }
        if (count <= 0) {
            throw new BaseException("活仔数不能小于0");
        }
        //判断活仔母是否大于所有活的产仔数
        if (ObjectUtil.isNotEmpty(pigLabor.getLiveNumber())) {
            if (pigLabor.getLiveNumber() > count) {
                throw new BaseException("活仔母不能大于活的产仔数");
            }
        }
        Date lastStatusTime = pigBreeding.getStatusTime();
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        pigBreeding.setUpdateBy(userId);
        pigBreeding.setPigStatus(PigBreedingStatusEnum.LACTATION.getStatus());
        pigBreeding.setStatusTime(pigLabor.getLaborDate());
        pigBreedingMapper.updateById(pigBreeding);
        //转猪
        if (ObjectUtil.notEqual(pigLabor.getPigHouseId(), pigBreeding.getPigHouseId())) {
            TransferPigDTO dto = new TransferPigDTO();
            dto.setPigs(Collections.singletonList(pigBreeding.getId()));
            transferService.transferPig(pigLabor.getPigHouseId(), Collections.singletonList(dto));
        }
        //结束妊娠
        productionDayService.insertOrUpdate(pigBreeding.getId(), lastStatusTime, pigLabor.getLaborDate());
        //添加数据
//        可饲养仔猪数：健仔+弱仔+畸形-处死活仔
        pigLabor.setFeedingNumber(
                (ObjectUtil.isEmpty(pigLabor.getHealthyNumber())?0:pigLabor.getHealthyNumber()) +
                (ObjectUtil.isEmpty(pigLabor.getWeakNumber())?0:pigLabor.getWeakNumber()) -
                (ObjectUtils.isEmpty(pigLabor.getKilledNumber())?0:pigLabor.getKilledNumber()));
        if (ObjectUtil.isEmpty(pigLabor.getLaborResult())) {
            pigLabor.setLaborResult(1);//默认顺产
        }
        if (ObjectUtil.isEmpty(pigLabor.getOperatorId())) {
            pigLabor.setOperatorId(userId);
        }
        pigLabor.setPigHouseId(laborHouse.getId());
        pigLabor.setPigHouseName(laborHouse.getName());
//        if (ObjectUtil.isNotEmpty(pigBreeding.getPigHouseId())) {
//            PigHouse house = houseService.findByCache(pigBreeding.getPigHouseId());
//            pigLabor.setPigHouseId(house.getId());
//            pigLabor.setPigHouseName(house.getName());
//        }
        pigLabor.setParity(pigBreeding.getParity());
        pigLabor.setCreateBy(userId);
        baseMapper.insert(pigLabor);
        //添加猪仔入场, 以栋舍为维度
        LambdaQueryWrapper<PigPiggy> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PigPiggy::getPigHouseId, pigLabor.getPigHouseId());
        PigPiggy pigPiggy = pigPiggyMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(pigPiggy)) {
              PigPiggy build = PigPiggy.builder()
                      .pigHouseId(pigLabor.getPigHouseId())
                      .number(pigLabor.getFeedingNumber())
                      .createBy(userId).build();
            pigPiggyMapper.insert(build);
        } else {
            pigPiggy.setNumber(pigPiggy.getNumber() + pigLabor.getFeedingNumber());
            pigPiggyMapper.updateById(pigPiggy);
        }
        //修改任务，判断是否有分娩任务，如果有的话，就将分娩任务改为已处理
        PigLaborTask pigLaborTask = pigLaborTaskMapper.selectByPigBreedingId(pigBreeding.getId());
        if (!ObjectUtils.isEmpty(pigLaborTask)) {
            pigLaborTask.setStatus(2);
            pigLaborTaskMapper.updateById(pigLaborTask);
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
    public void batchAdd(List<PigLabor> pigLabor) {
        pigLabor = pigLabor.stream().filter(m -> ObjectUtil.isNotEmpty(m.getPigBreedingId())).collect(Collectors.toList());
        Map<Long, List<PigLabor>> pigMaps = pigLabor.stream().collect(Collectors.groupingBy(PigLabor::getPigBreedingId));
        pigMaps.forEach((key, value) -> {
            if (value.size() > 1) {
                throw new BaseException("猪只[ %s ]重复", pigBreedingMapper.selectById(value.get(0).getPigBreedingId()).getEarNumber());
            }
        });
        pigLabor.forEach(this::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PigLabor pigLabor) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigLabor.getPigBreedingId());
        //是否离场
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        //如果当前状态不为“哺乳”
        if (pigBreeding.getPigStatus() != PigBreedingStatusEnum.LACTATION.getStatus()) {
            throw new BaseException("当前种猪状态不正确");
        }
        //修改猪仔的个数
        Integer count = pigLabor.getHealthyNumber();
        if (!ObjectUtils.isEmpty(pigLabor.getWeakNumber())) {
            count = count + pigLabor.getWeakNumber();
        }else {
            pigLabor.setWeakNumber(0);
        }
        if (!ObjectUtils.isEmpty(pigLabor.getDeformityNumber())) {
            count = count + pigLabor.getDeformityNumber();
        }else {
            pigLabor.setDeformityNumber(0);
        }
        if (count <= 0) {
            throw new BaseException("活仔数不能小于0");
        }
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        pigBreeding.setUpdateBy(userId);
        pigBreeding.setStatusTime(pigLabor.getLaborDate());
        pigBreedingMapper.updateById(pigBreeding);
        pigLabor.setUpdateBy(userId);
        baseMapper.updateById(pigLabor);

        LambdaQueryWrapper<PigPiggy> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PigPiggy::getPigBreedingId, pigBreeding.getId());
        PigPiggy pigPiggy = pigPiggyMapper.selectOne(queryWrapper);
//        pigPiggy.setNumber(count);
        pigPiggy.setNumber(pigLabor.getHealthyNumber()
                + pigLabor.getWeakNumber()
                + pigLabor.getDeformityNumber()
                - (ObjectUtils.isEmpty(pigLabor.getKilledNumber())?0:pigLabor.getKilledNumber()));
        pigPiggyMapper.updateById(pigPiggy);
    }

    @Override
    public PageInfo<EventPigLaborVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigLaborVO> eventPigLaborVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigLaborVOS);
    }
}
