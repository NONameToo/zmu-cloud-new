package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zmu.cloud.commons.dto.BlendFeedColumnDto;
import com.zmu.cloud.commons.dto.BlendFeedParam;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.app.ColumnOperateType;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.BlendFeedColumnVo;
import com.zmu.cloud.commons.vo.BlendFeedVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlendFeedServiceImpl implements BlendFeedService {

    final BlendFeedMapper blendFeedMapper;
    final BlendFeedColumnService blendFeedColumnService;
    final BlendFeedColumnMapper blendFeedColumnMapper;
    final GatewayService gatewayService;
    final RedissonClient redissonClient;
//    final PigFarmTaskService farmTaskService;
    final PigFarmTaskMapper farmTaskMapper;
    final PigHouseColumnsService columnsService;
    final PigHouseMapper houseMapper;
    final MaterialLineMapper materialLineMapper;

    @Override
    public BlendFeedVo find() {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        BlendFeedVo.BlendFeedVoBuilder builder = BlendFeedVo.builder();
        Optional<BlendFeed> opt = query(farmId);
        if (opt.isPresent()) {
            BlendFeed feed = opt.get();
            builder.id(feed.getId())
                    .firstAmount(feed.getFirstAmount())
                    .feedAgain(feed.getFeedAgain())
                    .feedAgainAmount(feed.getFeedAgainAmount());
            if (ObjectUtil.isNotEmpty(feed.getFeedAgainTime())) {
                builder.feedAgainTime(feed.getFeedAgainTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            List<BlendFeedColumn> fields = blendFeedColumnMapper.selectList(Wrappers.lambdaQuery(BlendFeedColumn.class)
                    .eq(BlendFeedColumn::getBlendFeedId, feed.getId()));
            if (ObjectUtil.isNotEmpty(fields)) {
                Map<String, List<BlendFeedColumnVo>> rows = fields.stream()
                        .map(f -> {
                            BlendFeedColumnVo vo = new BlendFeedColumnVo();
                            BeanUtil.copyProperties(f, vo);
                            vo.setRow(f.getPosition().substring(0, f.getPosition().lastIndexOf("-")));
                            vo.setPosition(f.getPosition().substring(f.getPosition().lastIndexOf("-") + 1));
                            return vo;
                        }).collect(Collectors.groupingBy(BlendFeedColumnVo::getRow));
                builder.feedFields(rows);
            }
        }
        return builder.build();
    }

    private Optional<BlendFeed> query(Long farmId) {
        return Optional.ofNullable(blendFeedMapper.selectOne(Wrappers.lambdaQuery(BlendFeed.class)
                .eq(BlendFeed::getFarmId, farmId)));
    }

    @Override
    public void config(BlendFeedParam blendFeedParam) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        if (1 == blendFeedParam.getFeedAgain() &&
                (ObjectUtil.hasEmpty(blendFeedParam.getFeedAgainTime(), blendFeedParam.getFeedAgainTime()) || blendFeedParam.getFeedAgainAmount() <= 0)) {
            throw new BaseException("启用二次下料时，请填写下料时间和饲喂量，且饲喂量大于0");
        }

        BlendFeed feed;
        PigFarmTask task;
        if (ObjectUtil.isEmpty(blendFeedParam.getId())) {
            feed = new BlendFeed();
            feed.setFarmId(farmId);
            feed.setFirstAmount(blendFeedParam.getFirstAmount());
            feed.setFeedAgain(blendFeedParam.getFeedAgain());
            if (1 == blendFeedParam.getFeedAgain() &&
                    ObjectUtil.hasEmpty(blendFeedParam.getFeedAgainTime(), blendFeedParam.getFeedAgainAmount())) {
                throw new BaseException("二次下料时，请填写下料时间和饲喂量");
            }
            feed.setFeedAgainTime(LocalDateTimeUtil.parse(blendFeedParam.getFeedAgainTime(), "HH:mm").toLocalTime());
            feed.setFeedAgainAmount(blendFeedParam.getFeedAgainAmount());
//            task = farmTaskService.buildBlendTask(farmId, feed.getFeedAgainTime(), feed.getFeedAgain());
//            feed.setFeedAgainTaskId(task.getId());
            blendFeedMapper.insert(feed);
        } else {
            feed = blendFeedMapper.selectById(blendFeedParam.getId());
            feed.setFirstAmount(blendFeedParam.getFirstAmount());
            feed.setFeedAgain(blendFeedParam.getFeedAgain());
            if (1 == blendFeedParam.getFeedAgain() &&
                    ObjectUtil.hasEmpty(blendFeedParam.getFeedAgainTime(), blendFeedParam.getFeedAgainAmount())) {
                throw new BaseException("二次下料时，请填写下料时间和饲喂量");
            }
            feed.setFeedAgainTime(LocalDateTimeUtil.parse(blendFeedParam.getFeedAgainTime(), "HH:mm").toLocalTime());
            feed.setFeedAgainAmount(blendFeedParam.getFeedAgainAmount());
            blendFeedMapper.updateById(feed);
            task = farmTaskMapper.selectById(feed.getFeedAgainTaskId());
            task.setTaskEnable(feed.getFeedAgain());
            task.setTaskTime(feed.getFeedAgainTime());
            task.setTaskCron(task.getTaskTime().format(DateTimeFormatter.ofPattern("ss mm HH * * ?")));
            farmTaskMapper.updateById(task);
        }
    }

    @Override
    public void save(List<Long> colIds) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        Long farmId = info.getPigFarmId();
        if (ObjectUtil.isNotEmpty(colIds)) {
            Optional<BlendFeed> opt = query(farmId);
            if (!opt.isPresent()) throw new BaseException("请先保存二次下料配置信息");
            BlendFeed feed = opt.get();
            List<PigHouseColumns> cols = columnsService.listByIds(colIds);
            List<BlendFeedColumn> blendFeedFields = cols.stream().map(f -> {
                if (notExists(farmId, feed.getId(), f.getId())) {
                    BlendFeedColumn feedColumn = new BlendFeedColumn();
                    feedColumn.setBlendFeedId(feed.getId());
                    feedColumn.setFarmId(feed.getFarmId());
                    feedColumn.setHouseId(f.getPigHouseId());
                    feedColumn.setColId(f.getId());
                    feedColumn.setPosition(f.getPosition());
                    return feedColumn;
                }
                return null;
            }).filter(ObjectUtil::isNotNull).collect(toList());
            blendFeedColumnService.saveBatch(blendFeedFields);
            //检查是否都绑定了饲喂器、是否在同一条料线下面
            feed.setMaterialLineId(check(farmId, feed.getId()));
            blendFeedMapper.updateById(feed);
            MaterialLine line = materialLineMapper.selectById(feed.getMaterialLineId());
            PigHouse house = houseMapper.selectById(line.getPigHouseId());
            PigFarmTask task = farmTaskMapper.selectById(feed.getFeedAgainTaskId());
            task.setMaterialLineId(line.getId());
            task.setPigHouseId(line.getPigHouseId());
            task.setPigHouseType(house.getType());
            farmTaskMapper.updateById(task);
        }
        redissonClient.getMap(ColumnOperateType.vagueTransferPig.key() + info.getUserId()).delete();
    }

    @Override
    public void remove(List<Long> colIds) {
        blendFeedColumnMapper.delete(Wrappers.lambdaQuery(BlendFeedColumn.class).in(BlendFeedColumn::getColId, colIds));
    }

    @Override
    public Optional<BlendFeedColumnDto> isBlendFeedField(Long colId) {
        BlendFeedColumnDto.BlendFeedColumnDtoBuilder builder = BlendFeedColumnDto.builder();
        BlendFeedColumn col = blendFeedColumnMapper.selectOne(Wrappers.lambdaQuery(BlendFeedColumn.class)
                .eq(BlendFeedColumn::getColId, colId));
        if (ObjectUtil.isNotEmpty(col)) {
            BlendFeed feed = blendFeedMapper.selectById(col.getBlendFeedId());
            builder.firstAmount(feed.getFirstAmount());
            builder.feedAgain(feed.getFeedAgain());
            builder.feedAgainAmount(feed.getFeedAgainAmount());
            return Optional.of(builder.build());
        }
        return Optional.empty();
    }

    private boolean notExists(Long farmId, Long feedId, Long colId) {
        return blendFeedColumnMapper.exists(Wrappers.lambdaQuery(BlendFeedColumn.class)
                .eq(BlendFeedColumn::getFarmId, farmId)
                .eq(BlendFeedColumn::getBlendFeedId, feedId)
                .eq(BlendFeedColumn::getColId, colId)
        );
    }

    /**
     * 检查是否都绑定了饲喂器、是否在同一条料线下面
     *
     * @param feedId
     */
    private Long check(Long farmId, Long feedId) {
        List<PigHouseColumns> fields = findByFeedId(farmId, feedId);
        String unBindFields = fields.stream().filter(f -> ObjectUtil.hasEmpty(f.getClientId(), f.getFeederCode()))
                .map(PigHouseColumns::getPosition).collect(Collectors.joining());
        if (ObjectUtil.isNotEmpty(unBindFields)) {
            throw new BaseException("选择的栏位{}未绑定饲喂器", unBindFields);
        }
        List<Gateway> gateways = gatewayService.gateways(fields.stream().map(PigHouseColumns::getClientId).collect(toList()));
        Set<Long> mlIds = gateways.stream().map(Gateway::getMaterialLineId).filter(Objects::nonNull).collect(toSet());
        if (ObjectUtil.isEmpty(mlIds) || mlIds.size() > 1) {
            throw new BaseException("选择的栏位主机未配置料线或不在同一条料线下");
        }
        return mlIds.iterator().next();
    }

    @Override
    public List<PigHouseColumns> findByFeedId(Long farmId, Long feedId) {
        List<Long> fieldIds = blendFeedColumnService.listObjs(Wrappers.lambdaQuery(BlendFeedColumn.class)
                .select(BlendFeedColumn::getColId)
                .eq(BlendFeedColumn::getFarmId, farmId)
                .eq(BlendFeedColumn::getBlendFeedId, feedId), o -> (Long)o);
        return columnsService.listByIds(fieldIds);
    }

    @Override
    public List<PigHouseColumns> findByFarmId(Long farmId, Integer feedingAmount) {
        Optional<BlendFeed> opt = query(farmId);
        if (opt.isPresent()) {
            BlendFeed feed = opt.get();
            feedingAmount = feed.getFeedAgainAmount();
            return findByFeedId(farmId, feed.getId());
        }
        return ListUtil.empty();
    }
}
