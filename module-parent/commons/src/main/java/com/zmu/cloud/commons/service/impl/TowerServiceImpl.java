package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.annotations.RedissonDistributedLock;
import com.zmu.cloud.commons.config.PythonProperties;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.dto.*;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.event.MeasureEvent;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.exception.admin.UnauthorizedException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.*;
import com.zmu.cloud.commons.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.codec.SerializationCodec;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zmu.cloud.commons.constants.Constants.getZCList;
import static com.zmu.cloud.commons.utils.ZmDateUtil.forMateDateToMonthDay;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static jodd.util.ThreadUtil.sleep;

/**
 * 料塔Mqtt主题
 * 1、发送即时测量：/tower/{deviceId}/RX
 * 2、接收采集数据：/tower/{deviceId}/TX
 * 3、状态上报:    /tower/{deviceId}/STATUS
 * 4、通知APP刷新  /tower/{deviceId}/REFRESH
 *
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TowerServiceImpl implements TowerService {

    final FeedTowerMapper towerMapper;
    final FeedTowerDeviceMapper towerDeviceMapper;
    final TowerDeviceService towerDeviceService;
    final FeedTowerLogMapper towerLogMapper;
    final TowerLogService towerLogService;
    final PigFarmMapper farmMapper;
    final PigHouseMapper houseMapper;
    final RedissonClient redissonClient;
    final FeedTowerFeedTypeMapper feedTypeMapper;
    final TaskService taskService;
    final TowerQrtzService towerQrtzService;
    final FeedTowerEmployMapper towerEmployMapper;
    final FeedTowerApplyMapper feedTowerApplyMapper;
    final ThreadPoolTaskExecutor taskExecutor;
    final PythonProperties pythonProperties;
    final ApplicationEventPublisher eventPublisher;
    final FeedTowerAddMapper feedTowerAddMapper;
    final FeedTowerLogSlaveMapper towerLogSlaveMapper;
    final DeviceQualityCheckMapper deviceQualityCheckMapper;
    final FeedTowerGrowthAbilityMapper feedTowerGrowthAbilityMapper;
    final DeviceAgingCheckMapper deviceAgingCheckMapper;
    final DeviceInitCheckMapper deviceInitCheckMapper;
    final FeedTowerMapper feedTowerMapper;
    final SysUserFarmMapper sysUserFarmMapper;
    final ZmuCloudProperties config;
    final TowerProperty towerProperty ;

    private final Long  TOWER_VOLUME_STANDARD = 1000000L; //单位立方厘米
    private final Integer  PASS_PERCENT = 9500; //通过的准确度95%


    /**
     * 料塔模块初始化
     */
    @PostConstruct
    public void initSubscribe() {
        RSet<String> timers = redissonClient.getSet(CacheKey.Admin.tower_default_timer.key);
        if (!timers.isExists()) {
            timers.addAll(ListUtil.of("8:00", "12:00", "16:00", "18:00", "21:00"));
        }
    }

    @Override
    public List<TowerVo> listVo() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        Long userId = info.getResourceType().equals(ResourceType.JX)?info.getUserId():null;
        List<FeedTower> list = list(null, info.getPigFarmId(), null, userId);
        return wrapperTowerOnlineMessage(list).stream()
                .peek(vo -> vo.setUsed(last5DayUseFeedByTower(vo.getId()))).peek(oneTower -> {
                    if(StringUtils.isNotBlank(oneTower.getDeviceNo()) && oneTower.getInit() == 1 &&  ObjectUtil.isNotEmpty(oneTower.getWarning()) &&  ObjectUtil.isNotEmpty(oneTower.getResidualPercentage())  &&oneTower.getWarning()> oneTower.getResidualPercentage()){
                        oneTower.setLowFeedWarning(true);
                    }else{
                        oneTower.setLowFeedWarning(false);
                    }
                }).collect(toList());
    }

    @Override
    public TowerPageData listVoPage(Integer pageNum,Integer pageSize) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        Long userId = info.getResourceType().equals(ResourceType.JX)?info.getUserId():null;
        Map<Long, List<FeedTower>> map = listPage(null, info.getPigFarmId(), null, userId, pageNum, pageSize);
        List<FeedTower> list = new ArrayList<>();
        Long count = 0L;
        for (Map.Entry<Long, List<FeedTower>> entry : map.entrySet()) {
            count = entry.getKey();
            list.addAll(entry.getValue());
        }
        List<TowerVo> collect = wrapperTowerOnlineMessage(list).stream()
                .peek(vo -> vo.setUsed(last5DayUseFeedByTower(vo.getId()))).peek(oneTower -> {
                    if (StringUtils.isNotBlank(oneTower.getDeviceNo()) && oneTower.getInit() == 1 && ObjectUtil.isNotEmpty(oneTower.getWarning()) && ObjectUtil.isNotEmpty(oneTower.getResidualPercentage()) && oneTower.getWarning() > oneTower.getResidualPercentage()) {
                        oneTower.setLowFeedWarning(true);
                    } else {
                        oneTower.setLowFeedWarning(false);
                    }
                }).collect(toList());
        return TowerPageData.builder().pageSize(pageSize.longValue()).pageNum(pageNum.longValue()).count(count).list(collect).build();
    }

    @Override
    public List<TowerWarningVo> warning() {
        List<TowerVo> towerVoList = listVo();
        ArrayList<TowerWarningVo> towerWarningVos = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(towerVoList)){
            towerVoList.forEach(one->{
                if(one.getLowFeedWarning()){
                    TowerWarningVo towerWarningVo = new TowerWarningVo();
                    BeanUtil.copyProperties(one,towerWarningVo);
                    towerWarningVos.add(towerWarningVo);
                }
            });
        }
        return towerWarningVos;
    }

    @Override
    public List<FarmTowerPointVo> farmTowerPoint(String farmName, String deviceNo) {
        List<PigFarm> farms = farmMapper.selectFarmByName(farmName);
        return farms.stream().map(farm -> {
            //获取猪场下面所有料塔
            List<FeedTower> towers = feedTowerMapper.findTowerByFarmIdAndDeviceNo(farm.getId(),deviceNo);
            List<TowerVo> towerVoList = wrapperTowerOnlineMessage(towers);
            return FarmTowerPointVo.builder().farmName(farm.getName()).towerNum(towerVoList.size()).towers(towerVoList).build();
        }).collect(toList());
    }

    //设备情况
    @Override
    public TowerStatusInfoVo towersInfo() {
        List<TowerVo> towerVos = listVo();
        if(ObjectUtil.isNotEmpty(towerVos)){
            towerVos = towerVos.stream().filter(one->ObjectUtil.isNotEmpty(one.getDeviceNo())).collect(toList());
            return TowerStatusInfoVo.builder()
                    .normal(towerVos.stream().filter(one -> one.getDeviceStatus()!= null && !"故障".equals(one.getDeviceStatus()) &&!"离线".equals(one.getNetwork())).count())
                    .total(Long.parseLong(String.valueOf(towerVos.size())))
                    .error(towerVos.stream().filter(one -> one.getDeviceStatus()== null ||"故障".equals(one.getDeviceStatus()) || "离线".equals(one.getNetwork())).count())
                    .errorInfoList(towerVos.stream().filter(one -> one.getDeviceStatus()== null ||"故障".equals(one.getDeviceStatus()) || "离线".equals(one.getNetwork())).collect(toList()))
                    .build();
        }else{
            return TowerStatusInfoVo.builder()
                    .normal(0L)
                    .total(0L)
                    .error(0L)
                    .errorInfoList(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public List<FeedTower> all() {
        return list(null, null, null, null);
    }

    @Override
    public List<FeedTower> list(String deviceNo, Long farmId, String name, Long myTower) {
        LambdaQueryWrapper<FeedTower> wrapper = new LambdaQueryWrapper<>();
        //只查询非质检料塔
        wrapper.ne(FeedTower::getTemp, 1);
        if (ObjectUtil.isNotEmpty(deviceNo)) {
            wrapper.eq(FeedTower::getDeviceNo, deviceNo);
        }
        if (ObjectUtil.isNotEmpty(farmId)) {
            wrapper.eq(FeedTower::getPigFarmId, farmId);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(FeedTower::getName, name);
        }
        if (ObjectUtil.isNotNull(myTower)) {
            LambdaQueryWrapper<FeedTowerEmploy> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FeedTowerEmploy::getEmployId, myTower);
            List<Long> ids = towerEmployMapper.selectList(queryWrapper).stream().map(FeedTowerEmploy::getTowerId).collect(toList());
            if (CollUtil.isNotEmpty(ids)) {
                wrapper.in(FeedTower::getId, ids);
            }
        }
        wrapper.orderByAsc(FeedTower::getName);
        return towerMapper.selectList(wrapper);
    }
    @Override
    public Map<Long,List<FeedTower>> listPage(String deviceNo, Long farmId, String name, Long myTower,Integer pageNum,Integer pageSize) {
        LambdaQueryWrapper<FeedTower> wrapper = new LambdaQueryWrapper<>();
        //只查询非质检料塔
        wrapper.ne(FeedTower::getTemp, 1);
        if (ObjectUtil.isNotEmpty(deviceNo)) {
            wrapper.ne(FeedTower::getDeviceNo, deviceNo);
        }
        if (ObjectUtil.isNotEmpty(farmId)) {
            wrapper.eq(FeedTower::getPigFarmId, farmId);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(FeedTower::getName, name);
        }
        if (ObjectUtil.isNotNull(myTower)) {
            LambdaQueryWrapper<FeedTowerEmploy> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FeedTowerEmploy::getEmployId, myTower);
            List<Long> ids = towerEmployMapper.selectList(queryWrapper).stream().map(FeedTowerEmploy::getTowerId).collect(toList());
            if (CollUtil.isNotEmpty(ids)) {
                wrapper.in(FeedTower::getId, ids);
            }
        }
        wrapper.orderByAsc(FeedTower::getName);
        Long count = towerMapper.selectCount(wrapper);
        wrapper.last(" limit "+(pageNum-1)*pageSize+" , "+pageSize);
        List<FeedTower> feedTowers = towerMapper.selectList(wrapper);
        Map<Long,List<FeedTower>> map = new HashMap<>();
        map.put(count,feedTowers);
        return map;
    }

    @Override
    public Optional<FeedTower> find(String deviceNo) {
        if (ObjectUtil.isEmpty(deviceNo)) {
            return Optional.empty();
        }
        RBucket<FeedTower> bucket = redissonClient.getBucket(CacheKey.Admin.tower_config.key.concat(deviceNo));
        FeedTower tower;
        if (bucket.isExists()) {
            return Optional.of(bucket.get());
        } else {
            tower = towerMapper.selectOne(Wrappers.lambdaQuery(FeedTower.class).eq(FeedTower::getDeviceNo, deviceNo));
            if (ObjectUtil.isNull(tower)) {
                return Optional.empty();
            }
            bucket.set(tower);
            return Optional.of(tower);
        }
    }

    @Override
    public FeedTower save(TowerDto dto) {
        FeedTower tower;
        RequestInfo info = RequestContextUtils.getRequestInfo();
        Integer houseType = null;
        if (ObjectUtil.isNotEmpty(dto.getHouseIds()) && ObjectUtil.notEqual(AssociationType.ZZLT.name(), dto.getHouseIds())) {
            houseType = houseMapper.selectById(dto.getHouseIds().split(",")[0]).getType();
        }

        if (ObjectUtil.isNull(dto.getId())) {
            boolean exists = towerMapper.exists(Wrappers.lambdaQuery(FeedTower.class).eq(FeedTower::getName, dto.getName()));
            if (exists) {
                throw new BaseException("料塔名称不能重复");
            }
            tower = FeedTower.builder()
                    .name(dto.getName())
                    .capacity(ObjectUtil.isEmpty(dto.getCapacity())?null:ZmMathUtil.tTog(Double.parseDouble(dto.getCapacity())))
                    .warning(dto.getWarning())
//                    .initVolume(ZmMathUtil.m3ToCm3(100D)) //新建料塔 初始化容积 100 M³
                    .feedTypeId(dto.getFeedTypeId())
                    .feedType(dto.getFeedType())
                    .density(ZmMathUtil.kgTog(dto.getDensity()))
                    .companyId(info.getCompanyId())
                    .pigFarmId(info.getPigFarmId())
                    .houses(dto.getHouseIds())
                    .houseType(houseType)
                    .build();
            towerMapper.insert(tower);
        } else {
            boolean exists = towerMapper.exists(Wrappers.lambdaQuery(FeedTower.class).ne(FeedTower::getId, dto.getId())
                    .eq(FeedTower::getName, dto.getName()));
            if (exists) {
                throw new BaseException("料塔名称不能重复");
            }
            tower = towerMapper.selectById(dto.getId());
            Long oldFeedTypeId = tower.getFeedTypeId();
            LambdaUpdateWrapper<FeedTower> wrapper = new UpdateWrapper<FeedTower>().lambda();

            if (ObjectUtil.notEqual(1, tower.getInit())) {
                if (ObjectUtil.isEmpty(dto.getCapacity()) || Double.parseDouble(dto.getCapacity()) <= 0) {
//                    wrapper.set(FeedTower::getInitVolume, ZmMathUtil.m3ToCm3(100D).longValue());
                    wrapper.set(FeedTower::getInitVolume, null);
                } else {
                    wrapper.set(FeedTower::getInitVolume, ZmMathUtil.m3ToCm3(Double.valueOf(dto.getCapacity()) * 2).longValue());
                }
            }
            wrapper.set(FeedTower::getName, dto.getName())
                    .set(FeedTower::getCapacity, ObjectUtil.isEmpty(dto.getCapacity())?null:ZmMathUtil.tTog(Double.parseDouble(dto.getCapacity())))
                    .set(FeedTower::getWarning, dto.getWarning())
                    .set(FeedTower::getFeedTypeId, dto.getFeedTypeId())
                    .set(FeedTower::getFeedType, dto.getFeedType())
                    .set(FeedTower::getDensity, ZmMathUtil.kgTog(dto.getDensity()));
            if (ObjectUtil.equals(dto.getHouseIds(), "ZZLT")) {
                wrapper.set(FeedTower::getHouseType, null);
                wrapper.set(FeedTower::getHouses, AssociationType.ZZLT.name());
            } else {
                wrapper.set(FeedTower::getHouseType, houseType);
                wrapper.set(FeedTower::getHouses, dto.getHouseIds());
            }
            //如果饲料密度值发生变化，则重算相关料塔的余料量
            /*if (ObjectUtil.notEqual(oldFeedTypeId, dto.getFeedTypeId())) {
                Double g = ZmMathUtil.cmTm(tower.getResidualVolume())*ZmMathUtil.kgTog(dto.getDensity());
                wrapper.set(FeedTower::getResidualWeight, g.longValue());

                //更新日志表的加料记录
                updateLastOneGoInByTowerId(dto.getId(),ZmMathUtil.kgTog(dto.getDensity()).doubleValue());

            }*/
            towerMapper.update(tower, wrapper.eq(FeedTower::getId, tower.getId()));
        }
        delTowerConfigCache(tower.getDeviceNo());
        return tower;
    }

    /**
     * 密度变化时更新今日最近一次加料的重量及变化量
     * @param towerId 料塔id
     * @param density 密度
     */
    public void updateLastOneGoInByTowerId(Long towerId,Double density) {
        FeedTowerLog lastLog = towerLogMapper.selectLastOneGoInByTowerId(towerId);
        if (ObjectUtil.isNotNull(lastLog)){
            Long oldWeight = lastLog.getWeight();
            Double weight = ZmMathUtil.cmTm(lastLog.getVolume()) * density;
            Long newWeight = weight.longValue();
            //变化量
            Long bhl = newWeight - oldWeight;
            lastLog.setTowerDensity(density.longValue());
            lastLog.setVariation(lastLog.getVariation()+bhl);
            lastLog.setWeight(newWeight);
            towerLogMapper.updateLogWeightById(lastLog);
        }
    }

    @Override
    public void associationHouse(String towerId, String houseIds) {
        FeedTower tower = towerMapper.selectById(towerId);
        LambdaUpdateWrapper<FeedTower> wrapper = new UpdateWrapper<FeedTower>().lambda();
        if (ObjectUtil.equals(houseIds, "ZZLT")) {
            wrapper.set(FeedTower::getHouseType, null);
            wrapper.set(FeedTower::getHouses, AssociationType.ZZLT.name());
        } else {
            wrapper.set(FeedTower::getHouseType, houseMapper.selectById(houseIds.split(",")[0]).getType());
            wrapper.set(FeedTower::getHouses, houseIds);
        }
        towerMapper.update(tower, wrapper.eq(FeedTower::getId, towerId));
        delTowerConfigCache(tower.getDeviceNo());
    }

    @Override
    public List<String> defaultCapacity() {
        RSet<Double> capacity = redissonClient.getSet(CacheKey.Admin.tower_default_capacity.key);
        return capacity.stream().sorted().map(d -> new DecimalFormat("0.##").format(d)).collect(toList());
    }

    @Override
    public List<String> defaultTimer() {
        RSet<String> timer = redissonClient.getSet(CacheKey.Admin.tower_default_timer.key);
        return timer.stream()
                .map(t -> DateUtil.parseTime(t+":00"))
                .sorted(Comparator.comparing(Date::getTime))
                .map(t -> DateUtil.format(t, "HH:mm")).collect(toList());
    }

    @Override
    public List<FeedTypeVo> feedTypes() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        List<FeedTowerFeedType> feedTowerFeedTypes = feedTypeMapper.selectListByCompanyId(info.getCompanyId(),ResourceType.JX.equals(info.getResourceType()) ? "jx":null);

        return feedTowerFeedTypes.stream()
                .map(type -> FeedTypeVo.builder().id(type.getId()).name(type.getName()).density(type.getDensity())
                        .densityString(ZmMathUtil.gTokgDensityString(type.getDensity())).inlay(type.isInlay())
                        .updateBy(type.getUpdateBy())
                        .updateTime(type.getUpdateTime())
                        .updateByName(type.getUpdateByName())
                        .createBy(type.getCreateBy())
                        .createTime(type.getCreateTime()).build())
                .collect(Collectors.toList());
    }

    @Override
    public void feedTypeSave(Long id, String name, Double density) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        FeedTowerFeedType exists = feedTypeMapper.selectOne(Wrappers.lambdaQuery(FeedTowerFeedType.class)
                .eq(FeedTowerFeedType::getCompanyId, info.getCompanyId())
                .eq(FeedTowerFeedType::getName, name));
        FeedTowerFeedType type;

        if (ObjectUtil.isEmpty(id)) {
            if (ObjectUtil.isNotEmpty(exists)) {
                throw new BaseException("饲料品类【%s】已存在，无需再添加", name);
            }
            type = new FeedTowerFeedType();
            type.setName(name);
            type.setCompanyId(info.getCompanyId());
            type.setDensity(ZmMathUtil.kgTog(density));

            type.setCreateBy(info.getUserId());
            type.setCreateTime(LocalDateTime.now());
            type.setUpdateBy(info.getUserId());
            type.setUpdateTime(LocalDateTime.now());

            feedTypeMapper.insert(type);
        } else {
            if (ObjectUtil.isNotEmpty(exists) && ObjectUtil.notEqual(exists.getId(), id)) {
                throw new BaseException("饲料品类【%s】已存在，无需再添加", name);
            }
            type = feedTypeMapper.selectById(id);
            long old = type.getDensity();
            type.setName(name);
            type.setCompanyId(info.getCompanyId());
            type.setDensity(ZmMathUtil.kgTog(density));

            type.setUpdateBy(info.getUserId());
            type.setUpdateTime(LocalDateTime.now());

            feedTypeMapper.updateById(type);

            //如果饲料密度值发生变化，则重算相关料塔的余料量
            long newDensity = ZmMathUtil.kgTog(density);
            /*if (ObjectUtil.notEqual(newDensity, old)) {
                recalculate(type);
            }*/
        }
    }

    private void recalculate(FeedTowerFeedType newFeedType) {
        towerMapper.findByCompanyId(RequestContextUtils.getRequestInfo().getCompanyId(), newFeedType.getId())
                .forEach(tower -> {
                    LambdaUpdateWrapper<FeedTower> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.set(FeedTower::getFeedTypeId, newFeedType.getId())
                            .set(FeedTower::getFeedType, newFeedType.getName())
                            .set(FeedTower::getDensity, newFeedType.getDensity());
                    Long tmp = tower.getResidualWeight();
                    if (ObjectUtil.isNotEmpty(tower.getResidualVolume())) {
                        Double g = ZmMathUtil.cmTm(tower.getResidualVolume())*newFeedType.getDensity();
                        wrapper.set(FeedTower::getResidualWeight, g.longValue());
                        tmp = g.longValue();

                        //更新日志表的加料记录
                        updateLastOneGoInByTowerId(tower.getId(),newFeedType.getDensity().doubleValue());
                    }
                    //towerMapper.update(tower, wrapper.eq(FeedTower::getId, tower.getId()));
                    towerMapper.updateFeedTypeById(tower.getId(), newFeedType.getId(), newFeedType.getName(), newFeedType.getDensity(),tmp);

                    //                    FeedTowerLog towerLog = towerLogMapper.selectOne(Wrappers.lambdaQuery(FeedTowerLog.class)
//                            .eq(FeedTowerLog::getTowerId, tower.getId())
//                            .eq(FeedTowerLog::getDeviceNo, tower.getDeviceNo())
//                            .eq(FeedTowerLog::getStatus, TowerStatus.completed.name())
//                            .orderByDesc(FeedTowerLog::getCreateTime).last("limit 1"));
//
//                    //存在就重新计算
//                    if (ObjectUtil.isNotEmpty(towerLog)) {
//                        try {
//                            CalResponse response = calForSelfDevNew(tower, JSONUtil.parseObj(towerLog.getData()), "料塔");
//                            double residualWeight = response.getWeight()<0?0:response.getWeight();
//                            tower.setResidualWeight((long) residualWeight);
//                            towerMapper.updateById(tower);
//                        } catch (Exception e) {
//                            log.info("变更密度后重新计算余料量异常：", e);
//                        }
//                    }

                    delTowerConfigCache(tower.getDeviceNo());
                });
    }

    @Override
    public void feedTypeDel(Long feedTypeId) {
        feedTypeMapper.deleteById(feedTypeId);
        RequestInfo info = RequestContextUtils.getRequestInfo();
        towerMapper.findByCompanyId(info.getCompanyId(), feedTypeId)
                .forEach(tower -> {
                    tower.setFeedTypeId(null);
                    tower.setFeedType(null);
                    tower.setDensity(null);
                    towerMapper.update(tower, new UpdateWrapper<FeedTower>().lambda()
                            .set(FeedTower::getFeedTypeId, null)
                            .set(FeedTower::getFeedType, null)
                            .set(FeedTower::getDensity, null)
                            .eq(FeedTower::getId, tower.getId())
                    );
                    delTowerConfigCache(tower.getDeviceNo());
                });
    }

    @Override
    public void del(Long towerId,Boolean check) {
        FeedTower tower = towerMapper.selectById(towerId);
        String device = tower.getDeviceNo();
        if(ObjectUtil.isEmpty(tower)){
            throw new BaseException("料塔不存在!");
        }

        if (ObjectUtil.isNotEmpty(tower.getDeviceNo())) {
            try {
                unbind(towerId,check);
            } catch (Exception e) {
                throw new BaseException("删除料塔时，解绑设备失败！".concat(e.getMessage()));
            }
        }
        delTowerConfigCache(device);
        towerMapper.deleteById(towerId);
    }

    /**
     * 二维码内容：
     * {"type":"TowerMonitor", "code":"48_55_19_d6_7c_9e", "version":"V1"}
     *
     * @param towerId
     * @param deviceNo
     */
    @Override
    public void bind(Long towerId, String deviceNo, String wifiAccount, String wifiPwd) throws InterruptedException {
        FeedTower tower = towerMapper.selectById(towerId);
        if (ObjectUtil.isNotEmpty(tower.getDeviceNo())) {
            throw new BaseException("该料塔已绑定设备【%s】", tower.getDeviceNo());
        }

        FeedTowerDevice device;
        Optional<FeedTowerDevice> optDevice = towerDeviceService.findByCache(deviceNo);
        if (optDevice.isPresent()) {
            device = optDevice.get();
            if (ObjectUtil.isNotNull(device.getTowerId()) && device.getTowerId() > 0) {
                throw new BaseException("该设备【%s】已绑定到料塔【%s】",
                        device.getDeviceNo(), towerMapper.selectById(device.getTowerId()).getName());
            }
        } else {
            throw new BaseException("该设备【%s】未入库！", deviceNo);
        }

        deviceBindBefore(tower, device, wifiAccount, wifiPwd);
        //加入MQTT订阅列表
        redissonClient.getTopic(RedisTopicEnum.tower_subscribe_topic.name(), new SerializationCodec()).publish(device.getDeviceNo());
        //通知设备绑定
        redissonClient.getTopic(RedisTopicEnum.tower_bind_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(Enable.ON).build());
        //阻塞，等待绑定结果
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(CacheKey.Queue.bind.build(deviceNo));
        String res = queue.poll(60, TimeUnit.SECONDS);
        if (ObjectUtil.isNull(res)) {
            //移出MQTT订阅列表
            redissonClient.getTopic(RedisTopicEnum.tower_unsubscribe_topic.name(), new SerializationCodec()).publish(deviceNo);
            throw new BaseException("设备【%s】绑定超时！", deviceNo);
        }
        if ("00".equals(res)) {
            //移出MQTT订阅列表
            redissonClient.getTopic(RedisTopicEnum.tower_unsubscribe_topic.name(), new SerializationCodec()).publish(deviceNo);
            throw new BaseException("设备【%s】绑定失败！", deviceNo);
        }
        queue.delete();
        delTowerConfigCache(tower.getDeviceNo());
        towerDeviceService.delByCache(tower.getDeviceNo());

        // 加入定时任务
        RSet<String> timers = redissonClient.getSet(CacheKey.Admin.tower_default_timer.key);
        timers.forEach(time -> taskService.towerAdd(device.getDeviceNo(), time));

    }

    @Override
    public void bind(String deviceNo, String towerId, String towerName) throws InterruptedException {
        Optional<FeedTowerDevice> opt = towerDeviceService.findByCache(deviceNo);
        if (!opt.isPresent()) {
            throw new BaseException("设备【%s】未入库", deviceNo);
        }

        if (ObjectUtil.isAllEmpty(towerId, towerName)) {
            throw new BaseException("请关联料塔");
        }

        FeedTower tower = null;
        RequestInfo info = RequestContextUtils.getRequestInfo();

        RLock lock = redissonClient.getLock(CacheKey.Lock.bind.build(deviceNo));
        try {
            boolean isLock = lock.tryLock(30, TimeUnit.SECONDS);
            if (isLock) {
                //已有料塔
                if (ObjectUtil.isNotEmpty(towerId)) {
                    tower = towerMapper.selectById(towerId);
                    if (ObjectUtil.isNotEmpty(tower.getDeviceNo())) {
                        return;
                    }
                    tower.setDeviceNo(deviceNo);
                    towerMapper.updateById(tower);
                }
                //没有料塔
                else {
                    boolean exists = towerMapper.exists(Wrappers.lambdaQuery(FeedTower.class).eq(FeedTower::getName, towerName));
                    if (exists) {
                        throw new BaseException("料塔名称不能重复");
                    }
                    tower = FeedTower.builder().companyId(info.getCompanyId()).pigFarmId(info.getPigFarmId())
                            .name(towerName).deviceNo(deviceNo).build();
                    if (towerName.contains("质检")) {
                        tower.setInitVolume(ZmMathUtil.m3ToCm3(100D));
                        tower.setDensity(65000L);
                        tower.setTemp(1);
                    }
//                    tower.setInitVolume(ZmMathUtil.m3ToCm3(100D));
                    towerMapper.insert(tower);
                }

                bindDevice(deviceNo);
            } else {
                return;
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        FeedTowerDevice device = opt.get();
        device.setCompanyId(tower.getCompanyId());
        device.setPigFarmId(tower.getPigFarmId());
        device.setTowerId(tower.getId());
        towerDeviceMapper.updateById(device);

        delTowerConfigCache(tower.getDeviceNo());
        towerDeviceService.delByCache(tower.getDeviceNo());

        //获取SIM卡号
        redissonClient.getTopic(RedisTopicEnum.tower_sim_topic.name(), new SerializationCodec()).publish(deviceNo);
        // 加入定时任务
        RSet<String> timers = redissonClient.getSet(CacheKey.Admin.tower_default_timer.key);
        timers.forEach(time -> taskService.towerAdd(device.getDeviceNo(), time));

    }

    public void bindDevice(String deviceNo) throws InterruptedException {
        //加入MQTT订阅列表
        redissonClient.getTopic(RedisTopicEnum.tower_subscribe_topic.name(), new SerializationCodec()).publish(deviceNo);
        //通知设备绑定
        redissonClient.getTopic(RedisTopicEnum.tower_bind_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(Enable.ON).build());
        //阻塞，等待绑定结果
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(CacheKey.Queue.bind.build(deviceNo));
        String res = queue.poll(20, TimeUnit.SECONDS);
        if (ObjectUtil.isNull(res)) {
            //移出MQTT订阅列表
            redissonClient.getTopic(RedisTopicEnum.tower_unsubscribe_topic.name(), new SerializationCodec()).publish(deviceNo);
            throw new BaseException("设备【%s】绑定超时！", deviceNo);
        }
        if ("00".equals(res)) {
            //移出MQTT订阅列表
            redissonClient.getTopic(RedisTopicEnum.tower_unsubscribe_topic.name(), new SerializationCodec()).publish(deviceNo);
            throw new BaseException("设备【%s】绑定失败！", deviceNo);
        }
        queue.delete();
    }

    //设备绑定成功之后处理
    private void deviceBindBefore(FeedTower tower, FeedTowerDevice device, String wifiAccount, String wifiPwd) {
        tower.setDeviceNo(device.getDeviceNo());
        towerMapper.updateById(tower);
        device.setCompanyId(tower.getCompanyId());
        device.setPigFarmId(tower.getPigFarmId());
        device.setTowerId(tower.getId());
        device.setWifiAccount(wifiAccount);
        device.setWifiPwd(wifiPwd);
        towerDeviceMapper.updateById(device);
    }

    @Override
    public void unbind(Long towerId, Boolean check) {
        FeedTower tower = towerMapper.selectById(towerId);
        if(ObjectUtil.isEmpty(tower)){
            throw new BaseException("料塔不存在!");
        }
        unbind(tower, CacheKey.Lock.bind.build(tower.getDeviceNo()),check);
    }

    @SneakyThrows
    @RedissonDistributedLock(key = "#lockKey", waitTime = 30)
    public void unbind(FeedTower tower, String lockKey, Boolean check) {
        //判断设备是否在线
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + tower.getDeviceNo());
        if (!bucket.isExists()) {
            throw new BaseException("设备已解绑！", tower.getDeviceNo());
        }
        DeviceStatus status = bucket.get();
        if ("离线".equals(status.getNetworkStatus())) {
            throw new BaseException("设备%s已离线", tower.getDeviceNo());
        }
        String deviceNo = tower.getDeviceNo();
        if(!check){
            //料塔设备是否在进行质检
            QueryDeviceCheck queryDeviceCheck = new QueryDeviceCheck();
            queryDeviceCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
            queryDeviceCheck.setDeviceNo(deviceNo);
            List<DeviceQualityCheck> list = deviceQualityCheckMapper.list(queryDeviceCheck);
            if(ObjectUtil.isNotEmpty(list)){
                throw new BaseException("设备正在质检!无法解绑!");
            }

            //料塔设备是否在进行老化
            QueryAgingCheck queryAgingCheck = new QueryAgingCheck();
            queryAgingCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
            queryAgingCheck.setDeviceNo(deviceNo);
            List<DeviceAgingCheck> aList = deviceAgingCheckMapper.list(queryAgingCheck);
            if(ObjectUtil.isNotEmpty(aList)){
                throw new BaseException("设备正在老化!无法解绑!");
            }

            //料塔设备是否在进行校准
            DeviceInitCheck deviceInitCheck = new DeviceInitCheck();
            deviceInitCheck.setCheckStatus(DeviceInitCheckStatusEnum.CHECKING.getStatus());
            deviceInitCheck.setDeviceNum(deviceNo);
            List<DeviceInitCheck> iList = deviceInitCheckMapper.list(deviceInitCheck);
            if(ObjectUtil.isNotEmpty(iList)){
                throw new BaseException("设备正在校准!无法解绑!");
            }
        }
        //设备解绑之前
        deviceUnbindBefore(tower);
        //阻塞，等待解绑结果
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(CacheKey.Queue.unbind.build(deviceNo));
        //通知设备解绑
        redissonClient.getTopic(RedisTopicEnum.tower_bind_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(Enable.OFF).build());
        String res = queue.poll(15, TimeUnit.SECONDS);
        if (ObjectUtil.isNull(res)) {
            throw new BaseException("设备解绑超时！");
        }
        if ("00".equals(res)) {
            throw new BaseException("设备解绑失败！");
        }
        queue.delete();
        //清理
        redissonClient.getTopic(RedisTopicEnum.tower_unsubscribe_topic.name(), new SerializationCodec()).publish(deviceNo);
        towerDeviceService.delByCache(deviceNo);
        delTowerConfigCache(deviceNo);
        towerQrtzService.deleteByDeviceNo(deviceNo);
    }

    private void deviceUnbindBefore(FeedTower tower) {
        String deviceNo = tower.getDeviceNo();
        FeedTower feedTower = towerMapper.selectById(tower.getId());
        Long initVo = null;
        if (ObjectUtil.isNotNull(feedTower) && ObjectUtil.isNotNull(feedTower.getCapacity()) && feedTower.getCapacity() > 0){
            initVo = feedTower.getCapacity() * 2;
        }
        towerMapper.update(tower,new UpdateWrapper<FeedTower>().lambda()
                .set(FeedTower::getIccid,null)
                .set(FeedTower::getDeviceNo,null)
                .set(FeedTower::getInit,0)
                .set(FeedTower::getInitVolume,initVo)
                .set(FeedTower::getResidualVolume,0)
                .set(FeedTower::getResidualWeight,0)
                .eq(FeedTower::getId,tower.getId()));
        Optional<FeedTowerDevice> opt = towerDeviceService.findByCache(deviceNo);
        opt.ifPresent(device -> {
            device.setCompanyId(0L);
            device.setPigFarmId(0L);
            device.setTowerId(0L);
            device.setNetMode(1L);
            towerDeviceMapper.updateById(device);
        });
    }


    @Override
    public void sendModbusId(Long deviceId, Integer modbusId) throws InterruptedException {
        FeedTowerDevice device = towerDeviceMapper.selectById(deviceId);
        //判断设备是否在线
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + device.getDeviceNo());
        if (!bucket.isExists()) {
            throw new BaseException("设备%s状态不存在", device.getDeviceNo());
        }
        DeviceStatus status = bucket.get();
        if ("离线".equals(status.getNetworkStatus())) {
            throw new BaseException("设备%s已离线", device.getDeviceNo());
        }

        redissonClient.getTopic(RedisTopicEnum.tower_modbus_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(device.getDeviceNo()).modbusId(modbusId).build());
        //阻塞，等待解绑结果
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(CacheKey.Queue.modbus.build(device.getDeviceNo()));
        String res = queue.poll(10, TimeUnit.SECONDS);
        if (ObjectUtil.isNull(res)) {
            throw new BaseException("发送ModbusId超时！");
        }
        if ("00".equals(res)) {
            throw new BaseException("发送ModbusId失败！");
        }
        device.setModbusId(modbusId);
        towerDeviceMapper.updateById(device);
    }

    @Override
    public void openBle(String deviceNo, Enable enable) {
        //判断设备是否在线
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + deviceNo);
        if (!bucket.isExists()) {
            throw new BaseException("设备%s状态不存在", deviceNo);
        }
        DeviceStatus status = bucket.get();
        if ("离线".equals(status.getNetworkStatus())) {
            throw new BaseException("设备%s已离线", deviceNo);
        }

        redissonClient.getTopic(RedisTopicEnum.tower_ble_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(enable).build());
    }

    @Override
    public TowerVo detail(Long towerId) {
        FeedTower tower = towerMapper.selectById(towerId);
        Integer residualPercent = null;
        if (tower.getInitVolume() != null && tower.getInitVolume() > 0 && tower.getResidualVolume() != null) {
            residualPercent = ZmMathUtil.getPercent(tower.getResidualVolume(), tower.getInitVolume());
        }
        //料塔状态标签
        TowerTabEnum tab;
        if (ObjectUtil.isEmpty(tower.getDeviceNo())) {
            tab = TowerTabEnum.UnBind;
        } else if (ObjectUtil.isEmpty(tower.getHouses())) {
            tab = TowerTabEnum.UnAssociation;
        } else if (ObjectUtil.notEqual(tower.getInit(), 1)) {
            tab = TowerTabEnum.UnCalibration;
        } else {
            tab = TowerTabEnum.Normal;
        }
        String houses = associationAnalysis(tower.getHouses());

        Integer schedule = null;
        if (ObjectUtil.isNotEmpty(tower.getDeviceNo())) {
            FeedTowerLog lastLog = towerLogService.lastLog(tower.getId(), tower.getDeviceNo(), null);
            if (ObjectUtil.isNotEmpty(lastLog)) {
                if (ObjectUtil.isNotNull(lastLog.getInitId())) {
                    DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(lastLog.getInitId());
                    if (DeviceInitCheckStatusEnum.CHECKING.getStatus() == deviceInitCheck.getCheckStatus()){
                        tab = TowerTabEnum.CalibrationIn;
                    }

                }else {
                    if (Stream.of(TowerStatus.starting.name(), TowerStatus.running.name()).anyMatch(s -> ObjectUtil.equals(s, lastLog.getStatus()))) {
                        if (ObjectUtil.equals(lastLog.getStartMode(), MeasureModeEnum.Init.getDesc())) {
                            tab = TowerTabEnum.CalibrationIn;
                        } else {
                            tab = TowerTabEnum.MeasureIn;
                        }
                    }
                }
                schedule = schedule(tower.getDeviceNo(), lastLog.getTaskNo(),lastLog);
            }
        }

        TowerVo.TowerVoBuilder builder = TowerVo.builder();
        if (ObjectUtil.isNotEmpty(tower.getDeviceNo())) {
            RBucket<DeviceStatus> status = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(tower.getDeviceNo()));
            if (status.isExists()) {
                builder.version(status.get().getVersion())
                        .deviceVersion(status.get().getVersionCode())
                        .temperature(status.get().getTemperature())
                        .boxTemperature(status.get().getBoxTemperature())
                        .humidity(status.get().getHumidity())
                        .network(status.get().getNetworkStatus())
                        .deviceStatus(ObjectUtil.isEmpty(status.get().getDeviceStatus())?"":status.get().getDeviceStatus().getDesc())
                        .deviceErrorInfo(status.get().getDeviceErrorInfo());
            }
        }
        //查询料塔的最后一次测量记录
        FeedTowerLog feedTowerLog = towerLogService.lastCompletedLog(towerId, tower.getDeviceNo());

        //猪场下所有料塔的当日用料和补料
        String todayUse = ZmMathUtil.gToTString(getOneTowerTodayUseORAnd(new ArrayList<Long>(){{this.add(towerId);}}, TowerLogStatusEnum.USE));
        String todayAdd = ZmMathUtil.gToTString(getOneTowerTodayUseORAnd(new ArrayList<Long>(){{this.add(towerId);}},TowerLogStatusEnum.ADD));

         boolean lowFeedWarning = false;
        if(StringUtils.isNotBlank(tower.getDeviceNo()) && tower.getInit() == 1 && ObjectUtil.isNotNull(tower.getWarning()) &&  ObjectUtil.isNotNull(residualPercent)  && (tower.getWarning()>residualPercent)){
            lowFeedWarning = true;
        }
        Long netMode = towerDeviceMapper.selectNetModeByDeviceNo(tower.getDeviceNo());
        return builder
                .id(tower.getId())
                .farmId(tower.getPigFarmId())
                .name(tower.getName())
                .deviceNo(tower.getDeviceNo())
                .capacity(ObjectUtil.isEmpty(tower.getCapacity())?"":ZmMathUtil.gToTString(tower.getCapacity()))
                .feedTypeId(tower.getFeedTypeId())
                .feedType(tower.getFeedType())
                .density(ZmMathUtil.gTokgDensityString(tower.getDensity()))
                .residualVolume(ZmMathUtil.cmTmString(tower.getResidualVolume()))
                .residualWeight(ZmMathUtil.gToTString(tower.getResidualWeight()))
                .residualPercentage(residualPercent)
                .warning(tower.getWarning())
                .houseIds(tower.getHouses())
                .houses(houses)
                .iccid(tower.getIccid())
                .init(tower.getInit())
                .initVolume(ZmMathUtil.cmTmString(tower.getInitVolume()))
                .refreshTime(feedTowerLog == null? null:feedTowerLog.getCreateTime())
                .todayUse(todayUse)
                .todayAdd(todayAdd)
                .tab(tab)
                .schedule(schedule)
                .lowFeedWarning(lowFeedWarning)
                .netMode(netMode!=null?netMode:1)
                .build();
    }
    @Override
    public TowerVo detailIn(Long towerId) {
        FeedTower tower = towerMapper.selectTowerById(towerId);
        if (ObjectUtil.isNull(tower)){
            throw new BaseException("料塔不存在！");
        }
        Integer residualPercent = null;
        if (tower.getInitVolume() != null && tower.getInitVolume() > 0 && tower.getResidualVolume() != null) {
            residualPercent = ZmMathUtil.getPercent(tower.getResidualVolume(), tower.getInitVolume());
        }
        //料塔状态标签
        TowerTabEnum tab;
        if (ObjectUtil.isEmpty(tower.getDeviceNo())) {
            tab = TowerTabEnum.UnBind;
        } else if (ObjectUtil.isEmpty(tower.getHouses())) {
            tab = TowerTabEnum.UnAssociation;
        } else if (ObjectUtil.notEqual(tower.getInit(), 1)) {
            tab = TowerTabEnum.UnCalibration;
        } else {
            tab = TowerTabEnum.Normal;
        }
        String houses = associationAnalysisIn(tower.getHouses());

        Integer schedule = null;
        if (ObjectUtil.isNotEmpty(tower.getDeviceNo())) {
            FeedTowerLog lastLog = towerLogService.lastLogIn(tower.getId(), tower.getDeviceNo(), null);
            if (ObjectUtil.isNotEmpty(lastLog)) {
                if (ObjectUtil.isNotNull(lastLog.getInitId())) {
                    DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectByIdIn(lastLog.getInitId());
                    if (DeviceInitCheckStatusEnum.CHECKING.getStatus() == deviceInitCheck.getCheckStatus()){
                        tab = TowerTabEnum.CalibrationIn;
                    }

                }else {
                    if (Stream.of(TowerStatus.starting.name(), TowerStatus.running.name()).anyMatch(s -> ObjectUtil.equals(s, lastLog.getStatus()))) {
                        if (ObjectUtil.equals(lastLog.getStartMode(), MeasureModeEnum.Init.getDesc())) {
                            tab = TowerTabEnum.CalibrationIn;
                        } else {
                            tab = TowerTabEnum.MeasureIn;
                        }
                    }
                }
                schedule = schedule(tower.getDeviceNo(), lastLog.getTaskNo(),lastLog);
            }
        }

        TowerVo.TowerVoBuilder builder = TowerVo.builder();
        if (ObjectUtil.isNotEmpty(tower.getDeviceNo())) {
            RBucket<DeviceStatus> status = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(tower.getDeviceNo()));
            if (status.isExists()) {
                builder.version(status.get().getVersion())
                        .deviceVersion(status.get().getVersionCode())
                        .temperature(status.get().getTemperature())
                        .boxTemperature(status.get().getBoxTemperature())
                        .humidity(status.get().getHumidity())
                        .network(status.get().getNetworkStatus())
                        .deviceStatus(ObjectUtil.isEmpty(status.get().getDeviceStatus())?"":status.get().getDeviceStatus().getDesc())
                        .deviceErrorInfo(status.get().getDeviceErrorInfo());
            }
        }
        //查询料塔的最后一次测量记录
        FeedTowerLog feedTowerLog = towerLogService.lastCompletedLogIn(towerId, tower.getDeviceNo());

        //猪场下所有料塔的当日用料和补料
        String todayUse = ZmMathUtil.gToTString(getOneTowerTodayUseORAnd(new ArrayList<Long>(){{this.add(towerId);}}, TowerLogStatusEnum.USE));
        String todayAdd = ZmMathUtil.gToTString(getOneTowerTodayUseORAnd(new ArrayList<Long>(){{this.add(towerId);}},TowerLogStatusEnum.ADD));

        boolean lowFeedWarning = false;
        if(StringUtils.isNotBlank(tower.getDeviceNo()) && tower.getInit() == 1 && ObjectUtil.isNotNull(tower.getWarning()) &&  ObjectUtil.isNotNull(residualPercent)  && (tower.getWarning()>residualPercent)){
            lowFeedWarning = true;
        }
        return builder
                .id(tower.getId())
                .farmId(tower.getPigFarmId())
                .name(tower.getName())
                .deviceNo(tower.getDeviceNo())
                .capacity(ObjectUtil.isEmpty(tower.getCapacity())?"":ZmMathUtil.gToTString(tower.getCapacity()))
                .feedTypeId(tower.getFeedTypeId())
                .feedType(tower.getFeedType())
                .density(ZmMathUtil.gTokgDensityString(tower.getDensity()))
                .residualVolume(ZmMathUtil.cmTmString(tower.getResidualVolume()))
                .residualWeight(ZmMathUtil.gToTString(tower.getResidualWeight()))
                .residualPercentage(residualPercent)
                .warning(tower.getWarning())
                .houseIds(tower.getHouses())
                .houses(houses)
                .iccid(tower.getIccid())
                .init(tower.getInit())
                .initVolume(ZmMathUtil.cmTmString(tower.getInitVolume()))
                .refreshTime(feedTowerLog == null? null:feedTowerLog.getCreateTime())
                .todayUse(todayUse)
                .todayAdd(todayAdd)
                .tab(tab)
                .schedule(schedule)
                .lowFeedWarning(lowFeedWarning)
                .build();
    }

    private String associationAnalysisIn(String houses) {
        if (ObjectUtil.equals(AssociationType.ZZLT.name(), houses)) {
            return "中转料塔";
        }
        if (ObjectUtil.isNotEmpty(houses)) {
            return Stream.of(houses.split(","))
                    .map(id -> ObjectUtil.isNotNull(houseMapper.selectByIdIn(Long.parseLong(id))) ? houseMapper.selectByIdIn(Long.parseLong(id)).getName() : "")
                    .collect(Collectors.joining(","));
        }
        return houses;
    }

    private String associationAnalysis(String houses) {
        if (ObjectUtil.equals(AssociationType.ZZLT.name(), houses)) {
            return "中转料塔";
        }
        if (ObjectUtil.isNotEmpty(houses)) {
            return Stream.of(houses.split(","))
                    .map(id -> ObjectUtil.isNotNull(houseMapper.selectById(Long.parseLong(id))) ? houseMapper.selectById(Long.parseLong(id)).getName() : "")
                    .collect(Collectors.joining(","));
        }
        return houses;
    }

    @Override
    public void measureCheck(String deviceNo, Optional<FeedTower> towerOpt, MeasureModeEnum mode) {
        if (!towerOpt.isPresent()) {
            throw new BaseException("设备%s绑定的料塔不存在！", deviceNo);
        }
        FeedTowerLog lastLog = towerLogService.lastLog(null, deviceNo, null);
        if (ObjectUtil.isNotNull(lastLog) && ListUtil.of(TowerStatus.starting.name(), TowerStatus.running.name()).contains(lastLog.getStatus())) {
            String tag = Stream.of(MeasureModeEnum.Auto.getDesc(), MeasureModeEnum.Manual.getDesc())
                    .anyMatch(m -> m.equals(lastLog.getStartMode()))?lastLog.getStartMode() + "测量"
                    :lastLog.getStartMode();
            throw new BaseException("设备正在【%s】运行，请稍等！", tag);
        }
        if (ObjectUtil.isNotNull(lastLog) && ObjectUtil.isNotNull(lastLog.getCompletedTime()) && !lastLog.getStartMode().equals(MeasureModeEnum.Aging.getDesc())) {
            long threshold = LocalDateTimeUtil.between(lastLog.getCompletedTime(), LocalDateTime.now(), ChronoUnit.SECONDS);
            if (threshold <= 20) {
                throw new BaseException("设备电机正在归位，请%d秒后再试！", 20 - threshold);
            }
        }
        FeedTower tower = towerOpt.get();
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + tower.getDeviceNo());
        if (!bucket.isExists()) {
            towerLogService.addTowerLog(tower, null, mode, "设备网络状态不存在", null, null, null);
            throw new BaseException("设备%s状态不存在！", deviceNo);
        }
        DeviceStatus status = bucket.get();
        if ("离线".equals(status.getNetworkStatus())) {
            towerLogService.addTowerLog(tower, null, mode, "设备离线", status.getNetworkStatus(), status.getTemperature(), status.getHumidity());
            throw new BaseException("设备%s离线,无法启动！", deviceNo);
        }
        if (Stream.of(DeviceStatusEnum.Standby).noneMatch(s -> ObjectUtil.equals(s, status.getDeviceStatus()))) {
            throw new BaseException("设备不在待机状态，无法启动！");
        }
        //如果设备在打料流程中,无法启动
        if(!MeasureModeEnum.AddAfter.equals(mode) && !MeasureModeEnum.AddBefore.equals(mode)){
            LambdaQueryWrapper<FeedTowerAdd> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FeedTowerAdd::getTowerId,tower.getId());
            wrapper.between(FeedTowerAdd::getCurrentState,TowerAddFeedingStatusEnum.ADD_BEFORE_TESTING.getStatus(),TowerAddFeedingStatusEnum.ADD_AFTER_TESTING.getStatus());
            FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectOne(wrapper);
            if(ObjectUtil.isNotEmpty(feedTowerAdd)){
                throw new BaseException("打料流程进行中!请等待打料流程结束后重试!");
            }
        }
        if(!MeasureModeEnum.QualityInspection.equals(mode)){
            //料塔设备是否在进行质检
            QueryDeviceCheck queryDeviceCheck = new QueryDeviceCheck();
            queryDeviceCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
            queryDeviceCheck.setDeviceNo(deviceNo);
            List<DeviceQualityCheck> list = deviceQualityCheckMapper.list(queryDeviceCheck);
            if(ObjectUtil.isNotEmpty(list)){
                throw new BaseException("设备正在质检!请等待质检结束后重试!");
            }
        }
        if(!MeasureModeEnum.Aging.equals(mode)){
            //料塔设备是否在进行质检
            QueryAgingCheck queryAgingCheck = new QueryAgingCheck();
            queryAgingCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
            queryAgingCheck.setDeviceNo(deviceNo);
            List<DeviceAgingCheck> list = deviceAgingCheckMapper.list(queryAgingCheck);
            if(ObjectUtil.isNotEmpty(list)){
                throw new BaseException("设备正在老化!请等待老化结束后重试!");
            }
        }
    }

    @Override
    public String measureCheckAging(String deviceNo, Optional<FeedTower> towerOpt, MeasureModeEnum mode) {
        if (!towerOpt.isPresent()) {
            return "设备"+deviceNo+"绑定的料塔不存在";
        }
        FeedTowerLog lastLog = towerLogService.lastLog(null, deviceNo, null);
        if (ObjectUtil.isNotNull(lastLog) && ListUtil.of(TowerStatus.starting.name(), TowerStatus.running.name()).contains(lastLog.getStatus())) {
            String tag = Stream.of(MeasureModeEnum.Auto.getDesc(), MeasureModeEnum.Manual.getDesc())
                    .anyMatch(m -> m.equals(lastLog.getStartMode()))?lastLog.getStartMode() + "测量"
                    :lastLog.getStartMode();
            return "设备正在"+tag+"运行，请稍等！";
        }
        if (ObjectUtil.isNotNull(lastLog) && ObjectUtil.isNotNull(lastLog.getCompletedTime()) && !lastLog.getStartMode().equals(MeasureModeEnum.Aging.getDesc())) {
            long threshold = LocalDateTimeUtil.between(lastLog.getCompletedTime(), LocalDateTime.now(), ChronoUnit.SECONDS);
            if (threshold <= 20) {
                return "设备电机正在归位，请"+(20 - threshold)+"秒后再试！";
            }
        }
        FeedTower tower = towerOpt.get();
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + tower.getDeviceNo());
        if (!bucket.isExists()) {
            towerLogService.addTowerLog(tower, null, mode, "设备网络状态不存在", null, null, null);
            return "设备"+deviceNo+"网络状态不存在！";
        }
        DeviceStatus status = bucket.get();
        if ("离线".equals(status.getNetworkStatus())) {
            towerLogService.addTowerLog(tower, null, mode, "设备离线", status.getNetworkStatus(), status.getTemperature(), status.getHumidity());
            return "设备"+deviceNo+"离线,无法启动！";
        }
        if (Stream.of(DeviceStatusEnum.Standby).noneMatch(s -> ObjectUtil.equals(s, status.getDeviceStatus()))) {
            return "设备不在待机状态，无法启动！";
        }
        //如果设备在打料流程中,无法启动
        if(!MeasureModeEnum.AddAfter.equals(mode) && !MeasureModeEnum.AddBefore.equals(mode)){
            LambdaQueryWrapper<FeedTowerAdd> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FeedTowerAdd::getTowerId,tower.getId());
            wrapper.between(FeedTowerAdd::getCurrentState,TowerAddFeedingStatusEnum.ADD_BEFORE_TESTING.getStatus(),TowerAddFeedingStatusEnum.ADD_AFTER_TESTING.getStatus());
            FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectOne(wrapper);
            if(ObjectUtil.isNotEmpty(feedTowerAdd)){
                return "打料流程进行中!请等待打料流程结束后重试!";
            }
        }
        if(!MeasureModeEnum.QualityInspection.equals(mode)){
            //料塔设备是否在进行质检
            QueryDeviceCheck queryDeviceCheck = new QueryDeviceCheck();
            queryDeviceCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
            queryDeviceCheck.setDeviceNo(deviceNo);
            List<DeviceQualityCheck> list = deviceQualityCheckMapper.list(queryDeviceCheck);
            if(ObjectUtil.isNotEmpty(list)){
                return "设备正在质检!请等待质检结束后重试!";
            }
        }
        if(!MeasureModeEnum.Aging.equals(mode)){
            //料塔设备是否在进行质检
            QueryAgingCheck queryAgingCheck = new QueryAgingCheck();
            queryAgingCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
            queryAgingCheck.setDeviceNo(deviceNo);
            List<DeviceAgingCheck> list = deviceAgingCheckMapper.list(queryAgingCheck);
            if(ObjectUtil.isNotEmpty(list)){
                return "设备正在老化!请等待老化结束后重试!";
            }
        }
        return "Y";
    }

    @Override
    public FeedTowerLog measureInit(String deviceNo, MeasureModeEnum mode) {
        return measureStartWithMode(deviceNo, mode);
    }

    @Override
    public void measureInitAverage(String deviceNo, MeasureModeEnum mode,Integer checkNo) {
        measureStartWithModeAverageFirst(deviceNo, mode, checkNo);
    }

    @Override
    public FeedTowerLog measureStartWithMode(String deviceNo, MeasureModeEnum mode) {
        Optional<FeedTower> towerOpt = find(deviceNo);
        measureCheck(deviceNo, towerOpt, mode);
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + deviceNo);
        DeviceStatus status = bucket.get();
        FeedTowerLog towerLog = towerLogService.addTowerLog(towerOpt.get(), null, mode, null, status.getNetworkStatus(), status.getTemperature(), status.getHumidity());
        eventPublisher.publishEvent(new MeasureEvent(this, towerLog.getId(), towerLog.getDeviceNo()));
        return towerLog;
    }

    @Override
    public FeedTowerLog measureStartWithModeAverageFirst(String deviceNo, MeasureModeEnum mode,Integer checkNo) {
        Optional<FeedTower> towerOpt = find(deviceNo);
        measureCheck(deviceNo, towerOpt, mode);
        FeedTower feedTower = towerOpt.get();
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + deviceNo);
        DeviceStatus status = bucket.get();
        DeviceInitCheck initCheck = insertInitCheck(deviceNo, checkNo, feedTower);
        FeedTowerLog towerLog = towerLogService.addTowerLogInit(feedTower, null, mode, null, status.getNetworkStatus(), status.getTemperature(), status.getHumidity(),initCheck.getId());
        eventPublisher.publishEvent(new MeasureEvent(this, towerLog.getId(), towerLog.getDeviceNo()));
        return towerLog;
    }

    @Override
    @Transactional(readOnly = false)
    public void measureStartWithModeAverage(String deviceNo, MeasureModeEnum mode,Long initId) {
        taskExecutor.execute(()->{
            sleep(21000);
            Optional<FeedTower> towerOpt = find(deviceNo);
            FeedTower feedTower = towerOpt.get();
            String s ;
            for (int i = 1; i <= 3; i++) {
                s = measureCheckAging(deviceNo, towerOpt, mode);
                if ("Y".equals(s)){
                    RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + deviceNo);
                    DeviceStatus status = bucket.get();
                    FeedTowerLog towerLog = towerLogService.addTowerLogInit(feedTower, null, mode, null, status.getNetworkStatus(), status.getTemperature(), status.getHumidity(),initId);
                    eventPublisher.publishEvent(new MeasureEvent(this, towerLog.getId(), towerLog.getDeviceNo()));
                    break;
                }
                if (i == 3){
                    DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(initId);
                    deviceInitCheck.setCheckStatus(DeviceInitCheckStatusEnum.ERR.getStatus());
                    deviceInitCheck.setEndTime(LocalDateTime.now());
                    deviceInitCheck.setRemark("校准过程中启动设备失败,系统自动终止校准");
                    deviceInitCheckMapper.updateById(deviceInitCheck);
                }
                sleep(21000);
            }
        });
    }

    /**
     * 新增校准表数据
     * @param deviceNo
     * @param checkNo
     * @param feedTower
     * @return
     */
    private DeviceInitCheck insertInitCheck(String deviceNo, Integer checkNo, FeedTower feedTower) {
        DeviceInitCheck deviceInitCheck = DeviceInitCheck.builder()
                .towerId(feedTower.getId())
                .deviceNum(deviceNo)
                .checkCount(checkNo)
                .runCount(0)
                .errCount(0)
                .startTime(LocalDateTime.now())
                .checkStatus(DeviceInitCheckStatusEnum.CHECKING.getStatus())
                .companyId(feedTower.getCompanyId())
                .pigFarmId(feedTower.getPigFarmId())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .handle(DeviceCheckHandleEnum.AUTO.getStatus())
                .build();
        deviceInitCheckMapper.insert(deviceInitCheck);
        return deviceInitCheck;
    }

    @Override
    public FeedTowerLog measureStartAgingWithDelay(String deviceNo) {
        Optional<FeedTower> towerOpt = find(deviceNo);
        measureCheck(deviceNo, towerOpt, MeasureModeEnum.Aging);
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + deviceNo);
        DeviceStatus status = bucket.get();
        FeedTowerLog towerLog = towerLogService.addTowerLog(towerOpt.get(), null, MeasureModeEnum.Aging, null, status.getNetworkStatus(), status.getTemperature(), status.getHumidity());
        taskExecutor.execute(()->{
            sleep(20000);
            eventPublisher.publishEvent(new MeasureEvent(this, towerLog.getId(), towerLog.getDeviceNo()));});
        return towerLog;
    }

    @Override
    public FeedTowerLog measureStartWithModeAging(String deviceNo, MeasureModeEnum mode,Long agingId) {
        Optional<FeedTower> towerOpt = find(deviceNo);
        measureCheck(deviceNo, towerOpt, mode);
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + deviceNo);
        DeviceStatus status = bucket.get();
        FeedTowerLog towerLog = towerLogService.addTowerLogAging(towerOpt.get(), null, mode, null, status.getNetworkStatus(), status.getTemperature(), status.getHumidity(),agingId);
        eventPublisher.publishEvent(new MeasureEvent(this, towerLog.getId(), towerLog.getDeviceNo()));
        return towerLog;
    }

    @Override
    @Transactional(readOnly = false)
    public void measureStartAgingWithDelayAging(String deviceNo,Long agingId) {
        taskExecutor.execute(()->{
            sleep(21000);
            Optional<FeedTower> towerOpt = find(deviceNo);
            String s ;
            for (int i = 1; i <= 5; i++) {
                s = measureCheckAging(deviceNo, towerOpt, MeasureModeEnum.Aging);
                log.info("老化Id：{} 设备编码：{} 第{}次延时启动检测结果：{}",agingId,deviceNo,i,s);
                if ("Y".equals(s)){
                    RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + deviceNo);
                    DeviceStatus status = bucket.get();
                    FeedTowerLog towerLog = towerLogService.addTowerLogAging(towerOpt.get(), null, MeasureModeEnum.Aging, null, status.getNetworkStatus(), status.getTemperature(), status.getHumidity(),agingId);
                    eventPublisher.publishEvent(new MeasureEvent(this, towerLog.getId(), towerLog.getDeviceNo()));
                    break;
                }
                if (i == 5){
                    DeviceAgingCheck deviceAgingCheck = deviceAgingCheckMapper.selectById(agingId);
                    deviceAgingCheck.setPass(DeviceCheckStatusEnum.CANCEL.getStatus());
                    deviceAgingCheck.setEndTime(LocalDateTime.now());
                    deviceAgingCheck.setRemark("老化过程中启动设备失败,系统自动终止老化("+s+")");
                    deviceAgingCheckMapper.updateById(deviceAgingCheck);
                }
                sleep(20000);
            }
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void measure(MeasureEvent event) {
        redissonClient.getTopic(RedisTopicEnum.tower_measure_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(event.getDeviceNo()).enable(Enable.ON).logId(event.getLogId()).build());
    }

    @Override
    public void measureStop(String deviceNo, String taskNo) {
        redissonClient.getTopic(RedisTopicEnum.tower_measure_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(Enable.OFF).taskNo(taskNo).build());
        redissonClient.getList(CacheKey.Admin.tower_cache_data.key + deviceNo + ":" + taskNo).delete();
    }

    @Override
    public void agingStop(String deviceNo, String taskNo,Long agingId) {
        if (StringUtils.isNotBlank(deviceNo) && StringUtils.isNotBlank(taskNo) && ObjectUtil.isNotNull(agingId)){
            agingStopOne(deviceNo, taskNo, agingId);
        }else if (StringUtils.isBlank(deviceNo) && StringUtils.isBlank(taskNo) && ObjectUtil.isNull(agingId)){
            QueryAgingCheck AgingCheck = new QueryAgingCheck();
            AgingCheck.setPass(DeviceCheckStatusEnum.TESTING.getStatus());
            List<DeviceAgingCheck> list = deviceAgingCheckMapper.list(AgingCheck);
            list.forEach(li -> {
                String no = towerLogMapper.selectTaskNoByIdAging(li.getId());
                if (StringUtils.isNotBlank(no)) {
                    agingStopOne(li.getDeviceNum(),no, li.getId());
                }else {
                    throw  new BaseException("编码为:"+li.getDeviceNum()+"的设备无taskNo,停止失败！");
                }
            });
        }

    }

    /**
     * 停止单个老化
     * @param deviceNo
     * @param taskNo
     * @param agingId
     */
    private void agingStopOne(String deviceNo, String taskNo, Long agingId) {
        redissonClient.getTopic(RedisTopicEnum.tower_measure_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(Enable.OFF).taskNo(taskNo).build());
        redissonClient.getList(CacheKey.Admin.tower_cache_data.key + deviceNo + ":" + taskNo).delete();
        //更新老化pass
        DeviceAgingCheck deviceAgingCheck = deviceAgingCheckMapper.selectById(agingId);
        deviceAgingCheck.setPass(DeviceCheckStatusEnum.CANCEL.getStatus());
        deviceAgingCheck.setUpdateTime(LocalDateTime.now());
        deviceAgingCheck.setEndTime(LocalDateTime.now());
        deviceAgingCheck.setRemark("老化员主动停止老化");
        deviceAgingCheckMapper.updateById(deviceAgingCheck);
    }

    /**
     * 停止单个校验
     * @param deviceNo
     * @param taskNo
     * @param initId
     */
    private void initStopOne(String deviceNo, String taskNo, Long initId) {
        redissonClient.getTopic(RedisTopicEnum.tower_measure_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(Enable.OFF).taskNo(taskNo).build());
        redissonClient.getList(CacheKey.Admin.tower_cache_data.key + deviceNo + ":" + taskNo).delete();
        //更新老化pass
        DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(initId);
        deviceInitCheck.setCheckStatus(DeviceInitCheckStatusEnum.ERR.getStatus());
        deviceInitCheck.setUpdateTime(LocalDateTime.now());
        deviceInitCheck.setEndTime(LocalDateTime.now());
        deviceInitCheck.setRemark("校准员主动停止校准");
        deviceInitCheckMapper.updateById(deviceInitCheck);
    }

    @Override
    public TowerMeasureInfoVo measureInfo(Long towerId, String modeEnum) {
        TowerMeasureInfoVo.TowerMeasureInfoVoBuilder builder = TowerMeasureInfoVo.builder();
            //根据料塔查询设备
        FeedTower feedTower = towerMapper.selectById(towerId);
        String deviceNo = feedTower.getDeviceNo();
        builder.initVolume(ZmMathUtil.cmTm(feedTower.getInitVolume()))
                .initDate(LocalDateTimeUtil.formatNormal(feedTower.getInitTime())).init(feedTower.getInit());
        if(ObjectUtil.isEmpty(deviceNo)){
            throw new BaseException("该料塔暂未绑定设备!");
        }
        MeasureModeEnum mode = ObjectUtil.isNotEmpty(modeEnum)?MeasureModeEnum.valueOf(modeEnum):null;
        FeedTowerLog towerLog = towerLogService.lastLog(towerId, deviceNo, mode);
        if (ObjectUtil.isEmpty(towerLog)) {
            builder.deviceNo(deviceNo);
            return builder.build();
        }
        RBucket<Long> init = redissonClient.getBucket(CacheKey.Admin.tower_init_data.key + deviceNo);
        if (init.isExists()) {
            builder.initConfirm(ZmMathUtil.cmTmString(init.get()));
        }

        //查看设备是否正在运行
        Integer schedule = null;
        FeedTowerLog lastLog = towerLogService.lastLog(null, deviceNo, null);
        if (ObjectUtil.isNotEmpty(lastLog)) {
            if (Stream.of(TowerStatus.starting.name(), TowerStatus.running.name()).anyMatch(s -> ObjectUtil.equals(s, lastLog.getStatus()))) {

                if (ObjectUtil.isNotNull(lastLog.getInitId())) {
                    DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(lastLog.getInitId());
                    if (DeviceInitCheckStatusEnum.CHECKING.getStatus() == deviceInitCheck.getCheckStatus()){
                        builder.currStatus(TowerTabEnum.CalibrationIn.name());
                    }

                }else {
                    if (MeasureModeEnum.Init.getDesc().equals(lastLog.getStartMode())) {
                        builder.currStatus(TowerTabEnum.CalibrationIn.name());
                    } else if (Stream.of(MeasureModeEnum.Auto.getDesc(), MeasureModeEnum.Manual.getDesc())
                            .anyMatch(m -> ObjectUtil.equals(m, lastLog.getStartMode()))) {
                        builder.currStatus(TowerTabEnum.MeasureIn.name());
                    }
                }

            }
        }
        schedule = schedule(deviceNo, towerLog.getTaskNo(),lastLog);

        builder.deviceNo(deviceNo)
                .beginTime(DateUtil.format(towerLog.getCreateTime(), "MM-dd HH:mm"))
                .volume(ZmMathUtil.cmTmString(towerLog.getVolume()))
                .density(ZmMathUtil.gTokgDensityString(towerLog.getTowerDensity()))
                .startupMode(towerLog.getStartMode())
                .weight(ZmMathUtil.gToTString(towerLog.getWeight()))
                .status(towerLog.getStatus())
                .completedTime(ZmDateUtil.localDateTimeToString(towerLog.getCompletedTime()))
                .remark(towerLog.getRemark())
                .taskNo(towerLog.getTaskNo())
                .schedule(schedule);
        return builder.build();
    }

    @Override
    public TowerMeasureInfoVo measureInfoAverage(Long towerId, String modeEnum) {
        TowerMeasureInfoVo.TowerMeasureInfoVoBuilder builder = TowerMeasureInfoVo.builder();
        //根据料塔查询设备
        FeedTower feedTower = towerMapper.selectById(towerId);
        String deviceNo = feedTower.getDeviceNo();
        builder.initVolume(ZmMathUtil.cmTm(feedTower.getInitVolume()))
                .initDate(LocalDateTimeUtil.formatNormal(feedTower.getInitTime())).init(feedTower.getInit());
        if(ObjectUtil.isEmpty(deviceNo)){
            throw new BaseException("该料塔暂未绑定设备!");
        }
        MeasureModeEnum mode = ObjectUtil.isNotEmpty(modeEnum)?MeasureModeEnum.valueOf(modeEnum):null;
        FeedTowerLog towerLog = towerLogService.lastLog(towerId, deviceNo, mode);
        if (ObjectUtil.isEmpty(towerLog)) {
            builder.deviceNo(deviceNo);
            return builder.build();
        }
        RBucket<Long> init = redissonClient.getBucket(CacheKey.Admin.tower_init_data.key + deviceNo);
        if (init.isExists()) {
            //builder.initConfirm(ZmMathUtil.cmTmString(init.get()));
        }

        //查看设备是否正在运行
        Integer schedule = null;
        DeviceInitCheck deviceInitCheck = null;
        if (ObjectUtil.isNotNull(towerLog.getInitId())) {
            deviceInitCheck = deviceInitCheckMapper.selectById(towerLog.getInitId());
        }
        FeedTowerLog lastLog = towerLogService.lastLog(null, deviceNo, null);
        if (ObjectUtil.isNotEmpty(lastLog)) {
            if (Stream.of(TowerStatus.starting.name(), TowerStatus.running.name()).anyMatch(s -> ObjectUtil.equals(s, lastLog.getStatus()))) {

                if (ObjectUtil.isNotNull(lastLog.getInitId())) {

                    if (DeviceInitCheckStatusEnum.CHECKING.getStatus() == deviceInitCheck.getCheckStatus()){
                        builder.currStatus(TowerTabEnum.CalibrationIn.name());
                    }

                }else {
                    if (MeasureModeEnum.Init.getDesc().equals(lastLog.getStartMode())) {
                        builder.currStatus(TowerTabEnum.CalibrationIn.name());
                    } else if (Stream.of(MeasureModeEnum.Auto.getDesc(), MeasureModeEnum.Manual.getDesc())
                            .anyMatch(m -> ObjectUtil.equals(m, lastLog.getStartMode()))) {
                        builder.currStatus(TowerTabEnum.MeasureIn.name());
                    }
                }

            }
        }
        schedule = schedule(deviceNo, towerLog.getTaskNo(),towerLog);


        Long comCount = 0L;
        if (ObjectUtil.isNotNull(towerLog) && ObjectUtil.isNotNull(towerLog.getInitId())) {
            comCount = towerLogMapper.selectRunCountByInitIdToCom(towerLog.getInitId());
        }
        String status = "";
        if (ObjectUtil.isNotNull(deviceInitCheck)) {
            if (comCount < deviceInitCheck.getCheckCount() && DeviceInitCheckStatusEnum.CHECKING.getStatus() == deviceInitCheck.getCheckStatus()){
                status = TowerStatus.running.name();
            }else if (comCount.intValue() == deviceInitCheck.getCheckCount() && DeviceInitCheckStatusEnum.NORMAL.getStatus() == deviceInitCheck.getCheckStatus()){
                status = TowerStatus.completed.name();
            }else {
                status = towerLog.getStatus();
            }
        }

        builder.deviceNo(deviceNo)
                .beginTime(DateUtil.format((ObjectUtil.isNotNull(deviceInitCheck)
                        && Objects.requireNonNull(deviceInitCheck).getStartTime() != null ? deviceInitCheck.getStartTime() : LocalDateTime.now() ), "MM-dd HH:mm"))
                .volume(ZmMathUtil.cmTmString(ObjectUtil.isNotNull(feedTower.getInitVolume()) ? feedTower.getInitVolume() : 0))
                //.density(ZmMathUtil.gTokgDensityString(towerLog.getTowerDensity()))
                .startupMode(towerLog.getStartMode())
                //.weight(ZmMathUtil.gToTString(towerLog.getWeight()))
                .status(status)
                .completedTime(ZmDateUtil.localDateTimeToString((ObjectUtil.isNotNull(deviceInitCheck) && ObjectUtil.isNotNull(deviceInitCheck.getEndTime())) ? deviceInitCheck.getEndTime() : null))
                .remark((ObjectUtil.isNotNull(deviceInitCheck) && StringUtils.isNotBlank(Objects.requireNonNull(deviceInitCheck).getRemark()))
                        ?deviceInitCheck.getRemark() :"")
                .taskNo(towerLog.getTaskNo())
                .initId(towerLog.getInitId())
                .schedule(schedule);
        return builder.build();
    }

    @Override
    public void initConfirm(Long towerId, boolean confirm) {
        FeedTower tower = towerMapper.selectById(towerId);
        RBucket<Long> initConfirm = redissonClient.getBucket(CacheKey.Admin.tower_init_data.key + tower.getDeviceNo());
        if (confirm) {
            tower.setInitVolume(initConfirm.get());
            tower.setInit(1);
            tower.setInitTime(LocalDateTime.now());
            // 校准完成之后重置余料量和余料体积为0
            tower.setResidualWeight(0L);
            tower.setResidualVolume(0L);
            tower.setResidualDate(LocalDateTime.now());
            towerMapper.updateById(tower);
            delTowerConfigCache(tower.getDeviceNo());
        }
        initConfirm.delete();
    }

    private Integer schedule(String deviceNo, String taskNo,FeedTowerLog feedTowerLog) {
        Integer schedule = null;
        RList<FeedTowerMsg> data = redissonClient.getList(CacheKey.Admin.tower_cache_data.key + deviceNo + ":" + taskNo);

        if (data.isExists() && !data.isEmpty()) {
            schedule = Math.min((int) (data.size()*1.25), 99);
        }

        if (ObjectUtil.isNotNull(feedTowerLog) && ObjectUtil.isNotNull(feedTowerLog.getInitId())) {
            DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectByIdIn(feedTowerLog.getInitId());
            double decimal = getSchedule(deviceNo, taskNo,feedTowerLog);
            double a = 0;
            if (ObjectUtil.isNotNull(data.size())) {
                a = (double) data.size() +decimal;
            }else {
                a = decimal;
            }
            double b = (a*1.25)/((deviceInitCheck.getCheckCount()!= 0 ? deviceInitCheck.getCheckCount() : 1));
            schedule = Math.min((int) (b), 99);
        }


        return schedule;
    }

    /**
     * 计算比例
     * @param deviceNo
     * @param taskNo
     * @return
     */
    private double getSchedule(String deviceNo, String taskNo,FeedTowerLog feedTowerLog) {
        Long count = towerLogMapper.selectRunCountByInitIdToCom(feedTowerLog.getInitId());
        return count*80;
    }

    /**
     * 计算校验次数站总次数比例
     * @return
     */
    private double getAtomicLong(FeedTowerLog feedTowerLog,double decimal) {
        if (ObjectUtil.isNotNull(feedTowerLog) && ObjectUtil.isNotNull(feedTowerLog.getInitId()) && feedTowerLog.getInitId() != 0){
            double runCount = towerLogMapper.selectRunCountByInitId(feedTowerLog.getInitId()).doubleValue();
            DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(feedTowerLog.getInitId());
            double checkCount = deviceInitCheck.getCheckCount().doubleValue();
            decimal=runCount/checkCount;
        }
        return decimal;
    }

    @Override
    public void reboot(String deviceNo) {
        redissonClient.getTopic(RedisTopicEnum.tower_reboot_topic.name(), new SerializationCodec()).publish(deviceNo);
    }

    @Override
    public void findDeviceBySound(String deviceNo, Enable enable) {
        redissonClient.getTopic(RedisTopicEnum.tower_find_device_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(enable).build());
    }

    @Override
    public void cleanDust(String deviceNo, Enable enable) {
        redissonClient.getTopic(RedisTopicEnum.tower_clean_dust_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(deviceNo).enable(enable).build());
    }

    @Override
    public void factoryDefault(String deviceNo) {
        redissonClient.getTopic(RedisTopicEnum.tower_factory_default_topic.name(), new SerializationCodec()).publish(deviceNo);
    }

    @Override
    public void delTowerConfigCache(String deviceNo) {
        redissonClient.getBucket(CacheKey.Admin.tower_config.key + deviceNo).delete();
    }

    @Override
    public void startAll() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        Long userId = info.getResourceType().equals(ResourceType.JX)?info.getUserId():null;
        List<FeedTower> list = list(null, info.getPigFarmId(), null, userId);
        list.stream().filter(tower -> ObjectUtil.isNotEmpty(tower.getDeviceNo())).forEach(tower -> {
            try {
                measureStartWithMode(tower.getDeviceNo(), MeasureModeEnum.Manual);
            } catch (Exception e) {
                log.info(String.format("跳过料塔%s", tower.getName()), e);
            }
        });
    }

    @Override
    public TowerReportByDayVo oneTimeDetail(Long towerId, Date startTime,Date endTime) {
        FeedTower feedTower = towerMapper.selectById(towerId);
        TowerReportByDayVo towerReportByDayVo = new TowerReportByDayVo();
        towerReportByDayVo.setFeedType(feedTower.getFeedType());
        List<FeedTowerLog> useList = towerLogService.getTowerTimeUseORAndList(ListUtil.of(towerId), startTime,endTime, TowerLogStatusEnum.USE);
        ArrayList<TowerLogReportVo> useTempList = new ArrayList<>();
        ArrayList<TowerLogReportVo> addTempList = new ArrayList<>();
        ArrayList<TowerLogReportVo> all = new ArrayList<>();
        useList.forEach(oneDayDetail -> {
            TowerLogReportVo towerLogReportVo = new TowerLogReportVo();
            towerLogReportVo.setDayStr(ZmDateUtil.localDateTimeToString(oneDayDetail.getCreateTime()));
            towerLogReportVo.setTime(oneDayDetail.getCreateTime());
            towerLogReportVo.setVariation(oneDayDetail.getVariation());
            towerLogReportVo.setVariationString(ZmMathUtil.gToTString(oneDayDetail.getVariation()));
            towerLogReportVo.setStatus(oneDayDetail.getModified());
            useTempList.add(towerLogReportVo);
            all.add(towerLogReportVo);
        });

        List<FeedTowerLog> addList = towerLogService.getTowerTimeUseORAndList(ListUtil.of(towerId), startTime,endTime,  TowerLogStatusEnum.ADD);
        addList.forEach(oneDayDetail -> {
            TowerLogReportVo towerLogReportVo = new TowerLogReportVo();
            towerLogReportVo.setDayStr(ZmDateUtil.localDateTimeToString(oneDayDetail.getCreateTime()));
            towerLogReportVo.setTime(oneDayDetail.getCreateTime());
            towerLogReportVo.setVariationAdd(oneDayDetail.getVariation());
            towerLogReportVo.setVariationAddString(ZmMathUtil.gToTString(oneDayDetail.getVariation()));
            towerLogReportVo.setStatus(oneDayDetail.getModified());
            addTempList.add(towerLogReportVo);
            all.add(towerLogReportVo);
        });
        List<TowerLogReportVo> useListFinal = useTempList.stream().sorted(Comparator.comparing(TowerLogReportVo::getTime).reversed()).collect(toList());
        List<TowerLogReportVo> addListFinal = addTempList.stream().sorted(Comparator.comparing(TowerLogReportVo::getTime).reversed()).collect(toList());

        towerReportByDayVo.setUse(useListFinal);
        towerReportByDayVo.setAdd(addListFinal);

        towerReportByDayVo.setUseTotal(ZmMathUtil.gToTString(useList.stream().mapToLong(FeedTowerLog::getVariation).sum()));
        towerReportByDayVo.setAddTotal(ZmMathUtil.gToTString(addList.stream().mapToLong(FeedTowerLog::getVariation).sum()));

        List<TowerLogReportVo> allList = all.stream().sorted(Comparator.comparing(TowerLogReportVo::getTime).reversed()).collect(toList());
        towerReportByDayVo.setAll(allList);
        return towerReportByDayVo;
    }


    @Override
    public TowerReportByDayVo reportByTime(Long towerId, String time, String type) {
        TowerReportByDayVo towerReportByDayVo;
        if("day".equals(type)){
             towerReportByDayVo = oneTimeDetail(towerId, ZmDateUtil.getDateStartTime(DateUtil.parse(time)), ZmDateUtil.getDateEndTime(DateUtil.parse(time)));
        }else if("month".equals(type)){
             towerReportByDayVo = oneTimeDetail(towerId, DateUtil.beginOfMonth(DateUtil.parse(time)), DateUtil.endOfMonth(DateUtil.parse(time)) );
        }else if("year".equals(type)){
             towerReportByDayVo = oneTimeDetail(towerId, DateUtil.beginOfYear(DateUtil.parse(time)), DateUtil.endOfYear(DateUtil.parse(time)) );
        }else{
            return null;
        }
        return towerReportByDayVo;
    }

    @Override
    public FeedTowerDevice scanFind(String content) {
        JSONObject obj = JSONUtil.parseObj(content);
        if (!obj.containsKey("type") || !"TowerMonitor".equals(obj.getStr("type"))) {
            throw new BaseException("请扫描泽牧科技料塔设备二维码");
        }
        Optional<FeedTowerDevice> opt = towerDeviceService.findByCache(obj.getStr("code"));
        if (opt.isPresent()) {
            FeedTowerDevice device = opt.get();
            if (ObjectUtil.isNotNull(device.getPigFarmId()) && device.getPigFarmId() > 0) {
                PigFarm farm = farmMapper.selectById(device.getPigFarmId());
                throw new BaseException("该设备【%s】已绑定到猪场【%s】", device.getDeviceNo(), farm.getName());
            }
            return device;
        }
        throw new BaseException("设备不存在");
    }

    @Override
    public List<FeedTowerDevice> search(String deviceNo) {
        PageHelper.startPage(1, 6);
        LambdaQueryWrapper<FeedTowerDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTowerDevice::getPigFarmId, 0L);
        wrapper.like(FeedTowerDevice::getDeviceNo, deviceNo);
        return towerDeviceMapper.selectList(wrapper);
    }

    @Override
    public void deviceModify(Long deviceId, String name) {
        FeedTowerDevice device = towerDeviceMapper.selectById(deviceId);
        device.setName(name);
        towerDeviceMapper.updateById(device);
        towerDeviceService.delByCache(device.getDeviceNo());
    }

    @Override
    public void deviceWifi(Long deviceId, String wifiAccount, String wifiPwd) throws InterruptedException {
        FeedTowerDevice device = towerDeviceMapper.selectById(deviceId);
        //判断设备是否在线
        RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key + device.getDeviceNo());
        if (!bucket.isExists()) {
            throw new BaseException("设备%s状态不存在", device.getDeviceNo());
        }
        DeviceStatus status = bucket.get();
        if ("离线".equals(status.getNetworkStatus())) {
            throw new BaseException("设备%s已离线", device.getDeviceNo());
        }

        redissonClient.getTopic(RedisTopicEnum.tower_wifi_topic.name(), new SerializationCodec())
                .publish(TowerRedisDto.builder().deviceNo(device.getDeviceNo()).wifiAccount(wifiAccount).wifiPwd(wifiPwd).build());
        //阻塞，等待解绑结果
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(CacheKey.Queue.wifiAccount.build(device.getDeviceNo()));
        String res = queue.poll(10, TimeUnit.SECONDS);
        if (ObjectUtil.isNull(res)) {
            throw new BaseException("发送WiFi账号超时！");
        }
        if ("00".equals(res)) {
            throw new BaseException("发送WiFi账号失败！");
        }

        queue = redissonClient.getBlockingQueue(CacheKey.Queue.wifiPwd.build(device.getDeviceNo()));
        res = queue.poll(10, TimeUnit.SECONDS);
        if (ObjectUtil.isNull(res)) {
            throw new BaseException("发送WiFi密码超时！");
        }
        if ("00".equals(res)) {
            throw new BaseException("发送WiFi密码失败！");
        }
        device.setWifiAccount(wifiAccount);
        device.setWifiPwd(wifiPwd);
        towerDeviceMapper.updateById(device);
        towerDeviceService.delByCache(device.getDeviceNo());
    }

    //查询单个/多个料塔今日用料/补料
    @Override
    public Long getOneTowerTodayUseORAnd(List<Long> towerIds, TowerLogStatusEnum statusEnum) {
        List<FeedTowerLog> towerLogs = towerLogService.getTowerOneDayUseORAndList(towerIds, new Date(), statusEnum);
        return towerLogs.stream().map(FeedTowerLog::getVariation).reduce(0L, Long::sum);
    }

    //查询单个/多个料塔   某日   用料/补料
    @Override
    public Long getTowerOneDayUseORAnd(List<Long> towerIds, Date date, TowerLogStatusEnum statusEnum) {
        List<FeedTowerLog> towerLogs = towerLogService.getTowerOneDayUseORAndList(towerIds, date, statusEnum);
        return towerLogs.stream().map(FeedTowerLog::getVariation).reduce(0L, Long::sum);
    }

    //查询单个/多个料塔一段时间内每一天用料/补料
    @Override
    public List<TowerLogReportVo> getTowerTimeLineEveryDayUseORAnd(List<Date> dateList, List<Long> towerIds) {
        ArrayList<TowerLogReportVo> timeLineEveryDayUseORAnd = new ArrayList<>();
        for (int m = 0; m < dateList.size(); m++) {
            Date oneDate = dateList.get(m);
            Integer spot = m + 1;
            TowerLogReportVo oneDayData = new TowerLogReportVo();
            oneDayData.setDayStr(forMateDateToMonthDay(oneDate));
            oneDayData.setSpot(spot);
            Long oneDay = getTowerOneDayUseORAnd(towerIds, oneDate, TowerLogStatusEnum.USE);
            oneDayData.setVariationString(ZmMathUtil.gToTString(oneDay));
            oneDayData.setVariation(oneDay);
            Long oneDayAdd = getTowerOneDayUseORAnd(towerIds, oneDate, TowerLogStatusEnum.ADD);
            oneDayData.setVariationAddString(ZmMathUtil.gToTString(oneDayAdd));
            oneDayData.setVariationAdd(oneDayAdd);
            timeLineEveryDayUseORAnd.add(oneDayData);
        }
        return timeLineEveryDayUseORAnd;
    }

    //查询单个/多个料塔近一周内每一天用料/补料
    @Override
    public List<TowerLogReportVo> getTowerNearWeekUseORAnd(List<Long> towerIds) {
        List<Date> days = ZmDateUtil.nearWeekDays(new Date());
        return getTowerTimeLineEveryDayUseORAnd(days, towerIds);
    }

    //查询单个/多个料塔近一个月内每一天用料/补料
    @Override
    public List<TowerLogReportVo> getTowerNearMonthUseORAnd(List<Long> towerIds) {
        List<Date> days = ZmDateUtil.nearMonthDays(new Date());
        return getTowerTimeLineEveryDayUseORAnd(days, towerIds);
    }

    @Override
    public UseFeedReport UseFeedReport(String dayOrMonth, Integer houseType) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        UseFeedReport.UseFeedReportBuilder builder = UseFeedReport.builder();
        if (ObjectUtil.equals(dayOrMonth, "day")) {
            List<LastSomeDayUseFeedByHouseType> last7Day = towerLogMapper.lastSomeDayUseFeedByHouseType(farmId, houseType)
                    .stream().peek(vo -> vo.setUsed(ZmMathUtil.gToTString(Long.parseLong(vo.getUsed()))))
                    .collect(Collectors.toList());
            List<LastSomeDayUseFeedByHouseType> all = towerLogMapper.allUseFeedForDay(farmId)
                    .stream().peek(vo -> vo.setUsed(ZmMathUtil.gToTString(Long.parseLong(vo.getUsed()))))
                    .collect(Collectors.toList());
            builder.last7Day(last7Day).all(all);
        } else if (ObjectUtil.equals(dayOrMonth, "month")) {
            List<LastSomeDayUseFeedByHouseType> last7Month = towerLogMapper.lastSomeMonthUseFeedByHouseType(farmId, houseType)
                    .stream().peek(vo -> vo.setUsed(ZmMathUtil.gToTString(Long.parseLong(vo.getUsed()))))
                    .collect(Collectors.toList());
            List<LastSomeDayUseFeedByHouseType> all = towerLogMapper.allUseFeedForMonth(farmId)
                    .stream().peek(vo -> vo.setUsed(ZmMathUtil.gToTString(Long.parseLong(vo.getUsed()))))
                    .collect(Collectors.toList());
            builder.last7Day(last7Month).all(all);
        }
        return builder.build();
    }

    @Override
    public List<Last5DayUseFeedByTowerVo> last5DayUseFeedByTower(Long towerId) {
        List<Last5DayUseFeedByTowerVo> vos = towerLogMapper.last5DayUseFeedByTower(towerId);
        return vos.stream().peek(vo -> vo.setUsed(ZmMathUtil.gToTString(Long.parseLong(vo.getUsed())))).collect(toList());
    }

    //查询单个料塔近3个月哪些天存在用料补料情况
    @Override
    public List<Map<Object, Object>> dateDetail(Long towerId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -3);
        Date time = calendar.getTime();

        LambdaQueryWrapper<FeedTowerLog> waper = new LambdaQueryWrapper<>();
        waper.eq(FeedTowerLog::getStatus, TowerStatus.completed);
        waper.eq(FeedTowerLog::getTowerId, towerId);
        waper.ne(FeedTowerLog::getModified, TowerLogStatusEnum.CANCEL.getType());
