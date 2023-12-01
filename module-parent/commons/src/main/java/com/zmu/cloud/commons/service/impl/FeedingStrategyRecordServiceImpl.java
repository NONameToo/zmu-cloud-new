package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.dto.AddFeedingStrategyDto;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.sphservice.SphEmployService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.FeedingStrategyVo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 初始饲喂策略划分：
 * record = 0  云慧养系统默认饲喂策略
 * company_id = 100002, pig_farm_id = -1 巨星所有公司公用饲喂策略
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedingStrategyRecordServiceImpl extends ServiceImpl<FarmFeedingStrategyRecordMapper, FarmFeedingStrategyRecord>
        implements FeedingStrategyRecordService {

    final FarmFeedingStrategyMapper strategyMapper;
    final FarmFeedingStrategyRecordMapper recordMapper;
    final FarmFeedingStrategyRecordDetailMapper detailMapper;
    final FarmFeedingStrategyAllowMapper allowMapper;
    final FeedingStrategyRecordDetailService feedingStrategyDetailService;
    final FeedingStrategyService feedingStrategyService;
    final SysUserMapper userMapper;
    final SphEmployMapper employMapper;
    final PigTypeMapper pigTypeMapper;
    final PigFarmService farmService;
    final RedissonClient redis;

    @Override
    public List<FarmFeedingStrategyAllow> allows() {
        return allowMapper.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 查询当前猪场的饲喂策略
     * 查询逻辑：
     *  1、如果该场自身未配置饲喂策略，就使用最新的公共策略 farmId = -1
     *  2、已经配置有，则使用最新配置
     * @return
     */
    @Override
    public List<FeedingStrategyVo> all() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        PigFarm farm = farmService.findByCache(info.getPigFarmId());
        LambdaQueryWrapper<FarmFeedingStrategyRecord> wrapper = new LambdaQueryWrapper<>();
        List<FarmFeedingStrategyRecord> records;
        //巨星
        if (UserClientTypeEnum.SphAndroid.equals(info.getClientType())) {
            wrapper.in(FarmFeedingStrategyRecord::getPigFarmId, info.getPigFarmId(), -1);
            wrapper.eq(FarmFeedingStrategyRecord::getPigTypeId, farm.getPigTypeId());
            wrapper.orderByDesc(FarmFeedingStrategyRecord::getCreateTime);
            records = recordMapper.selectList(wrapper);
            //如果有自有策略配置，则显示自有的配置列表
            if (records.stream().anyMatch(record -> record.getPigFarmId() > 0)) {
                records = records.stream().filter(record -> record.getPigFarmId() > 0).collect(toList());
            }
        } else {
            wrapper.in(FarmFeedingStrategyRecord::getPigFarmId, info.getPigFarmId());
            wrapper.orderByDesc(FarmFeedingStrategyRecord::getCreateTime);
            records = recordMapper.selectList(wrapper);
        }
        return records.stream().map(r -> {
            String userName = "";
            if (UserClientTypeEnum.SphAndroid.equals(info.getClientType())) {
                SphEmploy employ = employMapper.selectById(r.getOperatorId());
                userName = ObjectUtil.isNotEmpty(employ)?employ.getName():"系统";
            } else {
                SysUser user = userMapper.selectById(r.getOperatorId());
                userName = ObjectUtil.isNotEmpty(user)?user.getRealName():"系统";
            }
            FeedingStrategyVo vo = new FeedingStrategyVo();
            vo.setRecordId(r.getId());
            vo.setName(r.getName());
            vo.setUserId(r.getOperatorId());
            vo.setUserName(userName);
            vo.setFileName(r.getFileName());
            vo.setUploadTime(DateUtil.format(r.getCreateTime(), "yyyy-MM-dd"));
            LambdaQueryWrapper<FarmFeedingStrategyRecordDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.eq(FarmFeedingStrategyRecordDetail::getRecordId, r.getId());
            List<FarmFeedingStrategyRecordDetail> details = detailMapper.selectList(detailWrapper);
            vo.setDetails(details);
            return vo;
        }).collect(toList());
    }

    @Override
    public void add(AddFeedingStrategyDto dto) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        FarmFeedingStrategyRecord record = new FarmFeedingStrategyRecord();
        record.setCompanyId(info.getCompanyId());
        record.setPigFarmId(info.getPigFarmId());
        PigFarm farm = farmService.findByCache(info.getPigFarmId());
        Long pigTypeId;
        //巨星有控制操作权限
        if (UserClientTypeEnum.SphAndroid.equals(info.getClientType())) {
            List<FarmFeedingStrategyAllow> allows = allows();
            if (!allows.stream().map(FarmFeedingStrategyAllow::getEmployId).collect(toList()).contains(info.getUserId())) {
                throw new BaseException("抱歉，您没有此权限！");
            }
            if (ObjectUtil.isEmpty(dto.getCommon())) {
                throw new BaseException("‘是否公用‘字段不能为空");
            }
            //巨星所有场公用饲喂策略，则配置farmId = -1
            if (Boolean.TRUE.equals(dto.getCommon())) {
                record.setPigFarmId(-1L);
                if (ObjectUtil.isEmpty(dto.getPigType())) {
                    throw new BaseException("请先配置猪场饲养的猪种，例如：PIC、加系");
                }
                pigTypeId = dto.getPigType();
            } else {
                pigTypeId = farm.getPigTypeId();
            }
        } else {
            if (ObjectUtil.isEmpty(farm.getPigTypeId())) {
                throw new BaseException("请先配置猪场饲养的猪种，例如：PIC、加系");
            }
            pigTypeId = farm.getPigTypeId();
        }
        PigType type = pigTypeMapper.selectById(pigTypeId);
        record.setPigTypeId(type.getId());
        record.setPigType(type.getName());
        record.setName(dto.getName());
        record.setOperatorId(info.getUserId());
        record.setCreateBy(info.getUserId());
        record.setFileName("");
        record.setStoragePath("/");
        recordMapper.insert(record);

        addRecordDetail(dto, record);
        addFeedingStrategy(dto, record);
    }

    @Override
    public void add(PigFarm farm) {
        //暂时只给种猪场添加饲喂策略
        if (farm.getType() != 1) {
            return;
        }
        PigType pigType = pigTypeMapper.selectById(farm.getPigTypeId());
        FarmFeedingStrategyRecord def = new FarmFeedingStrategyRecord();
        def.setCompanyId(farm.getCompanyId());
        def.setPigFarmId(farm.getId());
        def.setPigTypeId(pigType.getId());
        def.setPigType(pigType.getName());
        def.setName("默认");
        def.setFileName("");
        def.setStoragePath("/");
        def.setOperatorId(ObjectUtil.isEmpty(farm.getPrincipalId())?RequestContextUtils.getUserId():farm.getPrincipalId());
        def.setCreateBy(farm.getPrincipalId());
        recordMapper.insert(def);

        /**
         * 云慧养新建猪场默认配置的饲喂策略 recordId = 0
         */
        List<FarmFeedingStrategyRecordDetail> details =
                detailMapper.selectList(new LambdaQueryWrapper<FarmFeedingStrategyRecordDetail>()
                        .eq(FarmFeedingStrategyRecordDetail::getRecordId, 0));
        details.forEach(d -> {
            d.setId(null);
            d.setRecordId(def.getId());
        });
        feedingStrategyDetailService.saveBatch(details);

        List<FarmFeedingStrategy> strategies =
                strategyMapper.selectList(new LambdaQueryWrapper<FarmFeedingStrategy>()
                        .eq(FarmFeedingStrategy::getRecordId, 0));

        strategies.forEach(s -> {
            s.setId(null);
            s.setRecordId(def.getId());
        });
        feedingStrategyService.saveBatch(strategies);
    }

    @Override
    public Integer queryDefaultFeedingAmount(Long farmId) {
        RBucket<Integer> bucket = redis.getBucket(CacheKey.Web.farm_default_feeding_amount.key + farmId);
        if (bucket.isExists()) {
            return bucket.get();
        } else {
            PigFarm farm = farmService.findByCache(farmId);
            if (ObjectUtil.isEmpty(farm.getDefaultFeedingAmount())) {
                farm.setDefaultFeedingAmount(2300);
                farmService.updateFarm(farm);
            }
            bucket.set(farm.getDefaultFeedingAmount());
            return farm.getDefaultFeedingAmount();
        }
    }

    @Override
    public void saveDefaultFeedingAmount(Integer amount) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        PigFarm farm = farmService.findByCache(info.getPigFarmId());
        farm.setDefaultFeedingAmount(amount);
        farmService.updateFarm(farm);
        redis.getBucket(CacheKey.Web.farm_default_feeding_amount.key + info.getPigFarmId()).set(amount);
    }

    private void addRecordDetail(AddFeedingStrategyDto dto, FarmFeedingStrategyRecord record) {
        List<FarmFeedingStrategyRecordDetail> details = new ArrayList<>();
        details.add(new FarmFeedingStrategyRecordDetail(null, "阶段", "饲料", "瘦（1）", "偏瘦（2）", "适宜（3）", "偏肥（4）", "肥（5）", record.getId()));
        details.add(wrap("1-3d", dto.getStage3d1(), dto.getStage3d2(), dto.getStage3d3(), dto.getStage3d4(), dto.getStage3d5(), record.getId()));
        details.add(wrap("4-30d（经产）", dto.getStage30d1(), dto.getStage30d2(), dto.getStage30d3(), dto.getStage30d4(), dto.getStage30d5(), record.getId()));
        details.add(wrap("4-30d（头胎）", dto.getStage30dFirst1(), dto.getStage30dFirst2(), dto.getStage30dFirst3(), dto.getStage30dFirst4(), dto.getStage30dFirst5(), record.getId()));
        details.add(wrap("31-90d", dto.getStage90d1(), dto.getStage90d2(), dto.getStage90d3(), dto.getStage90d4(), dto.getStage90d5(), record.getId()));
        details.add(wrap("91d-上产床", dto.getStageOnObstetricTable1(), dto.getStageOnObstetricTable2(), dto.getStageOnObstetricTable3(), dto.getStageOnObstetricTable4(), dto.getStageOnObstetricTable5(), record.getId()));
//        details.add(wrap("上产床—分娩", dto.getStagePerinatalPeriod1(), dto.getStagePerinatalPeriod2(), dto.getStagePerinatalPeriod3(), dto.getStagePerinatalPeriod4(), dto.getStagePerinatalPeriod5(), record.getId()));
        feedingStrategyDetailService.saveBatch(details);
    }

    private void addFeedingStrategy(AddFeedingStrategyDto dto, FarmFeedingStrategyRecord record) {
        List<FarmFeedingStrategy> strategies = new ArrayList<>();
        strategies.add(new FarmFeedingStrategy(null, 1, 3, 1, 0, dto.getStage3d1(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 1, 3, 2, 0, dto.getStage3d2(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 1, 3, 3, 0, dto.getStage3d3(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 1, 3, 4, 0, dto.getStage3d4(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 1, 3, 5, 0, dto.getStage3d5(), record.getId()));

        strategies.add(new FarmFeedingStrategy(null, 4, 30, 1, 1, dto.getStage30dFirst1(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 4, 30, 2, 1, dto.getStage30dFirst2(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 4, 30, 3, 1, dto.getStage30dFirst3(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 4, 30, 4, 1, dto.getStage30dFirst4(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 4, 30, 5, 1, dto.getStage30dFirst5(), record.getId()));

        strategies.add(new FarmFeedingStrategy(null, 4, 30, 1, 0, dto.getStage30d1(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 4, 30, 2, 0, dto.getStage30d2(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 4, 30, 3, 0, dto.getStage30d3(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 4, 30, 4, 0, dto.getStage30d4(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 4, 30, 5, 0, dto.getStage30d5(), record.getId()));

        strategies.add(new FarmFeedingStrategy(null, 31, 90, 1, 0, dto.getStage90d1(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 31, 90, 2, 0, dto.getStage90d2(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 31, 90, 3, 0, dto.getStage90d3(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 31, 90, 4, 0, dto.getStage90d4(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 31, 90, 5, 0, dto.getStage90d5(), record.getId()));

        strategies.add(new FarmFeedingStrategy(null, 91, 110, 1, 0, dto.getStageOnObstetricTable1(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 91, 110, 2, 0, dto.getStageOnObstetricTable2(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 91, 110, 3, 0, dto.getStageOnObstetricTable3(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 91, 110, 4, 0, dto.getStageOnObstetricTable4(), record.getId()));
        strategies.add(new FarmFeedingStrategy(null, 91, 110, 5, 0, dto.getStageOnObstetricTable5(), record.getId()));

//        strategies.add(new FarmFeedingStrategy(null, 111, 117, 1, 0, dto.getStagePerinatalPeriod1(), record.getId()));
//        strategies.add(new FarmFeedingStrategy(null, 111, 117, 2, 0, dto.getStagePerinatalPeriod2(), record.getId()));
//        strategies.add(new FarmFeedingStrategy(null, 111, 117, 3, 0, dto.getStagePerinatalPeriod3(), record.getId()));
//        strategies.add(new FarmFeedingStrategy(null, 111, 117, 4, 0, dto.getStagePerinatalPeriod4(), record.getId()));
//        strategies.add(new FarmFeedingStrategy(null, 111, 117, 5, 0, dto.getStagePerinatalPeriod5(), record.getId()));
        feedingStrategyService.saveBatch(strategies);
    }

    private FarmFeedingStrategyRecordDetail wrap(String stage, Integer one, Integer thin, Integer suitable, Integer fat,
                                                Integer five, Long recordId) {
        FarmFeedingStrategyRecordDetail detail = new FarmFeedingStrategyRecordDetail();
        detail.setStage(stage);
        detail.setOne(new DecimalFormat("0.##").format((float) one / 1000).concat(" kg"));
        detail.setThin(new DecimalFormat("0.##").format((float) thin / 1000).concat(" kg"));
        detail.setSuitable(new DecimalFormat("0.##").format((float) suitable / 1000).concat(" kg"));
        detail.setFat(new DecimalFormat("0.##").format((float) fat / 1000).concat(" kg"));
        detail.setFive(new DecimalFormat("0.##").format((float) five / 1000).concat(" kg"));
        detail.setRecordId(recordId);
        return detail;
    }

    public static void main(String[] args) {
        System.out.println(new DecimalFormat("0.##").format((float) 9 / 1000).concat(" kg"));
    }

    /**
     * 获取饲喂策略
     * @param pigTypeId
     * @return
     */
    @Override
    public Optional<Long> queryFeedingStrategyRecord(Long farmId, Long pigTypeId) {
        FarmFeedingStrategyRecord record = baseMapper.selectOne(Wrappers.lambdaQuery(FarmFeedingStrategyRecord.class)
                .in(FarmFeedingStrategyRecord::getPigFarmId, farmId)
                .eq(FarmFeedingStrategyRecord::getPigTypeId, pigTypeId)
                .orderByDesc(FarmFeedingStrategyRecord::getCreateTime)
                .last("limit 1")
        );
        //云慧养猪场和巨星场自有的策略配置
        if (ObjectUtil.isNotNull(record)) {
            return Optional.of(record.getId());
        }
        //巨星公共策略
        record = baseMapper.selectOne(Wrappers.lambdaQuery(FarmFeedingStrategyRecord.class)
                .in(FarmFeedingStrategyRecord::getPigFarmId, -1)
                .eq(FarmFeedingStrategyRecord::getPigTypeId, pigTypeId)
                .orderByDesc(FarmFeedingStrategyRecord::getCreateTime)
                .last("limit 1"));
        if (ObjectUtil.isNotNull(record)) {
            return Optional.of(record.getId());
        }
        log.info("猪场【{}】猪种【{}】未查找到相关的饲喂策略", farmId, pigTypeId);
        return Optional.empty();
    }
}
