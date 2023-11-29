package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zmu.cloud.commons.dto.*;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.app.ColumnOperateType;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.PigTransferVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class PigTransferServiceImpl implements PigTransferService {

    final RedissonClient redis;
    final PigHouseColumnsMapper columnsMapper;
    final PigHouseColumnsService columnsService;
    final PigHouseService houseService;
    //final PigBreedingService breedingService;
    final PigTransferMapper transferMapper;
    final PigTransferDetailMapper transferDetailMapper;
    final PigTransferDetailService transferDetailService;
    final PigBreedingMapper breedingMapper;
    final PigPiggyMapper piggyMapper;

    @Override
    public PigTransferVo transferPig(Long inHouseId, List<TransferPigDTO> pigs) {
        Long userId = RequestContextUtils.getUserId();
        PigHouse inHouse = houseService.findByCache(inHouseId);
        //忽略没猪的栏位
        pigs = pigs.stream().filter(param -> ObjectUtil.isNotEmpty(param.getPigs())).collect(toList());
        //倒叙处理，记录已处理的猪只，如有重复猪只，则忽略
        pigs = Lists.reverse(pigs);
        Set<Long> repeat = new HashSet<>();

        PigTransfer transfer = PigTransfer.builder().houseId(inHouseId).houseName(inHouse.getName())
                .createTime(LocalDateTime.now()).createBy(userId).build();
        transferMapper.insert(transfer);

        List<PigTransferDetail> colExistsPigTransferOutDetail = new ArrayList<>();
        List<PigTransferDetail> details = pigs.stream().flatMap(param -> {
            PigHouseColumns inCol = null;
            if (ObjectUtil.isNotEmpty(param.getColId())) {
                inCol = columnsMapper.selectById(param.getColId());
                //判断inCol栏位是否存在猪只，存在则先转出（只转出栏位栋舍不变）
                PigHouseColumns finalInCol1 = inCol;
                //List<PigBreeding> exists = breedingService.findByCol(inCol.getId());
                List<PigBreeding> exists = breedingMapper.findByCol(inCol.getId());
                //同一个栏位 现存猪只和转入的猪只取差集后 获得需要转出的现存猪只,剩下的猪只保持不变
                Collection<Long> subExists = CollUtil.subtract(exists.stream().map(PigBreeding::getId).collect(toList()), param.getPigs());
                exists.stream().filter(p -> subExists.contains(p.getId())).forEach(p -> {
                    PigHouse house = houseService.findByCache(finalInCol1.getPigHouseId());
                    PigTransferDetail detail = PigTransferDetail.builder()
                            .outHouseId(house.getId())
                            .outHouseName(house.getName())
                            .outColId(finalInCol1.getId())
                            .outColNo(finalInCol1.getNo())
                            .inHouseId(house.getId())
                            .inHouseName(house.getName())
                            .pigId(p.getId()).earNumber(p.getEarNumber())
                            .transferId(transfer.getId()).build();
                    colExistsPigTransferOutDetail.add(detail);

                    breedingMapper.update(p, new LambdaUpdateWrapper<PigBreeding>()
                            .set(PigBreeding::getPigHouseColumnsId, null)
                            .eq(PigBreeding::getId, p.getId()));
                });
            }
            PigHouseColumns finalInCol = inCol;
            return param.getPigs().stream().map(pid ->
                    build(transfer.getId(), pid, inHouse, finalInCol, repeat)
            ).filter(ObjectUtil::isNotEmpty);
        }).collect(toList());
        details.addAll(colExistsPigTransferOutDetail);
        transferDetailService.saveBatch(details);

        transfer.setCnt(repeat.size());
        transferMapper.updateById(transfer);

        //清理转猪栏位缓存
        String key = ColumnOperateType.transferPig.key() + userId;
        RMap<Long, Map<String, Set<Long>>> choseMap = redis.getMap(key);
        choseMap.remove(inHouseId);

        PigTransferVo vo = new PigTransferVo();
        BeanUtil.copyProperties(transfer, vo);
        vo.setDetails(details);
        return vo;
    }

    private PigTransferDetail build(Long transferId, Long pid, @NonNull PigHouse inHouse, PigHouseColumns inCol, Set<Long> repeat) {
        if (!repeat.contains(pid)) {
            repeat.add(pid);
            PigBreeding pig = breedingMapper.selectById(pid);
            PigHouse outHouse = null;
            if (ObjectUtil.isNotEmpty(pig.getPigHouseId())) {
                outHouse = houseService.findByCache(pig.getPigHouseId());
            }
            PigHouseColumns outCol = null;
            if (ObjectUtil.isNotEmpty(pig.getPigHouseColumnsId())) {
                outCol = columnsMapper.selectById(pig.getPigHouseColumnsId());
            }
            move(pig, inHouse.getId(), null==inCol?null:inCol.getId());
            return PigTransferDetail.builder()
                    .outHouseId(null==outHouse?null:outHouse.getId())
                    .outHouseName(null==outHouse?null:outHouse.getName())
                    .outColId(null==outCol?null:outCol.getId())
                    .outColNo(null==outCol?null:outCol.getNo())
                    .inHouseId(inHouse.getId())
                    .inHouseName(inHouse.getName())
                    .inColId(null==inCol?null:inCol.getId())
                    .inColNo(null==inCol?null:inCol.getNo())
                    .pigId(pig.getId()).earNumber(pig.getEarNumber())
                    .transferId(transferId).build();
        }
        return null;
    }

    @Override
    public void move(PigBreeding pig, Long inHouse, Long inCol) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        if (ResourceType.YHY.equals(info.getResourceType())) {
            LambdaUpdateWrapper<PigBreeding> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(PigBreeding::getPigHouseId, inHouse)
                    .set(PigBreeding::getPigHouseColumnsId, inCol)
                    .eq(PigBreeding::getId, pig.getId());
            breedingMapper.update(pig, wrapper);

            //判断母猪状态为哺乳，更新仔猪数据
            if (ObjectUtil.equals(PigBreedingStatusEnum.LACTATION.getStatus(), pig.getPigStatus())) {
                PigPiggy pigPiggy = piggyMapper.selectOne(Wrappers.lambdaQuery(PigPiggy.class).eq(PigPiggy::getPigHouseId, inHouse));
                if (ObjectUtil.isEmpty(pigPiggy)) {
                    pigPiggy = PigPiggy.builder()
                            .pigHouseId(inHouse)
                            .number(0)
                            .createBy(info.getUserId()).build();
                    piggyMapper.insert(pigPiggy);
                }
            }
        }
    }

    @Override
    public PageInfo<PigTransfer> transferPigRecord(BaseQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        List<PigTransfer> transfers = transferMapper.selectList(Wrappers.lambdaQuery(PigTransfer.class)
                .orderByDesc(PigTransfer::getCreateTime));
        transfers.forEach(t -> {
            PigHouse house = houseService.findByCache(t.getHouseId());
            t.setHouseType(house.getType());
        });
        return PageInfo.of(transfers);
    }

    @Override
    public PageInfo<PigTransferDetail> transferPigDetailRecord(TransferPigQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(transferDetailMapper.selectList(Wrappers.lambdaQuery(PigTransferDetail.class)
                .eq(PigTransferDetail::getTransferId, query.getTransferId())
                .orderByAsc(PigTransferDetail::getInColNo)
                .orderByAsc(PigTransferDetail::getId)
        ));
    }

    @Override
    public List<PigTransferDetail> transferPigDetailRecordForBigCol(Long transferId) {
        return transferDetailMapper.selectList(Wrappers.lambdaQuery(PigTransferDetail.class)
                .eq(PigTransferDetail::getTransferId, transferId)
                .orderByAsc(PigTransferDetail::getInColNo));
    }
}