//        waper.ne(FeedTowerLog::getVariation, 0L);
        waper.ge(FeedTowerLog::getCreateTime, ZmDateUtil.getDateStartTime(time));

        List<FeedTowerLog> towerLogs = towerLogMapper.selectList(waper);
        Map<String, List<FeedTowerLog>> logMap = towerLogs.stream()
                .collect(Collectors.groupingBy(l -> DateUtil.format(l.getCompletedTime(), DatePattern.NORM_DATE_PATTERN)));
        List<Map<Object, Object>> data = new ArrayList<>();
        logMap.forEach((date, logs) -> {
            Set<Integer> type = logs.stream().map(FeedTowerLog::getModified).collect(Collectors.toSet());
            data.add(MapUtil.builder().put("date", date).put("type", type.size()==1?type.stream().findFirst():0).build());
        });
        return data;
    }

    //查询单个料塔近3个月哪些天存在用料补料情况（内部）
    @Override
    public List<Map<Object, Object>> dateDetailIn(Long towerId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -3);
        Date time = calendar.getTime();
        FeedTowerLog log = new FeedTowerLog();
        log.setStatus(TowerStatus.completed.name());
        log.setTowerId(towerId);
        log.setCreateTime(dateToLocalDateTime(time));

        List<FeedTowerLog> towerLogs =  towerLogMapper.selectListIn(log);
        Map<String, List<FeedTowerLog>> logMap = towerLogs.stream()
                .collect(Collectors.groupingBy(l -> DateUtil.format(l.getCompletedTime(), DatePattern.NORM_DATE_PATTERN)));
        List<Map<Object, Object>> data = new ArrayList<>();
        logMap.forEach((date, logs) -> {
            Set<Integer> type = logs.stream().map(FeedTowerLog::getModified).collect(Collectors.toSet());
            data.add(MapUtil.builder().put("date", date).put("type", type.size()==1?type.stream().findFirst():0).build());
        });
        return data;
    }

    @Override
    public FeedTowerDeviceVo getDeviceByNumber(String deviceNo) {
        FeedTowerDevice device = towerDeviceMapper.selectOne(Wrappers.lambdaQuery(FeedTowerDevice.class)
                .eq(FeedTowerDevice::getDeviceNo, deviceNo));
        if (ObjectUtil.isNotNull(device)) {
            FeedTowerDeviceVo vo = new FeedTowerDeviceVo();
            BeanUtil.copyProperties(device, vo, true);
            RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo));
            if (bucket.isExists()) {
                vo.setStatus(bucket.get().getNetworkStatus());
            }
            if (ObjectUtil.isNotNull(device.getTowerId()) && device.getTowerId() > 0) {
                FeedTower feedTower = towerMapper.selectById(device.getTowerId());
                if (ObjectUtil.isNotNull(feedTower)) {
                    vo.setTowerName(feedTower.getName());
                    vo.setIccid(feedTower.getIccid());
                }
            }
            return vo;
        } else {
            throw new BaseException("设备不存在!");
        }
    }

    @Override
    public String farmAllCardIds(Long farmId) {
        LambdaQueryWrapper<FeedTower> waperTower = new LambdaQueryWrapper<>();
        waperTower.eq(FeedTower::getPigFarmId, farmId);
        waperTower.eq(FeedTower::getDel, 0);
        waperTower.eq(FeedTower::getEnable, 0);
        waperTower.isNotNull(FeedTower::getIccid);
        waperTower.ne(FeedTower::getIccid,"");
        List<FeedTower> feedTowers = towerMapper.selectList(waperTower);
        return feedTowers.stream().map(FeedTower::getIccid).collect(Collectors.joining(","));
    }

    @Override
    public Map<String,FeedTower> farmAllTowerCard(Long farmId) {
        LambdaQueryWrapper<FeedTower> waperTower = new LambdaQueryWrapper<>();
        waperTower.eq(FeedTower::getPigFarmId, farmId);
        waperTower.eq(FeedTower::getDel, 0);
        waperTower.eq(FeedTower::getEnable, 0);
        waperTower.isNotNull(FeedTower::getIccid);
        List<FeedTower> feedTowers = towerMapper.selectList(waperTower);
        Map<String, FeedTower> collect = feedTowers.stream().collect(Collectors.toMap(FeedTower::getIccid, Function.identity()));
        return collect;
    }

    @Override
    public PageInfo<FeedTowerApplyVO> page(QueryFeedTowerApply queryFeedTowerApply) {
        PageHelper.startPage(queryFeedTowerApply.getPage(), queryFeedTowerApply.getSize());
        return PageInfo.of(feedTowerApplyMapper.page(queryFeedTowerApply));
    }

    @Async
    @Override
    public void feedApply(FeedTower tower) {
        boolean flag = tower.getResidualWeight() <= tower.getInitVolume() * tower.getWarning() / 100;
        if (flag) {
            long total = (tower.getInitVolume() - tower.getResidualWeight()) * 80 / 100;
            FeedTowerApply apply = new FeedTowerApply();
            apply.setCompanyId(tower.getCompanyId());
            apply.setPigFarmId(tower.getPigFarmId());
            apply.setTowerId(tower.getId());
            apply.setApplyCode(DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN));
            apply.setFeedType(tower.getFeedType());
            apply.setTotal(total);
            apply.setApplyStatus(1);
            feedTowerApplyMapper.insert(apply);
        }
    }

    @Override
    public PigFarmDataPage towerPageIn(QueryTower queryTower) {
        Long count = farmMapper.listInCount(queryTower.getDeviceNo(), queryTower.getName());
        List<PigFarm> farms = farmMapper.listIn(queryTower.getDeviceNo(), queryTower.getName(), (queryTower.getPage() - 1) * queryTower.getSize(), queryTower.getSize());
        farms.forEach(f -> {
            List<FeedTower> list = towerMapper.listIn(queryTower.getDeviceNo(), queryTower.getName(),f.getId());
            List<TowerVo> towerVoList = list.stream().map(t -> {
                TowerVo.TowerVoBuilder builder = TowerVo.builder();
                if (ObjectUtil.isNotEmpty(t.getDeviceNo())) {
                    RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(t.getDeviceNo()));
                    if (bucket.isExists()) {
                        DeviceStatus status = bucket.get();
                        builder.network(status.getNetworkStatus());
                        builder.deviceStatus(ObjectUtil.isEmpty(status.getDeviceStatus())?"":status.getDeviceStatus().getDesc());
                        builder.temperature(status.getTemperature());
                        builder.humidity(status.getHumidity());
                    }
                }
                FeedTowerLog towerLog = towerLogService.lastLog(null, t.getDeviceNo(), null);
                if (ObjectUtil.isNotEmpty(towerLog) && towerLog.getStatus().equals(TowerStatus.running.name())) {
                    RList<FeedTowerMsg> data = redissonClient.getList(CacheKey.Admin.tower_cache_data.key + t.getDeviceNo() + ":" + towerLog.getTaskNo());
                    if (!data.isEmpty()) {
                        builder.schedule(data.size()>=100?99:(int)(data.size()*1.25));
                    }
                }

                if (ObjectUtil.isNotEmpty(t.getDeviceNo())) {
                    Optional<FeedTowerDevice> opt = towerDeviceService.findByCache(t.getDeviceNo());
                    opt.ifPresent(device -> {
                        builder.version(opt.get().getVersion()).deviceVersion(opt.get().getVersionCode());
                    });
                }

                return builder
                        .id(t.getId())
                        .farmId(t.getPigFarmId())
                        .name(t.getName())
                        .capacity(ZmMathUtil.gToTString(t.getCapacity()))
                        .density(ZmMathUtil.gTokgDensityString(t.getDensity()))
                        .feedType(t.getFeedType())
                        .iccid(t.getIccid())
                        .residualWeight(ZmMathUtil.gToTString(t.getResidualWeight()))
                        .warning(t.getWarning())
                        .deviceNo(t.getDeviceNo())
                        .bdOptimization(t.getBdOptimization())
                        .build();
            }).collect(toList());
            f.setTowers(towerVoList);
        });

        return PigFarmDataPage.builder().count(count).list(farms).pageNum(queryTower.getPage().longValue()).pageSize(queryTower.getSize().longValue()).build();
    }

    @Override
    public PageInfo<FeedTower> towerPage(QueryTower queryTower) {
        RequestInfo employ = RequestContextUtils.getRequestInfo();
        PageHelper.startPage(queryTower.getPage(), queryTower.getSize());
        Long userId = employ.getResourceType().equals(ResourceType.JX)?employ.getUserId():null;
        List<FeedTower> list = list(queryTower.getDeviceNo(), employ.getPigFarmId(), queryTower.getName(), userId);
        list.forEach(t -> {
            if (ObjectUtil.isNotEmpty(t.getDeviceNo())) {
                RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(t.getDeviceNo()));
                if (bucket.isExists()) {
                    DeviceStatus status = bucket.get();
                    t.setNetwork(status.getNetworkStatus());
                    t.setDeviceStatus(ObjectUtil.isEmpty(status.getDeviceStatus())?"":status.getDeviceStatus().getDesc());
                    t.setTemperature(status.getTemperature());
                    t.setHumidity(status.getHumidity());
                }
            }
            FeedTowerLog towerLog = towerLogService.lastLog(null, t.getDeviceNo(), null);
            if (ObjectUtil.isNotEmpty(towerLog) && towerLog.getStatus().equals(TowerStatus.running.name())) {
                RList<FeedTowerMsg> data = redissonClient.getList(CacheKey.Admin.tower_cache_data.key + t.getDeviceNo() + ":" + towerLog.getTaskNo());
                if (!data.isEmpty()) {
                    t.setSchedule(data.size()>=100?99:(int)(data.size()*1.25));
                }
            }

            if (ObjectUtil.isNotEmpty(t.getDeviceNo())) {
                Optional<FeedTowerDevice> opt = towerDeviceService.findByCache(t.getDeviceNo());
                opt.ifPresent(device -> {
                    t.setVersion(opt.get().getVersion());
                    t.setDeviceVersion(opt.get().getVersionCode());
                });
            }
            t.setFarmId(t.getPigFarmId());
            t.setCapacityView(ZmMathUtil.gToTString(t.getCapacity()));
            t.setDensityView(ZmMathUtil.gTokgDensityString(t.getDensity()));
            t.setResidualWeightView(ZmMathUtil.gToTString(t.getResidualWeight()));
        });
        return PageInfo.of(list);
    }

    public List<TowerVo> wrapperTowerOnlineMessage(List<FeedTower> towerList){
        return towerList.stream().map(t -> {
            //计算余量占比
            Integer residualPercent = null;
            if (t.getInitVolume() != null && t.getInitVolume() > 0 && t.getResidualVolume() != null) {
                residualPercent = ZmMathUtil.getPercent(t.getResidualVolume(), t.getInitVolume());
            }
            //料塔状态标签
            TowerTabEnum tab;
            if (ObjectUtil.isEmpty(t.getDeviceNo())) {
                tab = TowerTabEnum.UnBind;
            } else if (ObjectUtil.isEmpty(t.getHouses())) {
                tab = TowerTabEnum.UnAssociation;
            } else if (ObjectUtil.notEqual(t.getInit(), 1)) {
                tab = TowerTabEnum.UnCalibration;
            } else {
                tab = TowerTabEnum.Normal;
            }

            TowerVo.TowerVoBuilder builder = TowerVo.builder();
            Integer schedule = null;

            //料塔设备信息
            if (ObjectUtil.isNotEmpty(t.getDeviceNo())) {
                FeedTowerLog lastLog = towerLogService.lastLog(t.getId(), t.getDeviceNo(), null);
                RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(t.getDeviceNo()));
                if (bucket.isExists()) {
                    DeviceStatus status = bucket.get();
                    builder.network(ListUtil.of("在线", "弱", "中", "强").contains(status.getNetworkStatus()) ? "在线" : "离线");
                    builder.deviceStatus(ObjectUtil.isEmpty(status.getDeviceStatus())?"":status.getDeviceStatus().getDesc());
                    builder.deviceErrorInfo(status.getDeviceErrorInfo());
                }
                if (ObjectUtil.isNotEmpty(lastLog)) {

                    if (ObjectUtil.isNotNull(lastLog.getInitId())) {
                        DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(lastLog.getInitId());
                        if (DeviceInitCheckStatusEnum.CHECKING.getStatus() == deviceInitCheck.getCheckStatus()){
                            tab = TowerTabEnum.CalibrationIn;
                        }

                    }else {
                        if (Stream.of(TowerStatus.starting.name(), TowerStatus.running.name()).anyMatch(s -> ObjectUtil.equals(s, lastLog.getStatus()))) {
                            if (ObjectUtil.equals(lastLog.getStartMode(), MeasureModeEnum.Init.getDesc())) {
                                tab = TowerTabEnum.CalibrationIn;
                            } else {
                                tab = TowerTabEnum.MeasureIn;
                            }
                        }
                    }

                    schedule = schedule(t.getDeviceNo(), lastLog.getTaskNo(),lastLog);
                }
            }
            String houses = associationAnalysis(t.getHouses());

            //获取料塔历史记录
            List<FeedTowerLog> logs = getFeedTowerDataLogs(t);

            return builder
                    .id(t.getId())
                    .farmId(t.getPigFarmId())
                    .name(t.getName())
                    .residualWeight(ZmMathUtil.gToTString(t.getResidualWeight()))
                    .residualPercentage(residualPercent)
                    .deviceNo(t.getDeviceNo())
                    .init(t.getInit())
                    .tab(tab)
                    .feedType(t.getFeedType())
                    .density(ZmMathUtil.gTokgDensityString(t.getDensity()))
                    .houseIds(t.getHouses())
                    .houses(houses)
                    .schedule(schedule)
                    .warning(t.getWarning())
                    .dataLogs(logs)
                    .build();
        }).collect(toList());
    }

    /**
     * 获取料塔历史记录
     * @param t
     * @return
     */
    public List<FeedTowerLog> getFeedTowerDataLogs(FeedTower t) {
        RList<FeedTowerLog> list = redissonClient.getList(CacheKey.Admin.tower_data.key.concat(t.getId().toString()));
        List<FeedTowerLog> logs = new ArrayList<>();
        if (ObjectUtil.isNotNull(list) && list.size() <= 5){
            list.forEach(o->{
                FeedTowerLog log = new FeedTowerLog();
                BeanUtil.copyProperties(o,log);
                log.setData(null);
                logs.add(log);
            });
        }else {
            for (int i = 0; i < 5; i++) {
                FeedTowerLog log = new FeedTowerLog();
                BeanUtil.copyProperties(list.get(i),log);
                log.setData(null);
                logs.add(log);
            }
        }
        return logs;
    }

    @Override
    public FeedTowerLogVo points(Long towerId) {
        //根据料塔id查询最近一次点云数据
        RBucket<FeedTowerLog> towerPointCache = redissonClient.getBucket(CacheKey.Admin.tower_data.key.concat(towerId.toString()));
        FeedTowerLog feedTowerLog = towerPointCache.get();
        FeedTowerLogVo feedTowerLogVo = new FeedTowerLogVo();

        if(ObjectUtil.isNotEmpty(feedTowerLog)){
            String data = feedTowerLog.getData();
            if(ObjectUtil.isEmpty(data)){
                throw new BaseException("暂无点云数据!");
            }
            //最新的数据
            JSONObject obj = JSONUtil.parseObj(data);
            List<Double[]> lines = new ArrayList<>();
            obj.forEach((levelAngleStr, vertical) -> {
                int levelAngle = Integer.parseInt(levelAngleStr);
                JSONObject verObj = (JSONObject) vertical;
                verObj.forEach((angStr, disStr) -> {
                    double ang = Double.parseDouble(angStr) / 100;
                    int dis = (Integer) disStr;
                    if (dis < 10000) {
                        double z = Math.cos(Math.toRadians(ang)) * dis;
                        double temp = Math.sin(Math.toRadians(ang)) * dis;
                        double x = Math.cos(Math.toRadians(levelAngle)) * temp;
                        double y = Math.sin(Math.toRadians(levelAngle)) * temp;
                        Double[] onePoint = new Double[3];
                        onePoint[0] = x;
                        onePoint[1] = y;
                        onePoint[2] = z;
                        lines.add(onePoint);
                    }
                });
            });
            BeanUtil.copyProperties(feedTowerLog,feedTowerLogVo,"data");
            feedTowerLogVo.setPoints(lines);
            //计算准确率(百分比保留两位小数再乘以10000,使用的时候除以100就可以了)
            int percent = ZmMathUtil.calculateAccuracy(feedTowerLog.getVolumeBase(), TOWER_VOLUME_STANDARD);
            feedTowerLogVo.setRightPercent(percent);
            feedTowerLogVo.setStandardVolume(TOWER_VOLUME_STANDARD);
            if(percent>=PASS_PERCENT && percent<=10000) {
                feedTowerLogVo.setPass(DeviceCheckStatusEnum.PASS.getStatus());
            }else{
                feedTowerLogVo.setPass(DeviceCheckStatusEnum.NOT_PASS.getStatus());
            }
            FeedTower feedTower = towerMapper.selectById(feedTowerLog.getTowerId());
            if(ObjectUtil.isNotEmpty(feedTower)){
                feedTowerLogVo.setTowerName(feedTower.getName());
            }
            return feedTowerLogVo;
        }else{
            throw new BaseException("暂无点云缓存数据!请先测量!");
        }
    }

    @Override
    public FeedTowerLogVo pointsNew(Long towerId,Long logId) {
        //根据料塔id查询最近一次点云数据
        RList<FeedTowerLog> towerPointCache = redissonClient.getList(CacheKey.Admin.tower_data.key.concat(towerId.toString()));
        if (ObjectUtil.isNotNull(towerPointCache) && towerPointCache.size() > 0) {
            if (ObjectUtil.isNotNull(logId) && logId > 0) {
                for (FeedTowerLog feedTowerLog : towerPointCache) {
                    if (logId.equals(feedTowerLog.getId())){
                        return getFeedTowerLogVo(feedTowerLog);
                    }
                }
            }else {
                List<FeedTowerLog> collect = towerPointCache.stream().sorted(Comparator.comparing(FeedTowerLog::getCompletedTime).reversed()).collect(toList());
                FeedTowerLog feedTowerLog = collect.get(0);
                return getFeedTowerLogVo(feedTowerLog);
            }
        }
        throw new BaseException("暂无点云缓存数据!请先测量!");
    }

    /**
     * 获取点云
     * @param feedTowerLog
     * @return
     */
    public FeedTowerLogVo getFeedTowerLogVo(FeedTowerLog feedTowerLog) {
        FeedTowerLogVo feedTowerLogVo = new FeedTowerLogVo();
        String data = feedTowerLog.getData();
        if (ObjectUtil.isEmpty(data)) {
            throw new BaseException("暂无点云数据!");
        }
        //最新的数据
        JSONObject obj = JSONUtil.parseObj(data);
        List<Double[]> lines = new ArrayList<>();
        obj.forEach((levelAngleStr, vertical) -> {
            int levelAngle = Integer.parseInt(levelAngleStr);
            JSONObject verObj = (JSONObject) vertical;
            verObj.forEach((angStr, disStr) -> {
                double ang = Double.parseDouble(angStr) / 100;
                int dis = (Integer) disStr;
                if (dis < 10000) {
                    double z = Math.cos(Math.toRadians(ang)) * dis;
                    double temp = Math.sin(Math.toRadians(ang)) * dis;
                    double x = Math.cos(Math.toRadians(levelAngle)) * temp;
                    double y = Math.sin(Math.toRadians(levelAngle)) * temp;
                    Double[] onePoint = new Double[3];
                    onePoint[0] = x;
                    onePoint[1] = y;
                    onePoint[2] = z;
                    lines.add(onePoint);
                }
            });
        });
        BeanUtil.copyProperties(feedTowerLog, feedTowerLogVo, "data");
        feedTowerLogVo.setPoints(lines);
        //计算准确率(百分比保留两位小数再乘以10000,使用的时候除以100就可以了)
        int percent = ZmMathUtil.calculateAccuracy(ObjectUtil.isNotNull(feedTowerLog.getVolumeBase()) ? feedTowerLog.getVolumeBase() : 0, TOWER_VOLUME_STANDARD);
        feedTowerLogVo.setRightPercent(percent);
        feedTowerLogVo.setStandardVolume(TOWER_VOLUME_STANDARD);
        if (percent >= PASS_PERCENT && percent <= 10000) {
            feedTowerLogVo.setPass(DeviceCheckStatusEnum.PASS.getStatus());
        } else {
            feedTowerLogVo.setPass(DeviceCheckStatusEnum.NOT_PASS.getStatus());
        }
        FeedTower feedTower = towerMapper.selectById(feedTowerLog.getTowerId());
        if (ObjectUtil.isNotEmpty(feedTower)) {
            feedTowerLogVo.setTowerName(feedTower.getName());
        }
        return feedTowerLogVo;
    }

    @Override
    public void cachePoints(Long towerId) {
        LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTowerLog::getStatus,TowerStatus.completed.name());
        wrapper.orderByDesc(FeedTowerLog::getId);
        List<FeedTowerLog> resultList = towerLogMapper.selectList(wrapper.last("limit 1"));
        if (!resultList.isEmpty()) {
            FeedTowerLog newestRecord = resultList.get(0);
            LambdaQueryWrapper<FeedTowerLogSlave> feedTowerLogSlaveLambdaQueryWrapper = new LambdaQueryWrapper<>();
            feedTowerLogSlaveLambdaQueryWrapper.eq(FeedTowerLogSlave::getLogId,newestRecord.getId());
            FeedTowerLogSlave feedTowerLogSlave = towerLogSlaveMapper.selectOne(feedTowerLogSlaveLambdaQueryWrapper);
            if(ObjectUtil.isNotEmpty(feedTowerLogSlave)){
                newestRecord.setData(feedTowerLogSlave.getData());
                RBucket<FeedTowerLog> towerPointCache = redissonClient.getBucket(CacheKey.Admin.tower_data.key.concat(towerId.toString()));
                towerPointCache.set(newestRecord);
            }else{
                //最新的测量记录没得点云数据
                throw new BaseException("最新的测量记录没得点云数据!");
            }
        }
    }

    @Override
    public CalResponse volume(Long towerLogId) {
        towerLogId = 159044L;
        FeedTowerLog feedTowerLog = towerLogMapper.selectById(towerLogId);
        String dataJson = feedTowerLog.getData();
        JSONObject param = new JSONObject();
        param.putOpt("pointcloud", dataJson);
        param.putOpt("type", "料塔");
        param.putOpt("compensatePercent", 1.046);
        param.putOpt("fullVolume", 25616230);
        param.putOpt("standardDensity", 643500);
        String resStr = HttpUtil.post("http://122.9.147.118:8848/volume", param.toString());
        CalResponse calResponse = JSONUtil.toBean(resStr, CalResponse.class);
        if (ObjectUtil.isNotEmpty(calResponse) && calResponse.getSuccess()!=null && calResponse.getSuccess()) {
            log.info("算法调用响应: "+calResponse.toString());
            return calResponse;
        } else {
            System.out.println(calResponse.getNote());
            throw new BaseException(calResponse.getNote());
        }
    }

    @Override
    public void startPy(Long logId,Integer thread) {
        FeedTowerLog feedTowerLog = towerLogMapper.selectById(logId);
        StopWatch stopWatch = new StopWatch();
        String data = feedTowerLog.getData();
        stopWatch.start("task"+ IdUtil.fastSimpleUUID());
        CountDownLatch latch = new CountDownLatch(thread);
        for(int i =0;i<thread;i++){
            // 创建一个新线程来读取Python脚本的输出
            taskExecutor.execute(() -> {
                //单位精度：cm³
                Double volume = null;
                String temp = IdUtil.fastSimpleUUID();
                try {
                    //调用我们算法
                    if (SystemUtil.getOsInfo().isWindows()) {
                        //将map转为json然后序列化到临时文件
                        String tempFile = pythonProperties.getWindowsFileBasePath() +temp+".json";
                        String imageTempFile = pythonProperties.getWindowsFileBasePath()+temp+".jpg";
                        FileUtil.writeString(JSONUtil.parseObj(data).toString(), FileUtil.newFile(tempFile), Charset.defaultCharset());
                        volume = CallPythonScriptUtil.calculateVolume(pythonProperties.getWindows(),
                                pythonProperties.getScriptPathW(),pythonProperties.getMethod(),imageTempFile,tempFile, taskExecutor);
                    } else {
                        //将map转为json然后序列化到临时文件
                        String tempFile = pythonProperties.getLinuxFileBasePath() +temp+".json";
                        String imageTempFile = pythonProperties.getLinuxFileBasePath() +temp+".jpg";
                        FileUtil.writeString(JSONUtil.parseObj(data).toString(), FileUtil.newFile(tempFile), Charset.defaultCharset());
                        volume = CallPythonScriptUtil.calculateVolume(pythonProperties.getLinux(),
                                pythonProperties.getScriptPathL(),pythonProperties.getMethod(),imageTempFile,tempFile, taskExecutor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("算不出来!");
                }finally {
                    latch.countDown(); // 子线程执行完毕后，释放锁
                    //删除临时文件
                    if (SystemUtil.getOsInfo().isWindows()) {
                        String tempFile = pythonProperties.getWindowsFileBasePath() +temp+".json";
                        deleteFileIfExists(tempFile);
                    } else {
                        String tempFile = pythonProperties.getLinuxFileBasePath() +temp+".json";
                        deleteFileIfExists(tempFile);
                    }
                }
            });
        }
        try {
            latch.await(); // 等待子线程执行完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        log.info("总共执行了" + stopWatch.getTotalTimeMillis());
    }

    public static void deleteFileIfExists(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public FeedTowerGrowthAbility growthAbility(GrowthAbilityDto dto) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        //累计用料量
        List<FeedTowerLog> logs = towerLogMapper.selectList(Wrappers.lambdaQuery(FeedTowerLog.class)
                .eq(FeedTowerLog::getTowerId, dto.getTowerId())
                .eq(FeedTowerLog::getStatus, TowerStatus.completed)
                .eq(FeedTowerLog::getModified, TowerLogStatusEnum.USE.getType())
                .between(FeedTowerLog::getCreateTime, DateUtil.parseDate(dto.getBeginDate()), DateUtil.endOfDay(DateUtil.parseDate(dto.getEndDate()))));
        Long totalUsed = logs.stream().mapToLong(FeedTowerLog::getVariation).sum();
        //当天用料量
        List<FeedTowerLog> dayLogs = towerLogMapper.selectList(Wrappers.lambdaQuery(FeedTowerLog.class)
                .eq(FeedTowerLog::getTowerId, dto.getTowerId())
                .eq(FeedTowerLog::getStatus, TowerStatus.completed)
                .eq(FeedTowerLog::getModified, TowerLogStatusEnum.USE.getType())
                .between(FeedTowerLog::getCreateTime, DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date())));
        Long dayUsed = dayLogs.stream().mapToLong(FeedTowerLog::getVariation).sum();

        //料肉比
        dto.setFeedEfficiency(new BigDecimal(totalUsed)
                .divide(new BigDecimal(1000), RoundingMode.HALF_UP)
                .divide(new BigDecimal(dto.getAmount()), RoundingMode.HALF_UP)
                .divide(new BigDecimal(dto.getAvgWeight()), 2, RoundingMode.HALF_UP).toString());
        //日均用料量
        dto.setAvgDayUsed(new BigDecimal(dayUsed)
                .divide(new BigDecimal(1000), RoundingMode.HALF_UP)
                .divide(new BigDecimal(dto.getAmount()), 2, RoundingMode.HALF_UP).toString());

        FeedTowerGrowthAbility ability = FeedTowerGrowthAbility.builder().companyId(info.getCompanyId())
                .pigFarmId(info.getPigFarmId())
                .towerId(dto.getTowerId())
                .beginDate(LocalDateTimeUtil.parseDate(dto.getBeginDate()))
                .endDate(LocalDateTimeUtil.parseDate(dto.getEndDate()))
                .amount(dto.getAmount())
                .avgWeight(dto.getAvgWeight())
                .feedEfficiency(dto.getFeedEfficiency())
                .avgDayUsed(dto.getAvgDayUsed())
                .build();

        feedTowerGrowthAbilityMapper.insert(ability);

        return ability;
    }

    @Override
    public PageInfo<FeedTowerGrowthAbility> growthAbilityRecord(GrowthAbilityRecordQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        List<FeedTowerGrowthAbility> abilities = feedTowerGrowthAbilityMapper.selectList(Wrappers.lambdaQuery(FeedTowerGrowthAbility.class)
                .orderByDesc(FeedTowerGrowthAbility::getCreateTime));
        return PageInfo.of(abilities);
    }

    @Override
    public List<TowerMeasureBatchInfoVo> batchMeasureInfo() {
        List<TowerVo> towerVoList = listVo();
        ArrayList<TowerMeasureBatchInfoVo> infoList = new ArrayList<>();
        towerVoList.forEach(oneTower->{
            //查询最近一次的检测记录
            if(ObjectUtil.isNotEmpty(oneTower.getDeviceNo())){
                TowerMeasureInfoVo towerMeasureInfoVo = measureInfo(oneTower.getId(), MeasureModeEnum.Manual.name());
                TowerMeasureBatchInfoVo towerMeasureBatchInfoVo = new TowerMeasureBatchInfoVo();
                BeanUtil.copyProperties(towerMeasureInfoVo,towerMeasureBatchInfoVo);
                Optional<FeedTowerDevice> opt = towerDeviceService.findByCache(oneTower.getDeviceNo());
                opt.ifPresent(device -> {
                    towerMeasureBatchInfoVo.setSn(device.getSn());
                    towerMeasureBatchInfoVo.setName(oneTower.getName());
                    towerMeasureBatchInfoVo.setNetwork(oneTower.getNetwork());
                    towerMeasureBatchInfoVo.setDeviceStatus(oneTower.getDeviceStatus());
                    towerMeasureBatchInfoVo.setDeviceErrorInfo(oneTower.getDeviceErrorInfo());
                    if(MeasureModeEnum.Manual.getDesc().equals(towerMeasureBatchInfoVo.getStartupMode())){
                        infoList.add(towerMeasureBatchInfoVo);
                    }
                });
            }
        });
        return infoList;
    }

    /**
     *清理当前猪场下未设置 吨位 并且未校准的料塔
     */
    @Override
    public void resetMeasureInfo() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        List<FeedTower> towerList = list(null, info.getPigFarmId(), null, null);
        List<Long> filteredIds = towerList.stream()
                .filter(d ->
                        ObjectUtil.isNotEmpty(d.getDeviceNo()) &&
                                (ObjectUtil.isEmpty(d.getCapacity()) || d.getCapacity() == 0) &&
                                d.getInit() == 0)
                .map(FeedTower::getId) // 将满足条件的 towerVo 转化为 id
                .collect(Collectors.toList()); // 收集满足条件的 id
        LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FeedTowerLog::getTowerId,filteredIds);
        towerLogMapper.delete(wrapper);
    }

    @Override
    public void measureStopAverage(String deviceNo, String taskNo,Long initId) {
        if (StringUtils.isNotBlank(deviceNo) && StringUtils.isNotBlank(taskNo) && ObjectUtil.isNotNull(initId)){
            initStopOne(deviceNo, taskNo, initId);
        }else if (StringUtils.isBlank(deviceNo) && StringUtils.isBlank(taskNo) && ObjectUtil.isNull(initId)){
            LambdaQueryWrapper<DeviceInitCheck> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeviceInitCheck::getCheckStatus, DeviceInitCheckStatusEnum.CHECKING.getStatus());
            List<DeviceInitCheck> list = deviceInitCheckMapper.selectList(wrapper);
            list.forEach(li -> {
                String no = towerLogMapper.selectTaskNoByIdInit(li.getId());
                if (StringUtils.isNotBlank(no)) {
                    initStopOne(li.getDeviceNum(),no, li.getId());
                }else {
                    throw  new BaseException("编码为:"+li.getDeviceNum()+"的设备无taskNo,停止失败！");
                }
            });
        }
    }

    @Override
    public CalResponse calForSelfDevNew(FeedTower tower, String pointCloud, String type) {
        //单位精度：cm³
        JSONObject param = new JSONObject();
        param.putOpt("pointcloud", pointCloud);
        param.putOpt("type", type);

        if("方箱".equals(type)){
            param.putOpt("compensatePercent", towerProperty.getFIX_fx_compensatePercent());
        }else{
            if(towerProperty.getSWITCH_TowerVolumeCalByDeviceCheckPercent()){
                //查询设备信息里面的补偿百分比
                Optional<FeedTowerDevice> deviceInfo = towerDeviceService.findByCache(tower.getDeviceNo());
                if(deviceInfo.isPresent() && ObjectUtil.isNotEmpty(deviceInfo.get().getCompensatePercent())){
                    param.putOpt("compensatePercent", deviceInfo.get().getCompensatePercent());
                }else{
                    log.warn("设备{}不存在或者设备没有质检补偿数据",tower.getDeviceNo());
                    param.putOpt("compensatePercent", towerProperty.getFIX_tower_compensatePercent());
                }
            }else{
                param.putOpt("compensatePercent", towerProperty.getFIX_tower_compensatePercent());
            }
        }
        param.putOpt("fullVolume", ObjectUtil.isEmpty(tower.getInitVolume())?0:tower.getInitVolume());
        param.putOpt("standardDensity", ObjectUtil.isEmpty(tower.getDensity())?0:tower.getDensity());
        String resStr = HttpUtil.post("http://122.9.147.118:8848/volume", param.toString());
        CalResponse calResponse = JSONUtil.toBean(resStr, CalResponse.class);
        if (ObjectUtil.isNotEmpty(calResponse) && calResponse.getSuccess()!=null && calResponse.getSuccess()) {
            log.info("算法调用响应: "+calResponse.toString());
            Long volumeResult = calResponse.getVolume();
            Long feedResult = calResponse.getFeed_volume();
            calResponse.setVolume(volumeResult);
            calResponse.setFeed_volume(feedResult);
            //是否使用测算饲料体积作为重量结果
            if(towerProperty.getSWITCH_PredictionDestinyForWeight()){
                calResponse.setWeight(calResponse.getWeightPrediction());
            }
            //如果没有填写密度,重量始终为0
            if(towerProperty.getSWITCH_NoDestinyWeight0()){
                if(ObjectUtil.isEmpty(tower.getDensity()) || tower.getDensity()==0){
                    calResponse.setWeight(0L);
                }
            }

            return calResponse;
        } else {
            if(ObjectUtil.isNotEmpty(calResponse.getNote())){
                throw new BaseException(calResponse.getNote());
            }else{
                throw new BaseException("算法调用失败,错误的请求参数!");
            }
        }
    }

    /**
     * 抓取当前用户所有的料塔数据-总览
     * @return
     */
    @Override
    public TowerOverViewVo capView() {
        Set<Long> farmIds = getLoginUserFarmIds();//获取当前登录用户的所有猪场id
        //farmIds = Arrays.stream(bu.get().getManageIds().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toSet());
        List<FarmDetail> farms = farmMapper.selectFarmsIds(farmIds);
        //List<FarmDetail> farms = sysUserFarmMapper.selectFarmIdsByUserId(info.getUserId());//当前用户所有猪场----数据库获取
        List<FeedTower> onCount = new ArrayList<>();
        farms.forEach(farm -> {
            Long sumCount = towerMapper.getCountByFarmId(farm.getFarmId());
            List<FeedTower> ybddTowers = towerMapper.getybdByFarmId(farm.getFarmId());//单个猪场已绑定设备
            List<FeedTower> wbdTowers = towerMapper.getywbdByFarmId(farm.getFarmId());//单个猪场未绑定设备
            farm.setSumFarmCount(sumCount - wbdTowers.size());//单个猪场设备总数
            ybddTowers.forEach(feedTower -> {
                RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(feedTower.getDeviceNo()));
                if (bucket.isExists()) {
                    DeviceStatus status = bucket.get();
                    if (ListUtil.of("在线", "弱", "中", "强").contains(status.getNetworkStatus())){
                        onCount.add(feedTower);
                    }
                }
            });
            farm.setOnFarmCount((long) onCount.size());
            farm.setUnBindFarmCount(ObjectUtil.isNotNull(wbdTowers) ? (long) wbdTowers.size() : 0L);
            onCount.clear();
            //耗料
            Long consume = towerLogMapper.getMaterByFarmId(farm.getFarmId());
            if (ObjectUtil.isNotNull(consume) && consume > 0) {
                farm.setExpend(BigDecimal.valueOf(consume).divide(BigDecimal.valueOf(1000000),2,RoundingMode.HALF_UP));
            }else {
                farm.setExpend(BigDecimal.ZERO);
            }
        });
        //汇总数据
        long on = farms.stream().mapToLong(FarmDetail::getOnFarmCount).sum();
        long sum = farms.stream().mapToLong(FarmDetail::getSumFarmCount).sum();
        long unbind = farms.stream().mapToLong(FarmDetail::getUnBindFarmCount).sum();
        return TowerOverViewVo.builder()
                .time(LocalDateTime.now())
                .farmDetailList(farms.stream().sorted(Comparator.comparing(FarmDetail::getSumFarmCount).reversed()).collect(toList()))
                .onCount(on)
                .offCount(sum-on)
                .noBondCount(unbind)
                .sumCount(sum+unbind)
                .build();
    }

    /**
     * 获取当前登录用户的所有猪场id
     * @return
     */
    public Set<Long> getLoginUserFarmIds() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        RBucket<UserLoginResultVO> bu = redissonClient.getBucket(CacheKey.Admin.TOKEN.key.concat(info.getToken()));
        Set<Long> farmIds = new HashSet<>();
        if (info.getResourceType().equals(ResourceType.JX)) {
            farmIds = searchByJx(bu.get());
        } else if (info.getResourceType().equals(ResourceType.YHY)) {
            farmIds = searchByYhy(bu.get());
        }
        return farmIds;
    }

    private Set<Long> searchByJx(UserLoginResultVO loginVo) {
        HttpResponse response = HttpUtil.createRequest(Method.POST, config.getConfig().getJxAppBaseUrl().concat(getZCList))
                .header("token", loginVo.getToken())
                .form(JSONUtil.createObj().set("manager_zc", loginVo.getManageIds()).set("start", 1).set("rcount", 999))
                .execute();
        Set<Long> farmIds = new HashSet<>();
        JSONArray arr = (JSONArray)resultSelect(response.body());
        for (Object o : arr) {
            JSONObject js = (JSONObject) o;
            farmIds.add(Long.valueOf(js.getStr("z_zc_id")));
        }

        return farmIds;
    }

    private Set<Long> searchByYhy(UserLoginResultVO loginVo) {
        LambdaQueryWrapper<PigFarm> wrapper = Wrappers.lambdaQuery(PigFarm.class);
        if (loginVo.getRoleTypeEnum() != UserRoleTypeEnum.SUPER_ADMIN) {
            wrapper.eq(PigFarm::getCompanyId, loginVo.getCompanyId());
        }
        if (ObjectUtil.isNotEmpty(loginVo.getManageIds())) {
            Set<Long> fids = Arrays.stream(loginVo.getManageIds().split(",")).map(Long::parseLong).collect(Collectors.toSet());
            wrapper.in(PigFarm::getId, fids);
        }
        List<PigFarm> fs = farmMapper.selectList(wrapper.eq(PigFarm::getJx, 0));
        return fs.stream().map(PigFarm::getId).collect(Collectors.toSet());
    }

    public Object resultSelect(String body) {
        log.info("调试：[{}]", body);
        if (ObjectUtil.isNotEmpty(body)) {
            JSONObject obj;
            try {
                obj = JSONUtil.parseObj(body);
            } catch (Exception e) {
                log.info(String.format("巨星接口异常：%s", body), e);
                throw new BaseException("巨星接口异常！请联系系统管理员");
            }
            if (body.contains("登录失效")) {
                throw new UnauthorizedException(401, "请先登录");
            }
            if (Boolean.FALSE.equals(obj.getBool("flag"))) {
                throw new BaseException(body);
            }
            return obj.get("info");
        }
        throw new BaseException("巨星接口异常！请联系系统管理员");
    }

    /**
     * 强制解绑
     * @param deviceNo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forceUnbind(String deviceNo,Integer isReset) {
        //料塔设备是否在进行质检
        QueryDeviceCheck queryDeviceCheck = new QueryDeviceCheck();
        queryDeviceCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
        queryDeviceCheck.setDeviceNo(deviceNo);
        List<DeviceQualityCheck> list = deviceQualityCheckMapper.list(queryDeviceCheck);
        if(ObjectUtil.isNotEmpty(list)){
            DeviceQualityCheck deviceQualityCheck = list.get(0);
            deviceQualityCheck.setRemark("强制解绑！");
            queryDeviceCheck.setHandle(DeviceCheckHandleEnum.PERSON.getStatus());
            queryDeviceCheck.setPass(DeviceCheckStatusEnum.CANCEL.getStatus());
            deviceQualityCheckMapper.updateById(deviceQualityCheck);
            //throw new BaseException("设备正在质检!无法解绑!");
        }

        //料塔设备是否在进行老化
        QueryAgingCheck queryAgingCheck = new QueryAgingCheck();
        queryAgingCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
        queryAgingCheck.setDeviceNo(deviceNo);
        List<DeviceAgingCheck> aList = deviceAgingCheckMapper.list(queryAgingCheck);
        if(ObjectUtil.isNotEmpty(aList)){
            DeviceAgingCheck deviceAgingCheck = aList.get(0);
            deviceAgingCheck.setHandle(DeviceCheckHandleEnum.PERSON.getStatus());
            deviceAgingCheck.setPass(DeviceCheckStatusEnum.CANCEL.getStatus());
            deviceAgingCheck.setRemark("强制解绑！");
            deviceAgingCheckMapper.updateById(deviceAgingCheck);
            //throw new BaseException("设备正在老化!无法解绑!");
        }

        //料塔设备是否在进行校准
        DeviceInitCheck deviceInitCheck = new DeviceInitCheck();
        deviceInitCheck.setCheckStatus(DeviceInitCheckStatusEnum.CHECKING.getStatus());
        deviceInitCheck.setDeviceNum(deviceNo);
        List<DeviceInitCheck> iList = deviceInitCheckMapper.list(deviceInitCheck);
        if(ObjectUtil.isNotEmpty(iList)){
            DeviceInitCheck deviceInitCheck1 = iList.get(0);
            //deviceInitCheck1.setHandle(DeviceCheckHandleEnum.PERSON.getStatus());
            deviceInitCheck1.setCheckStatus(DeviceInitCheckStatusEnum.ERR.getStatus());
            deviceInitCheck1.setRemark("强制解绑！");
            deviceInitCheckMapper.updateById(deviceInitCheck1);
            //throw new BaseException("设备正在校准!无法解绑!");
        }
        towerMapper.unBindFeedTower(deviceNo,isReset);
        towerDeviceMapper.unBindFeedTower(deviceNo);

        redissonClient.getTopic(RedisTopicEnum.tower_unsubscribe_topic.name(), new SerializationCodec()).publish(deviceNo);
        towerDeviceService.delByCache(deviceNo);
        delTowerConfigCache(deviceNo);
        towerQrtzService.deleteByDeviceNo(deviceNo);
    }

    /**
     * 查询料塔异常信息
     * @param startTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public zmuData exceptionView(Date startTime, Date endTime, Integer pageSize, Integer pageNum) {
        //Set<Long> farmIds = getLoginUserFarmIds();//获取当前登录用户的所有猪场id
        Date time = null;
        if (ObjectUtil.isNotNull(endTime)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            time = calendar.getTime();
        }
        Long count = towerMapper.exceptionViewConut(startTime, ObjectUtil.isNotNull(time) ? time : null);
        //PageHelper.startPage(pageNum,pageSize);
        List<TowerExceptionViewVo> towerExceptionViewVos = towerMapper.exceptionView(startTime, ObjectUtil.isNotNull(time) ? time : null,(pageNum-1)*pageSize,pageSize);
        return zmuData.builder().count(count).list(towerExceptionViewVos).pageNum(pageNum.longValue()).pageSize(pageSize.longValue()).pages((count/pageSize) + 1).build();
        //return PageInfo.of(towerExceptionViewVos);
    }

    @Override
    public List<TowerFarmLogVo> towerFarmLogList(QueryTowerFarmLog queryTowerFarmLog) {
        List<TowerFarmLogVo> vos =
                farmMapper.selectFarmsByName(StringUtils.isNotBlank(queryTowerFarmLog.getFarmName())?queryTowerFarmLog.getFarmName():null);
        for (TowerFarmLogVo vo : vos) {
            List<TowerDetailLogVo> detailLogVos =towerMapper.selectTowerDetails(queryTowerFarmLog.getDeviceNo(),vo.getFarmId());
            vo.setTowers(detailLogVos);
        }
        return vos;
    }

    @Override
    public List<FeedTowerLog> towerFarmLogDetailList(QueryTowerFarmLog queryTowerFarmLog) {
        String query;
        if ("0".equals(queryTowerFarmLog.getStatus())){
            query = " status not in ('running', 'completed', 'starting') ";
        }else {
            query = " status = 'completed' ";
        }
        return towerLogMapper.towerFarmLogDetailList(queryTowerFarmLog.getTowerId(),
                query,
                queryTowerFarmLog.getStartTime(),
                queryTowerFarmLog.getEndTime());
    }

    @Override
    public void updateWeightAndVolume(Long towerId, Long weight, Long volume) {
        FeedTower tower = FeedTower.builder().residualWeight(weight).residualVolume(volume).id(towerId).build();
        towerMapper.updateTowerById(tower);
    }

    @Override
    public void updateFeedTowerLogDetail(FeedTowerLog feedTowerLog) {
        towerLogMapper.updateLogById(feedTowerLog);
    }

    @Override
    @Transactional
    public void addFeedTowerLogDetail(FeedTowerLogDto feedTowerLog) {
        FeedTower feedTower = towerMapper.selectTowerById(feedTowerLog.getTowerId());
        LocalDateTime time = getCreateTime(feedTowerLog);
        setTowerLogs(feedTowerLog, feedTower, time);
        FeedTowerLog log = new FeedTowerLog();
        BeanUtil.copyProperties(feedTowerLog,log);
        log.setCompletedTime(stringToLocalDateTime(feedTowerLog.getCompletedTime(),"yyyy-MM-dd HH:mm:ss"));
        towerLogMapper.insert(log);
    }


    @Override
    public List<TowerFarmLogVo> towerExportFind(QueryTowerFarmLog queryTowerFarmLog) {
        List<TowerFarmLogVo> vos =
                farmMapper.selectFarmsByName(StringUtils.isNotBlank(queryTowerFarmLog.getFarmName())?queryTowerFarmLog.getFarmName():null);
        for (TowerFarmLogVo vo : vos) {
            List<TowerDetailLogVo> detailLogVos =towerMapper.selectTowerDetailsByDate(queryTowerFarmLog.getDeviceNo(),vo.getFarmId(),queryTowerFarmLog.getStartTime(),queryTowerFarmLog.getEndTime());
            vo.setTowers(detailLogVos);
        }
        return vos;
    }

    private volatile Long i = 0L;

    @Override
    public List<TowerLogExportVo> logeExportOne(QueryTowerFarmLog queryTowerFarmLog) {
        List<TowerLogExportVo> vos = towerLogMapper.selectExportList(queryTowerFarmLog);
        vos.forEach(o->{
            taskExecutor.execute(()->{
                String data = towerLogSlaveMapper.selectData(o.getLogId());
                FeedTower tower = FeedTower.builder()
                        .deviceNo(o.getDeviceNo())
                        .initVolume(ObjectUtil.isNotNull(queryTowerFarmLog.getVolume()) ? queryTowerFarmLog.getVolume() : o.getTowerVolume())
                        .density(ObjectUtil.isNotNull(queryTowerFarmLog.getInputDensity()) ? queryTowerFarmLog.getInputDensity() : o.getDensity()).build();
                if (ObjectUtil.isNotNull(queryTowerFarmLog.getVolume())) {
                    o.setTowerVolume(queryTowerFarmLog.getVolume());
                }
                try {
                    if (StringUtils.isNotBlank(data)) {
                        CalResponse calResponse = calForSelfDevNewExport(tower, data, "料塔", ObjectUtil.isNotNull(queryTowerFarmLog.getCompensatePercent())?queryTowerFarmLog.getCompensatePercent():null);
                        if (!"error".equals(calResponse.getNote())) {
                            o.setCalDensity(calResponse.getDensity());
                            o.setCalFeedVolume(calResponse.getFeed_volume());
                            o.setCalVolume(calResponse.getVolume());
                            o.setCalWeight(calResponse.getWeight());
                            o.setCalWeightPrediction(calResponse.getWeightPrediction());
                            o.setInputDensity(ObjectUtil.isNotNull(queryTowerFarmLog.getInputDensity()) ? queryTowerFarmLog.getInputDensity() : 0);
                            o.setRemark("正常");
                        }else {
                            o.setRemark("算法调用失败！");
                        }
                    }else {
                        o.setRemark("该记录无点云数据！");
                    }
                    i++;
                } catch (Exception e) {
                    throw new BaseException("算法调用异常，导出失败");
                }
            });
        });
        while (true){
            if (i == vos.size()){
                i = 0L;
                return vos;
            }
        }
    }

    public CalResponse calForSelfDevNewExport(FeedTower tower, String pointCloud, String type,Double compensatePercent) {
        //单位精度：cm³
        JSONObject param = new JSONObject();
        param.putOpt("pointcloud", pointCloud);
        param.putOpt("type", type);
        param.putOpt("compensatePercent", compensatePercent/100D);
        param.putOpt("fullVolume", ObjectUtil.isEmpty(tower.getInitVolume())?0:tower.getInitVolume());
        param.putOpt("standardDensity", ObjectUtil.isEmpty(tower.getDensity())?0:tower.getDensity());
        String resStr = HttpUtil.post("http://122.9.147.118:8848/volume", param.toString());
        if (resStr.contains("error")){
            CalResponse calResponse = new CalResponse();
            calResponse.setNote("error");
            return calResponse;
        }
        CalResponse calResponse = JSONUtil.toBean(resStr, CalResponse.class);
        if (ObjectUtil.isNotEmpty(calResponse) && calResponse.getSuccess()!=null && calResponse.getSuccess()) {
            log.info("算法调用响应: "+calResponse.toString());
            Long volumeResult = calResponse.getVolume();
            Long feedResult = calResponse.getFeed_volume();
            calResponse.setVolume(volumeResult);
            calResponse.setFeed_volume(feedResult);
            //是否使用测算饲料体积作为重量结果
            if(towerProperty.getSWITCH_PredictionDestinyForWeight()){
                calResponse.setWeight(calResponse.getWeightPrediction());
            }
            //如果没有填写密度,重量始终为0
            if(towerProperty.getSWITCH_NoDestinyWeight0()){
                if(ObjectUtil.isEmpty(tower.getDensity()) || tower.getDensity()==0){
                    calResponse.setWeight(0L);
                }
            }

            return calResponse;
        } else {
            if(ObjectUtil.isNotEmpty(calResponse.getNote())){
                throw new BaseException(calResponse.getNote());
            }else{
                throw new BaseException("算法调用失败,错误的请求参数!");
            }
        }
    }

    @Override
    public List<TowerLogExportVo> logeExportMore(QueryTowerFarmLog queryTowerFarmLog) {
        return towerLogMapper.selectExporMoretList(queryTowerFarmLog);
    }

    @Override
    public void bdCorrection(Long towerId, Double bdWeight,Boolean ifExpend){
        //磅单辅助校正
        FeedTower feedTower = towerMapper.selectTowerById(towerId);
        if(ObjectUtil.isEmpty(feedTower)){
            throw new BaseException("料塔id不存在");
        }
        if(bdWeight<1D){
            throw new BaseException("校正的磅单重量不能低于1");
        }
        //查找最近一次的打料记录
        FeedTowerLog log = new FeedTowerLog();
        log.setTowerId(towerId);
        log.setStatus(TowerStatus.completed.name());
        log.setModified(1);
        FeedTowerLog feedTowerLog = towerLogMapper.selectOneIn(log);
        if(ObjectUtil.isNotEmpty(feedTowerLog) && feedTowerLog.getModified() ==1){
            feedTower.setBdOptimization(1);
            feedTower.setBdWeight(Math.round(bdWeight * 1000000D));
            feedTower.setWeBdWeight(feedTowerLog.getWeight());
            feedTower.setGapWeight(feedTower.getWeBdWeight()-feedTower.getBdWeight());
            //TODO 有些料塔是固定偏大那么多,就不用膨胀计算
            if(ifExpend){
                //根据调整的数据重新计算近期的结果,如果能对应上,则不开启打料膨胀效果,TODO 但是目前感觉膨不膨胀他不是固定的,根据打料的多少和料塔空塔体积大小来判定
                feedTower.setSwitchFeedAddExpansion(1L);
                feedTower.setCorrectEmptyTowerVolume(Math.round(feedTower.getGapWeight()/3D/feedTower.getDensity()*1000000D));
                //打料膨胀系数  (基于差距量) gap_weight
                feedTower.setFeedAddExpansionValue(2D/3D);
            }else{
                //根据调整的数据重新计算近期的结果,如果能对应上,则不开启打料膨胀效果,TODO 但是目前感觉膨不膨胀他不是固定的,根据打料的多少和料塔空塔体积大小来判定
                feedTower.setSwitchFeedAddExpansion(0L);
                feedTower.setCorrectEmptyTowerVolume(Math.round((double)feedTower.getGapWeight()/feedTower.getDensity()*1000000D));
                //打料膨胀系数  (基于差距量) gap_weight
                feedTower.setFeedAddExpansionValue(0D);
            }
            //磅单校正时间
            feedTower.setBdInitTime(LocalDateTime.now());
            feedTowerMapper.updateTowerAllInfoByIdIn(feedTower);
        }else{
            throw new BaseException("最近一次没有打料,请在打料测量之后再进行磅单辅助校正,防止出错!");
        }
    }

    @Override
    public FarmTowerDeviceVo deviceInfoByNo(String deviceNo) {
        FarmTowerDeviceVo vo = towerMapper.deviceInfoByNo(deviceNo);
        if (ObjectUtil.isNotNull(vo)){
            RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo));
            if (bucket.isExists()) {
                DeviceStatus status = bucket.get();
                status.setNetworkStatus(ListUtil.of("在线", "弱", "中", "强").contains(status.getNetworkStatus()) ? "在线" : "离线");
                vo.setDeviceStatus(status);
            }
        }
        return vo;
    }

    /**
     * 切换联网模式
     * @param deviceNo
     * @param netMode
     */
    @Override
    public boolean chooseNetMode(String deviceNo, Long netMode) {
        return towerDeviceMapper.chooseNetMode(deviceNo, netMode) > 0;
    }

    public void setTowerLogs(FeedTowerLogDto feedTowerLog, FeedTower feedTower, LocalDateTime time) {
        feedTowerLog.setCreateTime(time);
        feedTowerLog.setStatus("completed");
        feedTowerLog.setCompanyId(feedTower.getCompanyId());
        feedTowerLog.setPigFarmId(feedTower.getPigFarmId());
        feedTowerLog.setDeviceNo(feedTower.getDeviceNo());
        feedTowerLog.setTowerCapacity(feedTower.getCapacity());
        feedTowerLog.setTowerDensity(feedTower.getDensity());
        feedTowerLog.setTowerVolume(feedTower.getInitVolume());
        feedTowerLog.setStartMode("自动");
        feedTowerLog.setNetwork("强");
        feedTowerLog.setVolumeBase(feedTower.getInitVolume() - feedTowerLog.getVolume());
        feedTowerLog.setRemark("正常");
        feedTowerLog.setTooFull(0);
        feedTowerLog.setEmptyTower(0);
        feedTowerLog.setTooMuchDust(0);
        feedTowerLog.setCaking(0);
        feedTowerLog.setTaskNo(CRC16Util.toHexString(System.currentTimeMillis()/1000));
    }

    public LocalDateTime getCreateTime(FeedTowerLogDto feedTowerLog) {
        Calendar calendar = Calendar.getInstance();
        LocalDateTime localDateTime = stringToLocalDateTime(feedTowerLog.getCompletedTime(), "yyyy-MM-dd HH:mm:ss");
        Date date = localDateTimeToDate(localDateTime);
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE,-12);
        return dateToLocalDateTime(calendar.getTime());
    }

    /**
     * String转LocalDateTime
     * @param str
     * @param format
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String str,String format){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(str,df);
    }

    public Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    public LocalDateTime dateToLocalDateTime(Date date){
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static void main(String[] args) throws IOException {

//        System.out.println(ZmMathUtil.calculateAccuracy(898830 ,1000000) );
//
//        System.out.println((100D - ((Math.abs(898830D - 1000000D) / 1000000D) * 100D))*100D);
//        System.out.println(DateUtil.parseDate("2023-08-07"));
//        System.out.println(DateUtil.endOfDay(DateUtil.parseDate("2023-08-07")));
            System.out.println(Math.round(1910106*0.965));

//        codes().forEach(str -> {
//            JSONObject obj = JSONUtil.createObj().putOpt("type", "TowerMonitor").putOpt("code", str).putOpt("version", "V2");
//            BufferedImage file= QrCodeUtil.generate(obj.toString(), 450, 480);
//            ImgUtil.pressText(file, new File("D:\\tmp\\"+str+".png"),
//                    str,
//                    Color.black,
//                    new Font("微软雅黑", Font.BOLD, 26), 0, 230, 1);
//        });

//        log.info(LocalDateTime.now());
//        log.info(LocalDateTime.now().plus(20, ChronoUnit.SECONDS));

//        System.out.println(LocalDateTimeUtil.between(LocalDateTime.now().plus(20, ChronoUnit.SECONDS), LocalDateTime.now(), ChronoUnit.SECONDS));

//        ServerSocket ss = new ServerSocket(8787);
//        Socket s = ss.accept();
//        InputStream is = s.getInputStream();
//        byte[] bys = new byte[1024];
//        int len = is.read(bys);
//        String clientMsg = new String(bys, 0, len);
//        log.info("客户端数据："+clientMsg);
//        OutputStream os = s.getOutputStream();
//        os.write("数据已收到".getBytes(StandardCharsets.UTF_8));
//        s.close();
//        ss.close();

//        System.out.println(LocalDateTime.now());
//        System.out.println(LocalDateTimeUtil.parse("2023-06-06T16:28:53.135").format(DateTimeFormatter.ofPattern("")));
    }

    private static List<String> codes() {
        return Arrays.asList(
//            "c8_c9_a3_98_18_c8",
//            "c8_c9_a3_98_18_da",
//            "c8_c9_a3_97_c8_21",
//            "c8_c9_a3_97_c8_3f",
//            "c8_c9_a3_97_c8_1f",
//            "c8_c9_a3_97_c9_13",
//            "c8_c9_a3_98_18_c4",
//            "48_55_19_d5_8f_c6",
//            "c8_c9_a3_98_17_fa",
//            "c8_c9_a3_97_c9_5a",
//            "c8_c9_a3_98_19_96",
//            "c8_c9_a3_98_3d_d7",
//            "c8_c9_a3_97_c9_53",
//            "c8_c9_a3_98_19_dd",
//            "c8_c9_a3_98_17_f8",
//            "c8_c9_a3_97_c9_77",
//            "c8_c9_a3_98_19_95",
//            "c8_c9_a3_97_c9_5d",
//            "c8_c9_a3_97_c8_18",
//            "c8_c9_a3_98_19_a1",
//            "c8_c9_a3_97_c8_49",
//            "c8_c9_a3_98_19_d4",
//            "c8_c9_a3_97_c8_17",
//            "c8_c9_a3_98_19_da",
//            "c8_c9_a3_97_c9_3e",
//            "c8_c9_a3_97_c8_2f",
//            "c8_c9_a3_97_c8_8f",
//            "c8_c9_a3_97_c7_f7",
//            "c8_c9_a3_97_c8_b3",
//            "c8_c9_a3_98_18_c2",
//            "c8_c9_a3_98_18_e1",
//            "c8_c9_a3_98_18_50",
//            "c8_c9_a3_98_18_d2",
//            "c8_c9_a3_98_18_cb",
//            "c8_c9_a3_98_18_d7",
//            "c8_c9_a3_97_c9_26",
//            "c8_c9_a3_98_19_24",
//            "c8_c9_a3_97_c8_38",
//            "c8_c9_a3_97_c8_9",
//            "c8_c9_a3_97_c9_44"

//                "c8_c9_a3_98_19_28"

                //第二批
//            "44_17_93_17_78_61",
//            "c8_c9_a3_97_c8_f",
//            "48_55_19_d5_94_5a",
//            "c8_c9_a3_97_c8_3e",
//            "48_55_19_d5_a2_7e",
//            "48_55_19_d5_da_55",
//            "48_55_19_d5_99_3",
//            "48_55_19_d5_8a_be",
//            "48_55_19_d6_7f_4a",
//            "48_55_19_d6_71_5d",
//            "48_55_19_d6_73_15"

//                "c8_c9_a3_97_c9_43"
//                "c8_c9_a3_97_c9_45"
//                "430470373859423005DDFF39"
//                "430475363859423005D7FF39"
                "431375093859423005D6FF39",
                "431053313859423005D6FF39",
                "431056303859423005D7FF39"
        );
    }

}
