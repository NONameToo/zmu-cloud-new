package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.PageHelper;
import com.zmu.cloud.commons.dto.*;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.app.ColumnOperateType;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class PigHouseColumnsServiceImpl extends ServiceImpl<PigHouseColumnsMapper, PigHouseColumns>
        implements PigHouseColumnsService {

    final PigHouseColumnsMapper columnsMapper;
    final PigFarmService farmService;
    final PigHouseMapper houseMapper;
    final PigHouseService pigHouseService;
    final PigHouseRowsMapper rowsMapper;
    final PigHouseRowsService rowsService;
    final PigTypeService pigTypeService;
    final RedissonClient redis;
    final PigBreedingService breedingService;
    final PigBreedingMapper pigBreedingMapper;
    final PigMatingMapper pigMatingMapper;
    final QrcodeMapper qrcodeMapper;
    final QrcodeService qrcodeService;
    final FeedingStrategyRecordService feedingStrategyRecordService;
    final FeedingStrategyService feedingStrategyService;

    @Override
    public ColumnVo detail(Long colId) {
        ColumnVo vo = new ColumnVo();
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        PigHouseColumns col = baseMapper.selectById(colId);
        PigFarm farm = farmService.findByCache(col.getPigFarmId());
        PigHouse house = pigHouseService.findByCache(col.getPigHouseId());
        vo.setId(col.getId());
        vo.setPosition(col.getPosition());
        vo.setClientId(col.getClientId());
        vo.setFeederCode(col.getFeederCode());
        vo.setFeederEnable(col.getFeederEnable());
        vo.setFeedingAmount(col.getFeedingAmount());
        vo.setFeeder(ObjectUtil.isAllNotEmpty(col.getClientId(), col.getFeederCode())
                ?col.getClientId() + " - " + col.getFeederCode()
                :"未绑定");
        vo.setHouseId(house.getId());
        vo.setHouseName(house.getName());
        vo.setHouseType(house.getType());
        if (!farmId.equals(col.getPigFarmId())) {
            throw new BaseException("请切换至 %s 进行操作！", farm.getName());
        }

        Optional<Pig> opt = findByCol(col.getId());
        opt.ifPresent(pig -> {
            switch (vo.getHouseType()) {
                case 1://分娩舍
                case 2://配怀舍
                case 5://公猪舍
                case 6://妊娠舍
                case 531179://巨星配怀舍
                case 513419://巨星公猪舍
                    oneToOne(farm.getDefaultFeedingAmount(), col, pig, vo);
                case 9://后备舍
//                oneToMany();
                    break;
            }
        });
        return vo;
    }

    private void oneToOne(Integer defaultFeedingAmount, PigHouseColumns col, Pig pig, ColumnVo vo) {
        if (ObjectUtil.isNotEmpty(pig)) {
            vo.setPigId(pig.getId());
            vo.setEarNumber(pig.getEarNumber());
            vo.setIndividualNumber(pig.getIndividualNumber());
            vo.setSex(pig.getSex());
            vo.setBackFat(pig.getBackFat());
            vo.setBackFatStage(pig.getStage());
            vo.setBackFatDate(pig.getBackFatDate());
            vo.setBreedDate(pig.getBreedDate());
            vo.setLaborDate(pig.getLaborDate());
            vo.setPiggy(pig.getPiggy());
            Integer weight = 0;
            String desc = null;
            if (ObjectUtil.isNotEmpty(pig.getBackFat())) {
                Optional<Long> pigType = pigTypeService.colUsedPigType(col.getPigFarmId(), col.getPigHouseId());
                if (pigType.isPresent()) {
                    Optional<Long> recordOpt = feedingStrategyRecordService.queryFeedingStrategyRecord(col.getPigFarmId(), pigType.get());
                    if (recordOpt.isPresent()) {
                        weight = feedingStrategyService.findFeedingStrategy(col.getPosition(), pig, recordOpt.get());
                    } else {
                        desc = "请配置饲喂策略";
                        weight = defaultFeedingAmount;
                    }
                } else {
                    weight = defaultFeedingAmount;
                }
                vo.setSysFeedingAmount(weight);
            } else {
                vo.setSysFeedingAmount(defaultFeedingAmount);
            }
            desc = ObjectUtil.isNotNull(desc)?desc:String.format("胎次：%s、阶段：%s天、背膘：%s、计算饲喂量：%s、默认饲喂量：%s",
                    ObjectUtil.isEmpty(pig.getParity()) ? "--" : pig.getParity(),
                    ObjectUtil.isEmpty(pig.getStage()) ? "--" : pig.getStage(),
                    ObjectUtil.isEmpty(pig.getBackFat()) ? "--" : pig.getBackFat(),
                    weight, defaultFeedingAmount);
            vo.setSysFeedingAmountDesc(desc);
        }
    }

    @Override
    public void batchBindFeeder(Long rowId, Long clientId, Integer batch) {
//        List<PigHouseColumns> cols = findByPositionAndHouseOrRowId(rowId, null, null);
//        LambdaQueryWrapper<Qrcode> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Qrcode::getBatch, batch);
//        wrapper.isNull(Qrcode::getPigColumnId);
//        wrapper.orderByAsc(Qrcode::getFeederCode);
//        List<Qrcode> qrcodes = qrcodeMapper.selectList(wrapper);
//        cols = cols.stream().peek(col -> {
//            Optional<Qrcode> opt = qrcodes.stream()
//                    .filter(qr -> qr.getFeederCode().equals(Integer.parseInt(col.getCode())))
//                    .findAny();
//            col.setClientId(clientId);
//            if (!opt.isPresent()) {
//                throw new BaseException(String.format("栏位编号【%s】未找到对应的饲喂器", col.getCode()));
//            }
//            col.setFeederCode(opt.get().getFeederCode());
//        }).collect(toList());
//
//        super.saveOrUpdateBatch(cols);
    }

    private List<PigHouseColumns> find(Long farmId, Long houseId, String position, Long clientId, Integer feederCode) {
        LambdaQueryWrapper<PigHouseColumns> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PigHouseColumns::getDel, 0);
        if (ObjectUtil.isNotEmpty(farmId)) {
            wrapper.eq(PigHouseColumns::getPigFarmId, farmId);
        }
        if (ObjectUtil.isNotEmpty(houseId)) {
            wrapper.eq(PigHouseColumns::getPigHouseId, houseId);
        }
        if (ObjectUtil.isNotEmpty(position)) {
            wrapper.eq(PigHouseColumns::getPosition, position);
        }
        if (ObjectUtil.isNotEmpty(clientId)) {
            wrapper.eq(PigHouseColumns::getClientId, clientId);
        }
        if (ObjectUtil.isNotEmpty(feederCode)) {
            wrapper.eq(PigHouseColumns::getFeederCode, feederCode);
        }
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Optional<PigHouseColumns> findByFeeder(Long clientId, Integer feederCode) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        List<PigHouseColumns> fields = find(farmId,  null, null, clientId, feederCode);
        if (ObjectUtil.isEmpty(fields)) {
            return Optional.empty();
        } else if (fields.size() != 1) {
            log.error("猪场{}拥有多个相同饲喂器[{}]的栏位：{}", farmId, clientId + "-" + feederCode,
                    fields.stream().map(PigHouseColumns::getPosition).collect(toList()));
            return Optional.empty();
        } else {
            return Optional.ofNullable(fields.get(0));
        }
    }

    @Override
    public Optional<PigHouseColumns> findByQrcode(QrcodeVO vo) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        if (ObjectUtil.isNotEmpty(vo.getClientId())) {
            return findByFeeder(vo.getClientId(), vo.getFeederCode());
        } else if (ObjectUtil.isNotEmpty(vo.getCode())) {
            Qrcode code = qrcodeService.findByCode(vo.getCode());
            if (ObjectUtil.isNotEmpty(code)) {
                if (!info.getPigFarmId().equals(code.getPigFarmId())) {
                    throw new BaseException("请扫描猪场%s的二维码", farmService.findByCache(info.getPigFarmId()).getName());
                }
                return Optional.of(baseMapper.selectById(code.getPigColumnId()));
            }
        }
        throw new BaseException("请扫描泽牧科技提供二维码");
    }

    @Override
    public Optional<PigHouseColumns> findByPig(Long pigId) {
        PigHouseColumns col = baseMapper.selectOne(
                Wrappers.lambdaQuery(PigHouseColumns.class).eq(PigHouseColumns::getPigId, pigId));
        if (ObjectUtil.isEmpty(col)) {
            PigBreeding pig = pigBreedingMapper.selectById(pigId);
            if (null != pig && null != pig.getPigHouseColumnsId()) {
                col = baseMapper.selectById(pig.getPigHouseColumnsId());
            }
        }
        return Optional.ofNullable(col);
    }

    @Override
    public Optional<Pig> findByCol(Long colId) {
        PigHouseColumns col = baseMapper.selectById(colId);
        if (ObjectUtil.isNotEmpty(col.getPigId())) {
            return breedingService.findPig(col.getPigId());
        } else {
            PigBreeding pb = pigBreedingMapper.selectOne(
                    Wrappers.lambdaQuery(PigBreeding.class).eq(PigBreeding::getPigHouseColumnsId, colId));
            if (ObjectUtil.isNotEmpty(pb)) {
                PigMating mating = pigMatingMapper.selectByPigBreedingId(pb.getId());
                return Optional.of(Pig.wrap(pb, mating, null));
            }
        }
        return Optional.empty();
    }

    @Override
    public void batchUnbind(Long colId, String endPosition) {
        PigHouseColumns col = columnsMapper.selectById(colId);
        Optional<PigHouseColumns> colOpt = findByPositionAndHouseId(col.getPigHouseId(), endPosition);
        if (!colOpt.isPresent()) {
            throw new BaseException("结束栏位：%s不存在", endPosition);
        }
        List<PigHouseColumns> cols;
        if (col.getPosition().compareTo(endPosition) < 0) {
            cols = findByPositionAndHouseId(col.getPigHouseId(), col.getPosition(), endPosition);
        } else {
            cols = findByPositionAndHouseId(col.getPigHouseId(), endPosition, col.getPosition());
        }
        List<String> fieldCodes = Lists.newArrayList();
        cols.forEach(columns -> {
            try {
                Optional<Pig> opt = findByCol(columns.getId());
                opt.ifPresent(p -> breedingService.moveOutForField(p.getSource(), p.getId()));
            } catch (Exception e) {
                log.error("批量解绑失败：", e);
                fieldCodes.add(columns.getPosition());
            }
        });
        if (ObjectUtil.isNotEmpty(fieldCodes)) {
            throw new BaseException("栏位：%s移出猪只失败！", fieldCodes.toString());
        }
    }


    @Override
    public void batchChangeAmount(Long colId, String endPosition,Integer value) {
        //饲喂量有效值0-5000
        if(value>5000||value<0){
            throw new BaseException("饲喂量有效值0-5000!");
        }
        PigHouseColumns col = columnsMapper.selectById(colId);
        Optional<PigHouseColumns> colOpt = findByPositionAndHouseId(col.getPigHouseId(), endPosition);
        if (!colOpt.isPresent()) {
            throw new BaseException("结束栏位：%s不存在", endPosition);
        }
        List<PigHouseColumns> cols;
        if (col.getPosition().compareTo(endPosition) < 0) {
            cols = findByPositionAndHouseId(col.getPigHouseId(), col.getPosition(), endPosition);
        } else {
            cols = findByPositionAndHouseId(col.getPigHouseId(), endPosition, col.getPosition());
        }
        List<String> fieldCodes = Lists.newArrayList();
        cols.forEach(columns -> {
            try {
                columnsMapper.update(columns,new UpdateWrapper<PigHouseColumns>().lambda()
                        .set(PigHouseColumns::getFeedingAmount,value)
                        .eq(PigHouseColumns::getId,columns.getId()));
            } catch (Exception e) {
                log.error("批量修改修正饲喂量失败：", e);
                fieldCodes.add(columns.getPosition());
            }
        });
        if (ObjectUtil.isNotEmpty(fieldCodes)) {
            throw new BaseException("栏位：%s改修正饲喂量失败！", fieldCodes.toString());
        }
    }

    @Override
    public List<PigHouseColumns> findByPositionAndHouseId(Long houseId, String beginPosition, String endPosition) {
        LambdaQueryWrapper<PigHouseColumns> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PigHouseColumns::getPigHouseId, houseId);
        if (ObjectUtil.isAllNotEmpty(beginPosition, endPosition)) {
            wrapper.between(PigHouseColumns::getPosition, beginPosition, endPosition);
        }
        wrapper.orderByAsc(PigHouseColumns::getPosition);
        return columnsMapper.selectList(wrapper);
    }

    @Override
    public Optional<PigHouseColumns> findByPositionAndHouseId(Long houseId, String position) {
        PigHouseColumns col = columnsMapper.selectOne(Wrappers.lambdaQuery(PigHouseColumns.class)
                .eq(PigHouseColumns::getDel, 0)
                .eq(PigHouseColumns::getPigHouseId, houseId)
                .eq(PigHouseColumns::getPosition, position));
        return Optional.ofNullable(col);
    }

    @Override
    public void save(SaveColumnDto colDto) {
        PigHouseColumns col = columnsMapper.selectById(colDto.getColId());
        col.setFeedingAmount(colDto.getFeedingAmount());
        col.setFeederEnable(colDto.getFeederEnable());
        columnsMapper.updateById(col);
    }

    @Override
    public void init(InitColDto initColDto) {
        PigHouseColumns col = columnsMapper.selectById(initColDto.getColId());
        if (ObjectUtil.isNotEmpty(col.getClientId()) || ObjectUtil.isNotEmpty(col.getFeederCode())) {
            throw new BaseException("该栏位已绑定二维码, 请重新选择");
        }
        col.setClientId(initColDto.getClientId());
        col.setFeederCode(initColDto.getFeederCode());
        col.setFeederEnable(1);
        columnsMapper.updateById(col);

        if (ObjectUtil.isNotEmpty(initColDto.getQrcode())) {
            RequestInfo info = RequestContextUtils.getRequestInfo();
            PigHouseRows rows = rowsMapper.selectById(col.getPigHouseRowsId());
            List<Qrcode> qrcodes = qrcodeMapper.selectByCode(initColDto.getQrcode());
            if (ObjectUtil.isEmpty(qrcodes)) {
                throw new BaseException("二维码错误");
            }
            Qrcode qrcode = qrcodes.get(0);
            qrcode.setCompanyId(info.getCompanyId());
            qrcode.setPigFarmId(info.getPigFarmId());
            qrcode.setPigHouseId(rows.getPigHouseId());
            qrcode.setPigColumnId(col.getId());
            qrcodeMapper.updateById(qrcode);
        }

    }

    @Override
    public List<PigHouseColumns> listByHouse(Long houseId) {
        return list(houseId, null);
    }

    @Override
    public List<PigHouseColumns> listByRow(Long rowId) {
        return list(null, rowId);
    }

    private List<PigHouseColumns> list(Long houseId, Long rowId) {
        LambdaQueryWrapper<PigHouseColumns> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(houseId)) {
            List<Long> rowIds = rowsService.list(houseId, false).stream().map(PigHouseRows::getId).collect(Collectors.toList());
            wrapper.in(PigHouseColumns::getPigHouseRowsId, rowIds);
        }
        if (ObjectUtil.isNotEmpty(rowId)) {
            wrapper.eq(PigHouseColumns::getPigHouseRowsId, rowId);
        }
        wrapper.orderByAsc(PigHouseColumns::getPosition);
        return columnsMapper.selectList(wrapper);
    }

    @Override
    public List<PigHouseColumns> waitBatchBind() {
        Long userId = RequestContextUtils.getUserId();
        String key = ColumnOperateType.exactTransferPig.key() + userId;
        RMap<Long, Map<String, Set<Long>>> choseMap = redis.getMap(key);
        List<PigHouseColumns> vos = new ArrayList<>();
        if (choseMap.isEmpty()) {
            return vos;
        }
        for (Map.Entry<Long, Map<String, Set<Long>>> entry : choseMap.entrySet()) {
            if (ObjectUtil.isEmpty(entry.getValue())) {
                continue;
            }
            for (Map.Entry<String, Set<Long>> etr : entry.getValue().entrySet()) {
                if (ObjectUtil.isEmpty(etr.getValue())) {
                    continue;
                }
                vos.addAll(columnsMapper.selectBatchIds(etr.getValue()));
            }
        }
        vos.sort(Comparator.comparing(PigHouseColumns::getPosition));
        return vos;
    }

    @Override
    public List<ViewRowVo> viewHouseRows(Long houseId, ColumnOperateType operationType) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        String key = operationType.key() + info.getUserId();
        RMap<Long, Map<String, Set<Long>>> choseMap = redis.getMap(key);
        List<PigHouseRows> houseRows = ListUtil.empty();
        if (info.getResourceType().equals(ResourceType.JX)) {
            houseRows = houseRowsCache(houseId).stream().map(str -> {
                PigHouseRows r = new PigHouseRows();
                r.setPosition(str);
                return r;
            }).collect(toList());
        } else if (info.getResourceType().equals(ResourceType.YHY)) {
            houseRows = rowsService.list(houseId, false);
        }

        ColumnOperateType finalOperationType = operationType;
        return houseRows.stream().map(r -> {
            ViewRowVo.ViewRowVoBuilder builder = ViewRowVo.builder();
            PigHouseRows rows = rowsMapper.selectOne(Wrappers.lambdaQuery(PigHouseRows.class)
                    .eq(PigHouseRows::getPigHouseId, houseId).eq(PigHouseRows::getPosition, r.getPosition()));
            Long rowId = ObjectUtil.isNotNull(rows)?rows.getId():null;

            builder.houseId(houseId).rowId(rowId).row(r.getPosition()).operationType(finalOperationType);
            if (!choseMap.isEmpty()) {
                Map<String, Set<Long>> choseRowMap = choseMap.get(houseId);
                if (ObjectUtil.isNotEmpty(choseRowMap)) {
                    builder.columnIds(choseRowMap.get(r.getPosition()));
                }
            }
            return builder.build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ViewColumnVo> viewColumns(String row) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        PageHelper.clearPage();
        return columnsMapper.ViewColumnVo(farmId, row + "%");
    }

    @Override
    public List<ViewColumnVo> allCols(Long houseId) {
        PageHelper.clearPage();
        PigHouse house = houseMapper.selectById(houseId);
        List<Integer> types = Arrays.asList(1,2,5);
        if (types.contains(house.getType())) {
            return columnsMapper.viewAllCols(houseId);
        } else {
            List<PigHouseColumns> cols = find(house.getPigFarmId(), houseId, null, null, null);
            return cols.stream().map(c ->
                    ViewColumnVo.builder().id(c.getId()).no(c.getNo()).pigs(
                            breedingService.findByCol(c.getId()).stream().map(p ->
                                    SimplePigVo.builder().pigId(p.getId()).earNumber(p.getEarNumber()).build()
                            ).collect(toList())
                    ).build()
            ).collect(toList());
        }
    }

    @Override
    public void choseColumns(ViewRowVo viewRowVo) {
        Long userId = RequestContextUtils.getUserId();
        String key = viewRowVo.getOperationType().key() + userId;
        RMap<Long, Map<String, Set<Long>>> choseMap = redis.getMap(key);
        Map<String, Set<Long>> choseRowMap;
        if (choseMap.isEmpty()) {
            choseRowMap = new HashMap<>();
        } else {
            choseRowMap = choseMap.get(viewRowVo.getHouseId());
            if (ObjectUtil.isEmpty(choseRowMap)) {
                choseRowMap = new HashMap<>();
            }
        }
        if (ObjectUtil.isEmpty(viewRowVo.getColumnIds())) {
            choseRowMap.remove(viewRowVo.getRow());
        } else {
            choseRowMap.put(viewRowVo.getRow(), viewRowVo.getColumnIds());
        }
        choseMap.put(viewRowVo.getHouseId(), choseRowMap);
        Date now = DateUtil.date();
        long expire = DateUtil.endOfDay(now).between(now, DateUnit.SECOND);
        choseMap.expire(Duration.ofSeconds(expire));
    }

    @Override
    public void choseFieldsForTransferPig(ViewRowVo viewRowVo) {
        Long userId = RequestContextUtils.getUserId();
        String key = viewRowVo.getOperationType().key() + userId + ":" + viewRowVo.getHouseId();
        RSet<Long> chose = redis.getSet(key);
        chose.clear();
        chose.addAll(viewRowVo.getColumnIds());
        Date now = DateUtil.date();
        long expire = DateUtil.endOfDay(now).between(now, DateUnit.SECOND);
        chose.expire(Duration.ofSeconds(expire));
    }

    @Override
    public List<ViewRowVo> choose(ColumnOperateType operationType, Long houseId) {
        Long userId = RequestContextUtils.getUserId();
        String key = operationType.key() + userId;
        RMap<Long, Map<String, Set<Long>>> choseMap = redis.getMap(key);
        List<ViewRowVo> vos = new ArrayList<>();
        if (choseMap.isEmpty()) {
            return vos;
        }
        for (Map.Entry<Long, Map<String, Set<Long>>> entry : choseMap.entrySet()) {
            if (ObjectUtil.isEmpty(entry.getValue())) {
                continue;
            }
            if (ObjectUtil.isEmpty(houseId) || ObjectUtil.equals(houseId, entry.getKey())) {
                for (Map.Entry<String, Set<Long>> etr : entry.getValue().entrySet()) {
                    PigHouseRows rows = rowsMapper.selectOne(Wrappers.lambdaQuery(PigHouseRows.class).eq(PigHouseRows::getPosition, etr.getKey()));
                    ViewRowVo viewRowVo = ViewRowVo.builder()
                            .houseId(entry.getKey())
                            .row(etr.getKey())
                            .rowId(ObjectUtil.isNotEmpty(rows)? rows.getId():null)
                            .columnIds(etr.getValue())
                            .operationType(operationType).build();
                    if (ObjectUtil.isNotEmpty(houseId)) {
                        viewRowVo.setCols(etr.getValue().stream().map(id -> {
                            PigHouseColumns col = columnsMapper.selectById(id);
                            ColumnVo vo = new ColumnVo();
                            vo.setId(id);
                            vo.setNo(col.getNo());
                            findByCol(id).ifPresent(pig -> {
                                vo.setPigId(pig.getId());
                                vo.setEarNumber(pig.getEarNumber());
                            });
                            return vo;
                        }).collect(Collectors.toSet()).stream().sorted(Comparator.comparing(ColumnVo::getNo)).collect(Collectors.toList()));
                    }
                    vos.add(viewRowVo);
                }
            }
        }
        return vos;
    }

    @Override
    public ChooseCols chooseForTransferPig(String houseId, ColumnOperateType operationType) {
        Long userId = RequestContextUtils.getUserId();
        String key = operationType.key() + userId + ":" + houseId;
        RSet<Long> colIds = redis.getSet(key);
        List<ColumnVo> vos = colIds.stream().map(id -> {
            PigHouseColumns col = columnsMapper.selectById(id);
            ColumnVo vo = new ColumnVo();
            vo.setId(id);
            vo.setNo(col.getNo());
            findByCol(id).ifPresent(pig -> {
                vo.setPigId(pig.getId());
                vo.setEarNumber(pig.getEarNumber());
            });
            return vo;
        }).collect(Collectors.toSet()).stream().sorted(Comparator.comparing(ColumnVo::getNo)).collect(Collectors.toList());
        PigHouse house = houseMapper.selectById(houseId);
        return ChooseCols.builder().houseId(house.getId()).houseName(house.getName()).cols(vos).build();
    }

    @Override
    public void choseRemove(ViewRowVo viewRowVo) {
        Long userId = RequestContextUtils.getUserId();
        String key = viewRowVo.getOperationType().key() + userId;
        RMap<Long, Map<String, Set<Long>>> cache = redis.getMap(key);
        Map<Long, Map<String, Set<Long>>> choseMap = cache.readAllMap();
        if (!choseMap.isEmpty()) {
            Map<String, Set<Long>> choseRowMap = choseMap.get(viewRowVo.getHouseId());
            if (ObjectUtil.isNotEmpty(choseRowMap)) {
                choseRowMap.remove(viewRowVo.getRow());
            }
            if (ObjectUtil.isEmpty(choseRowMap)) {
                choseMap.remove(viewRowVo.getHouseId());
            }
        }
        cache.delete();
        if (!choseMap.isEmpty()) {
            cache.putAll(choseMap);
            Date now = DateUtil.date();
            long expire = DateUtil.endOfDay(now).between(now, DateUnit.SECOND);
            cache.expire(Duration.ofSeconds(expire));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(PigHouseColumnsDTO pigHouseColumnsDTO) {
        Long userId = RequestContextUtils.getUserId();
        PigHouseRows pigHouseRows = rowsMapper.selectById(pigHouseColumnsDTO.getPigHouseRowsId());
        if (ObjectUtil.isEmpty(pigHouseRows)) {
            throw new BaseException("猪舍排位不存在");
        }
        PigHouse pigHouse = houseMapper.selectById(pigHouseRows.getPigHouseId());
        if (ObjectUtil.isEmpty(pigHouse)) {
            throw new BaseException("猪舍不存在");
        }
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<PigHouseColumns>().eq(PigHouseColumns::getPigHouseRowsId, pigHouseColumnsDTO.getPigHouseRowsId()));
        PigHouseColumns pigHouseColumns = BeanUtil.copyProperties(pigHouseColumnsDTO, PigHouseColumns.class);
        String code = StringUtils.leftPad(String.valueOf(count + 1), 2, "0");
        //  如果code、position、name 为空则自动生成
        if (StringUtils.isBlank(pigHouseColumns.getName())) {
            pigHouseColumns.setName("栏位-" + code);
        }
        if (StringUtils.isBlank(pigHouseColumns.getCode())) {
            pigHouseColumns.setCode(code);
        }
        if (StringUtils.isBlank(pigHouseColumns.getPosition())) {
            pigHouseColumns.setPosition(pigHouse.getCode() + "-" + pigHouseRows.getCode() + "-" + code);
        }
        pigHouseColumns.setCreateBy(userId);
        save(pigHouseColumns);
        return pigHouseColumns.getId();
    }
    @Override
    public void update(PigHouseColumnsDTO pigHouseColumnsDTO) {
        PigHouseColumns pigHouseColumns = BeanUtil.copyProperties(pigHouseColumnsDTO, PigHouseColumns.class);
        // 不允许修改排位id
        pigHouseColumns.setPigHouseRowsId(null);
        pigHouseColumns.setUpdateBy(RequestContextUtils.getUserId());
        updateById(pigHouseColumns);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long pigHouseColumnsId) {
        Long userId = RequestContextUtils.getUserId();
//        PigStockDTO count = pigBreedingMapper.count(pigHouseColumnsId);
//        if (count.getTotal() > 0) {
//            throw new BaseException("该栏位下存在猪只，不能删除");
//        }
        PigHouseColumns pigHouseColumns = baseMapper.selectById(pigHouseColumnsId);
        if (ObjectUtil.isEmpty(pigHouseColumns)) {
            throw new BaseException("猪舍栏位不存在");
        }
        PigHouseRows pigHouseRows = rowsMapper.selectById(pigHouseColumns.getPigHouseRowsId());
        if (ObjectUtil.isEmpty(pigHouseRows)) {
            throw new BaseException("猪舍排位不存在");
        }
        PigHouse pigHouse = houseMapper.selectById(pigHouseRows.getPigHouseId());
        if (ObjectUtil.isEmpty(pigHouse)) {
            throw new BaseException("猪舍不存在");
        }
        LambdaUpdateWrapper<PigHouseColumns> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(PigHouseColumns::getUpdateBy, userId)
                .set(PigHouseColumns::getDel, true)
                .eq(PigHouseColumns::getId, pigHouseColumnsId);
        update(wrapper);
     /*   //更新猪舍表的栏位数量
        pigHouse.setColumns(pigHouse.getColumns() - 1);
        pigHouse.setUpdateBy(userId);
        houseMapper.updateById(pigHouse);*/
    }
    @Override
    public List<PigHouseColumns> saveBatch(List<PigHouseColumns> pigHouseColumns) {
        if (CollectionUtils.isNotEmpty(pigHouseColumns)) {
            super.saveBatch(pigHouseColumns);
            return pigHouseColumns;
        }
        return Collections.emptyList();
    }

    public List<String> houseRowsCache(Long houseId) {
        RList<String> rows = redis.getList(CacheKey.Web.column_rows.key + houseId);
        if (rows.isEmpty()) {
            rows.addAll(baseMapper.viewHouseRows(houseId));
        }
        return rows;
    }

    @Override
    public List<PigHouseColumns> findByClientId(Long clientId) {
        return columnsMapper.selectList(
                Wrappers.lambdaQuery(PigHouseColumns.class)
                        .eq(PigHouseColumns::getDel, 0)
                        .eq(PigHouseColumns::getClientId, clientId));
    }
//
//    @Override
//    public Pig wrap(PigHouseColumns col) {
//        Pig pig = null;
//        if (ObjectUtil.isEmpty(col.getCompanyId())) {
//            Optional<SphPig> opt = sphPigService.findByField(col.getId());
//            if (opt.isPresent()) {
//                pig = Pig.wrap(opt.get());
//            }
//        } else {
//            Optional<PigBreeding> opt = breedingService.findByField(col.getId());
//            if (opt.isPresent()) {
//                PigBreeding breeding = opt.get();
//                PigMating mating = matingMapper.selectByPigBreedingId(breeding.getId());
//                pig = Pig.wrap(breeding, mating, null);
//            }
//        }
//        return pig;
//    }
}
