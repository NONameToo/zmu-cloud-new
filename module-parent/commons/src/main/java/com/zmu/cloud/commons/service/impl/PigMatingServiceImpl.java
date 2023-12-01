package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.zmu.cloud.commons.service.PigMatingService;
import com.zmu.cloud.commons.service.PigTransferService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigMatingVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zmu.cloud.commons.enums.PigBreedingStatusEnum.RESERVE;
import static com.zmu.cloud.commons.enums.PigBreedingStatusEnum.WEANING;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PigMatingServiceImpl extends ServiceImpl<PigMatingMapper, PigMating> implements PigMatingService {

    final PigHouseService houseService;
    final PigBreedingMapper pigBreedingMapper;
    final PigMatingTaskMapper pigMatingTaskMapper;
    final PigPregnancyTaskMapper pigPregnancyTaskMapper;
    final PigTransferService transferService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PigMating pigMating) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigMating.getPigBreedingId());
        //是否离场
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        if (pigBreeding.getType() == 1) {
            throw new BaseException("当前猪只是公猪不能配种");
        }
        //如果当前状态不为“后备”，“流产”，“返情”，“空怀”，“断奶”
        if (pigBreeding.getPigStatus() != PigBreedingStatusEnum.RESERVE.getStatus()
                && pigBreeding.getPigStatus() != PigBreedingStatusEnum.ABORTION.getStatus()
                && pigBreeding.getPigStatus() != PigBreedingStatusEnum.EMPTY.getStatus()
                && pigBreeding.getPigStatus() != WEANING.getStatus()
                && pigBreeding.getPigStatus() != PigBreedingStatusEnum.RETURN.getStatus()) {
            throw new BaseException("当前种猪状态不正确，无法配种");
        }
//        PigPiggy pigPiggy = pigPiggyMapper.selectByPigBreedingId(pigBreeding.getId());
//        if (!ObjectUtils.isEmpty(pigPiggy) && pigPiggy.getNumber()>0){
//            throw new BaseException("当前种猪还有未断奶仔猪，不能进行配种");
//        }
        //如果当前种猪状态为8,表示断奶，就表示下一次胎次



        PigHouse matingHouse = houseService.findByCache(pigMating.getPigHouseId());

        if (Stream.of(RESERVE.getStatus(), WEANING.getStatus()).anyMatch(s -> s.equals(pigBreeding.getPigStatus()))) {
            pigBreeding.setParity(pigBreeding.getParity() + 1);
        }
        Long userId = RequestContextUtils.getUserId();
        pigMating.setParity(pigBreeding.getParity());
        //设置配种前置状态
        pigMating.setBeforePigStatus(pigBreeding.getPigStatus());
        if (ObjectUtil.isEmpty(pigMating.getOperatorId())) {
            pigMating.setOperatorId(userId);
        }

        //修改胎次，修改状态，状态时间

        pigBreeding.setUpdateBy(userId);
        pigBreeding.setPigStatus(PigBreedingStatusEnum.MATING.getStatus());
        pigBreeding.setStatusTime(pigMating.getMatingDate());
        pigBreedingMapper.updateById(pigBreeding);

        //转猪
        if (ObjectUtil.isNotNull(pigMating.getPigHouseId()) && ObjectUtil.notEqual(pigMating.getPigHouseId(), pigBreeding.getPigHouseId())) {
            TransferPigDTO dto = new TransferPigDTO();
            dto.setPigs(Collections.singletonList(pigBreeding.getId()));
            transferService.transferPig(pigMating.getPigHouseId(), Collections.singletonList(dto));
        }

        if (ObjectUtil.isNotEmpty(pigBreeding.getPigHouseId())) {
            PigHouse house = houseService.findByCache(pigBreeding.getPigHouseId());
            pigMating.setPigHouseId(house.getId());
            pigMating.setPigHouseName(house.getName());
        }

        //添加一条配种记录
        baseMapper.insert(pigMating);


        //查询任务中是否有配种任务，如果有的话，就将对应的配种任务改为已处理
        PigMatingTask pigMatingTask = pigMatingTaskMapper.selectByPigBreedingId(pigMating.getPigBreedingId());
        if (!ObjectUtil.isEmpty(pigMatingTask)) {
            pigMatingTask.setStatus(2);
            pigMatingTaskMapper.updateById(pigMatingTask);
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
    public void batchAdd(List<PigMating> pigMating) {
        pigMating = pigMating.stream().filter(m -> ObjectUtil.isNotEmpty(m.getPigBreedingId())).collect(Collectors.toList());
        Map<Long, List<PigMating>> pigMaps = pigMating.stream().collect(Collectors.groupingBy(PigMating::getPigBreedingId));
        pigMaps.forEach((key, value) -> {
            if (value.size() > 1) {
                throw new BaseException("猪只[ %s ]重复", pigBreedingMapper.selectById(value.get(0).getPigBreedingId()).getEarNumber());
            }
        });
        pigMating.forEach(this::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PigMating pigMating) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigMating.getPigBreedingId());
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        if (pigBreeding.getType() == 1) {
            throw new BaseException("当前猪只是公猪不能配种");
        }
        //如果当前猪的状态不为配种，就不能修改
        if (pigBreeding.getPigStatus() != PigBreedingStatusEnum.MATING.getStatus()) {
            throw new BaseException("当前种猪状态不正确");
        }
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        pigBreeding.setUpdateBy(userId);
        pigBreeding.setStatusTime(pigMating.getMatingDate());
        pigBreedingMapper.updateById(pigBreeding);
        pigMating.setUpdateBy(userId);
        baseMapper.updateById(pigMating);
    }

    @Override
    public PageInfo<EventPigMatingVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigMatingVO> eventPigMatingVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigMatingVOS);
    }

    @Override
    public PigMating findByParity(Long pig, int parity) {
        return baseMapper.selectOne(Wrappers.lambdaQuery(PigMating.class).eq(PigMating::getPigBreedingId, pig)
                .eq(PigMating::getParity, parity).orderByDesc(PigMating::getMatingDate, PigMating::getCreateTime).last("limit 1"));
    }
}
