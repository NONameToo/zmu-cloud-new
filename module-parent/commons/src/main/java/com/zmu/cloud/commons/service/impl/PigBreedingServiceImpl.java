package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.system.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.dto.*;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.enums.PigSexEnum;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.VarietyEnum;
import com.zmu.cloud.commons.enums.app.ColumnOperateType;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.utils.StrUtils;
import com.zmu.cloud.commons.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zmu.cloud.commons.enums.PigBreedingStatusEnum.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author shining
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PigBreedingServiceImpl extends ServiceImpl<PigBreedingMapper, PigBreeding> implements PigBreedingService {
    final PigMatingService matingService;
    final PigMatingMapper pigMatingMapper;
    final PigBreedingChangeHouseMapper pigBreedingChangeHouseMapper;
    final PigBreedingChangeHouseService pigBreedingChangeHouseService;
    final PigPregnancyMapper pigPregnancyMapper;
    final PigLaborMapper pigLaborMapper;
    final PigPiggyMapper pigPiggyMapper;
    final PigWeanedMapper pigWeanedMapper;
    final PigSemenCollectionMapper pigSemenCollectionMapper;
    final FinancialDataMapper financialDataMapper;
    final FinancialDataTypeMapper financialDataTypeMapper;
    final ProductionDayService productionDayService;
    final PigHouseColumnsMapper columnsMapper;
    final PigHouseColumnsService columnsService;
    final ZmuCloudProperties zmuCloudProperties;
    final RedissonClient redis;
    final BackFatService backFatService;
    final PigBreedingMapper breedingMapper;


    @Qualifier("jxJdbcTemplate")
    final JdbcTemplate jxJdbcTemplate;

    @Override
    public PageInfo<PigBreedingListVO> page(QueryPig queryPigBreeding) {
        PageHelper.startPage(queryPigBreeding.getPage(), queryPigBreeding.getSize());
        queryPigBreeding.setPresenceStatus(1);
        if (ObjectUtil.isNotEmpty(queryPigBreeding.getPigStatuses())) {
            queryPigBreeding.setStatuses(Arrays.stream(queryPigBreeding.getPigStatuses().split(","))
                    .filter(ObjectUtil::isNotEmpty).map(Integer::parseInt).collect(toList()));
        }
        List<PigBreedingListVO> pigBreedingListVOPageInfo = baseMapper.page(queryPigBreeding);
        return PageInfo.of(pigBreedingListVOPageInfo);
    }

    @Override
    public List<SimplePigVo> findByCache(String earNumber) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        Set<SimplePigVo> vos = cacheOtherPig(info.getPigFarmId(), earNumber);
        return vos.stream().filter(pig -> ObjectUtil.isEmpty(earNumber) || pig.getEarNumber().contains(earNumber)).limit(6).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void add(PigBreedingParam pigBreeding) {
        //进场规则验证
        addRule(pigBreeding);
        //胎次日龄转换出生日期，公式：日龄=220+胎次*145
        if (ObjectUtil.isEmpty(pigBreeding.getBirthDate())) {
            pigBreeding.setBirthDate(DateUtil.offsetDay(new Date(), -1*(220 + pigBreeding.getParity()*145)));
        }
        //根据状态日期计算配种日期
//        if (ObjectUtil.equals(MATING.getStatus(), pigBreeding.getPigStatus())) {
//            pigBreeding.setStatusTime(DateUtil.offsetDay(DateUtil.parseDate(pigBreeding.getExpectedDate()), -114));
//        }
        pigBreeding.setOperatorId(RequestContextUtils.getUserId());
        baseMapper.insert(pigBreeding);
        addStateRecord(pigBreeding);

        //记录财务
//        if (!ObjectUtils.isEmpty(pigBreeding.getPrice()) && pigBreeding.getPrice().compareTo(BigDecimal.ZERO)>0) {
//            //查询财务类型为购买的Id
//            LambdaQueryWrapper<FinancialDataType> queryWrapper2 = new LambdaQueryWrapper<>();
//            queryWrapper2.eq(FinancialDataType::getDataType, 2);
//            FinancialDataType financialDataType = financialDataTypeMapper.selectOne(queryWrapper2);
//            FinancialData build1 = FinancialData.builder().dataTypeId(financialDataType.getId())
//                    .income(2).number(1).unitPrice(pigBreeding.getPrice())
//                    .totalPrice(pigBreeding.getPrice()).status(0).createBy(userId)
//                    .remark("猪只购买：耳号：" + pigBreeding.getEarNumber()).build();
//            financialDataMapper.insert(build1);
//        }
    }

    /**
     * 进猪验证规则
     * @param pig
     */
    private void addRule(PigBreedingParam pig) {
        if (ObjectUtil.isEmpty(pig.getPigHouseId())) {
            throw new BaseException("转入栋舍不能为空");
        }
        if (ObjectUtil.isEmpty(pig.getEarNumber()) || ObjectUtil.length(pig.getEarNumber()) > 20) {
            throw new BaseException("耳号不能为空且长度不能超过20个字符");
        }
        //通过猪耳号查询是否已存在
        PigBreeding exists = baseMapper.selectOne(Wrappers.lambdaQuery(PigBreeding.class)
                .eq(PigBreeding::getPresenceStatus, 1)
                .eq(PigBreeding::getEarNumber, pig.getEarNumber()));
        //查询耳号是否存在
        if (ObjectUtil.isNotEmpty(exists)) {
            throw new BaseException("当前耳号已存在");
        }
        if (PigSexEnum.Sow.equals(PigSexEnum.type(pig.getType()))) {
            if (ObjectUtil.equals(RESERVE.getStatus(), pig.getPigStatus()) && ObjectUtil.notEqual(pig.getParity(), 0)) {
                throw new BaseException("后备母猪胎次只能为0");
            }
            if (ObjectUtil.notEqual(RESERVE.getStatus(), pig.getPigStatus())) {
                if (ObjectUtil.isNull(pig.getStatusTime()) || pig.getStatusTime().after(new Date())) {
                    throw new BaseException("配种、空怀、返情、流产、妊娠、哺乳、断奶状态母猪状态日期不能为空且不能超过今天");
                }
                if (ObjectUtil.isEmpty(pig.getParity()) || pig.getParity() <= 0 || pig.getParity() > 99) {
                    throw new BaseException("配种、空怀、返情、流产、妊娠、哺乳、断奶状态母猪胎次必须大于0且小于100");
                }
            }
            if (ObjectUtil.equals(LACTATION.getStatus(), pig.getPigStatus())
                    && (ObjectUtil.isEmpty(pig.getHealthyNumber()) || pig.getHealthyNumber() <= 0 || pig.getHealthyNumber() > 99)) {
                throw new BaseException("哺乳状态母猪健仔数必须大于0且小于100");
            }
        } else if (ObjectUtil.notEqual(RESERVE.getStatus(), pig.getPigStatus()) || ObjectUtil.notEqual(pig.getParity(), 0)) {
            throw new BaseException("公猪只能是后备且胎次为0");
        }
    }

    private void addStateRecord(PigBreedingParam pig) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        switch (PigBreedingStatusEnum.getStatus(pig.getPigStatus())) {
            case MATING:
                pigMatingMapper.insert(PigMating.builder()
                        .matingDate(pig.getStatusTime()).parity(pig.getParity()).pigBreedingId(pig.getId()).type(1)
                        .remark("自动创建" + (ObjectUtil.isEmpty(pig.getRemark())?"":pig.getRemark()))
                        .createBy(info.getUserId()).operatorId(pig.getOperatorId()).build());
                break;
            case EMPTY:
            case RETURN:
            case ABORTION:
            case PREGNANCY:
                //根据状态日期计算配种日期
                //配种后一般25到35天妊检,取中间值30
                Date matingDate = DateUtil.offsetDay(pig.getStatusTime(), -30);
                int result = PigBreedingStatusEnum.getStatus(pig.getPigStatus()).getResult();
                pigPregnancyMapper.insert(PigPregnancy.builder().pigBreedingId(pig.getId()).createBy(info.getUserId())
                        .matingDate(matingDate).operatorId(pig.getOperatorId()).parity(pig.getParity())
                        .remark("自动创建" + (ObjectUtil.isEmpty(pig.getRemark())?"":pig.getRemark()))
                        .pregnancyDate(pig.getStatusTime()).pregnancyResult(result).build());
                pigMatingMapper.insert(PigMating.builder()
                        .matingDate(matingDate).parity(pig.getParity()).pigBreedingId(pig.getId()).type(1)
                        .remark("自动创建" + (ObjectUtil.isEmpty(pig.getRemark())?"":pig.getRemark()))
                        .createBy(info.getUserId()).operatorId(pig.getOperatorId()).build());
                break;
            case LACTATION:
                pigLaborMapper.insert(PigLabor.builder()
                        .pigBreedingId(pig.getId()).laborDate(pig.getStatusTime()).laborResult(1)
                        .laborMinute(180).healthyNumber(pig.getHealthyNumber()).feedingNumber(pig.getHealthyNumber())
                        .parity(pig.getParity())
                        .remark("自动创建" + (ObjectUtil.isEmpty(pig.getRemark())?"":pig.getRemark()))
                        .createBy(info.getUserId()).operatorId(pig.getOperatorId()).build());
                PigPiggy pigPiggy = pigPiggyMapper.selectOne(Wrappers.lambdaQuery(PigPiggy.class).eq(PigPiggy::getPigHouseId, pig.getPigHouseId()));
                if (ObjectUtils.isEmpty(pigPiggy)) {
                    pigPiggyMapper.insert(PigPiggy.builder()
                            .pigHouseId(pig.getPigHouseId())
                            .number(pig.getHealthyNumber())
                            .createBy(info.getUserId()).build());
                } else {
                    pigPiggy.setNumber(pigPiggy.getNumber() + pig.getHealthyNumber());
                    pigPiggyMapper.updateById(pigPiggy);
                }

                break;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PigBreedingParam pigBreeding) {
        //通过猪耳号查询是否已存在
        LambdaQueryWrapper<PigBreeding> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PigBreeding::getEarNumber, pigBreeding.getEarNumber());
        PigBreeding pigBreeding1 = super.baseMapper.selectOne(queryWrapper);
        //查询耳号是否存在
        if (!ObjectUtils.isEmpty(pigBreeding1) &&
                !pigBreeding.getId().equals(pigBreeding1.getId())) {
            throw new BaseException("当前耳号已存在");
        }
        PigBreeding pigBreeding2 = super.baseMapper.selectById(pigBreeding.getId());
        //判断是否换了猪舍，如果不相同，表示换了猪舍
//        if (!ObjectUtils.isEmpty(pigBreeding2.getPigHouseColumnsId())){
//            if (!pigBreeding2.getPigHouseColumnsId().equals(pigBreeding.getPigHouseColumnsId())) {
//                PigStockDTO pigStockDTO = baseMapper.count(pigBreeding.getPigHouseColumnsId());
//                if (pigStockDTO.getTotal() + 1 > pigStockDTO.getMaxPerColumns()) {
//                    throw new BaseException("当前存栏数达到最大值");
//                }
//                //并添加转舍记录
//                Long userId = RequestContextUtils.getRequestInfo().getUserId();
//                PigBreedingChangeHouse build2 = PigBreedingChangeHouse.builder().pigBreedingId(pigBreeding.getId())
//                        .createBy(userId).houseColumnsInId(pigBreeding.getPigHouseColumnsId()).changeHouseTime(DateUtil.date())
//                        .houseColumnsOutId(pigBreeding2.getPigHouseColumnsId()).operatorId(pigBreeding.getOperatorId()).build();
//                pigBreedingChangeHouseMapper.insert(build2);
//            }
//        }
        //修改记录
        BeanUtils.copyProperties(pigBreeding, pigBreeding2);
        baseMapper.updateById(pigBreeding2);
    }

    @Override
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PigBreedingImportDto> importPig(String pigHouseId, MultipartFile file) throws IOException {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());

        List<PigBreedingImportDto> errs = new ArrayList<>();
        List<PigBreedingImportDto> dtos = reader.readAll(PigBreedingImportDto.class)
                .stream()
                .filter(dto -> {
                    if (ObjectUtil.hasEmpty(dto.get耳号(), dto.get品种(), dto.get猪只类型(), dto.get种猪状态())) {
                        dto.setErr("耳号、品种、猪只类型、种猪状态不能为空");
                        errs.add(dto);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        //重复的耳号
        Map<String, List<PigBreedingImportDto>> temp = dtos.stream().collect(Collectors.groupingBy(PigBreedingImportDto::get耳号));
        //过滤表格内耳号重复的
        List<String> repeatEars = temp.entrySet().stream().filter(en -> {
            if (en.getValue().size() > 1) {
                en.getValue().forEach(dto -> dto.setErr("耳号重复"));
                errs.addAll(en.getValue());
                return true;
            }
            return false;
        }).map(Map.Entry::getKey).collect(Collectors.toList());

        dtos = dtos.stream().filter(dto -> !repeatEars.contains(dto.get耳号())).collect(Collectors.toList());
        //过滤表格与数据库去重
        Set<String> es = dtos.stream().map(PigBreedingImportDto::get耳号).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(es)) {
            List<PigBreeding> ps = baseMapper.selectList(Wrappers.lambdaQuery(PigBreeding.class)
                    .eq(PigBreeding::getPresenceStatus, 1).in(PigBreeding::getEarNumber, es));
            if (ObjectUtil.isNotEmpty(ps)) {
                dtos = dtos.stream().filter(dto -> {
                    if (ps.stream().anyMatch(p -> p.getEarNumber().equals(dto.get耳号()))) {
                        dto.setErr("系统里已经存在该耳号的猪只");
                        errs.add(dto);
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
            }
        }

        List<PigBreeding> batch = new ArrayList<>();
        dtos.forEach(dto -> {
            PigBreeding pig = PigBreeding.builder()
                    .pigHouseId(Long.parseLong(pigHouseId))
                    .earNumber(dto.get耳号())
                    .approachTime(new Date())
                    .type(Objects.requireNonNull(PigSexEnum.desc(dto.get猪只类型())).getType())
                    .approachType(3)
                    .variety(VarietyEnum.desc(dto.get品种()).getType())
                    .pigStatus(PigBreedingStatusEnum.getStatusKey(dto.get种猪状态()).getStatus())
                    .statusTime(ObjectUtil.isEmpty(dto.get状态日期())?null:DateUtil.parseDate(dto.get状态日期()))
                    .parity(ObjectUtil.isEmpty(dto.get胎次())?0:dto.get胎次())
                    .birthDate(ObjectUtil.isEmpty(dto.get胎次())?null:DateUtil.offsetDay(new Date(), -1*(220 + dto.get胎次()*145)))
                    .operatorId(info.getUserId())
                    .createBy(info.getUserId())
                    .remark(dto.get备注())
                    .build();
            if (Stream.of(MATING, EMPTY, RETURN, ABORTION, PREGNANCY, LACTATION).anyMatch(s -> s.equals(PigBreedingStatusEnum.getStatusKey(dto.get种猪状态())))) {
                PigBreedingParam param = new PigBreedingParam();
                BeanUtil.copyProperties(pig, param);
                if (LACTATION.equals(PigBreedingStatusEnum.getStatusKey(dto.get种猪状态()))) {
                    param.setHealthyNumber(dto.get健仔数());
                }
                try {
                    add(param);
                } catch (Exception e) {
                    dto.setErr(e.getMessage());
                    errs.add(dto);
                }
            } else {
                if (ObjectUtil.equals(RESERVE.getStatus(), pig.getPigStatus()) && ObjectUtil.notEqual(pig.getParity(), 0)) {
                    dto.setErr("后备种猪胎次只能为0");
                    errs.add(dto);
                    return;
                }
                if (PigSexEnum.Sow.equals(PigSexEnum.type(pig.getType())) && ObjectUtil.equals(WEANING.getStatus(), pig.getPigStatus())) {
                    if (ObjectUtil.isEmpty(pig.getParity()) || pig.getParity() <= 0 || pig.getParity() > 99) {
                        dto.setErr("断奶状态母猪胎次必须大于0且小于100");
                        errs.add(dto);
                        return;
                    }
                    if (ObjectUtil.isNull(pig.getStatusTime()) || pig.getStatusTime().after(new Date())) {
                        dto.setErr("断奶状态母猪状态日期不能为空且不能超过今天");
                        errs.add(dto);
                        return;
                    }
                }
                if (PigSexEnum.Boar.equals(PigSexEnum.type(pig.getType())) && ObjectUtil.notEqual(RESERVE.getStatus(), pig.getPigStatus())) {
                    dto.setErr("公猪状态只能为后备");
                    errs.add(dto);
                    return;
                }
                batch.add(pig);
            }
        });
        saveBatch(batch);
        errs.forEach(dto -> {
            if (ObjectUtil.isNotEmpty(dto.get状态日期())) {
                dto.set状态日期(DateUtil.formatDate(DateUtil.parseDate(dto.get状态日期())));
            }
        });
        return errs;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Set<PigBreedingImportDto> importCheck(MultipartFile[] file) throws IOException {
        Set<PigBreedingImportDto> res = new HashSet<>();
        for (MultipartFile f : file) {
            ExcelReader reader = ExcelUtil.getReader(f.getInputStream());
            List<PigBreedingImportDto> dtos = reader.readAll(PigBreedingImportDto.class);

            List<String> earNumbers = dtos.stream().map(PigBreedingImportDto::get耳号).collect(toList());
            //获取已导入耳号
            List<String> ears = breedingMapper.selectExistNumber(earNumbers);
            dtos.forEach(dto -> {
                if (!ears.contains(dto.get耳号())){
                    dto.setErr("未导入！");
                    res.add(dto);
                }
            });
        }
        return res;
    }

    @Override
    public PigBreedingListVO detail(Long id) {
        PigBreedingListVO pigBreedingListVO = baseMapper.queryById(id);
        if (ObjectUtil.isNotEmpty(pigBreedingListVO.getBirthDate())) {
            pigBreedingListVO.setDayAge((int)DateUtil.betweenDay(pigBreedingListVO.getBirthDate(), new Date(), true));
        }
        //获取当前胎次的配种记录
        PigMating mating = matingService.findByParity(id, pigBreedingListVO.getParity());
        if (ObjectUtil.isNotEmpty(mating)) {
            pigBreedingListVO.setExpectedDate(DateUtil.offsetDay(mating.getMatingDate(), 114));
        }
        return pigBreedingListVO;
    }

    @Override
    public PigBreedingStatisticsVO statistics(Long id) {
        PigBreedingStatisticsVO pigBreedingStatisticsVO = baseMapper.statistics(id);
        return pigBreedingStatisticsVO;
    }

    @Override
    public List<EventPigBreedingVO> eventDetail(Long id) {
        List<EventPigBreedingVO> pigBreedingEventVOS = new ArrayList<>();

        List<EventDetailVO> eventDetailVOS = new ArrayList<>();
        //查询配种信息
        List<EventMatingVO> matingVOS = pigMatingMapper.selectEventById(id);
        //查询妊娠信息
        List<EventPregnancyVO> pregnancyVOS = pigPregnancyMapper.selectEventId(id);
        //查询分娩记录
        List<EventLaborVO> eventLaborVOS = pigLaborMapper.selectEventId(id);
        //查询断奶记录
        List<EventWeanedVO> eventWeanedVO = pigWeanedMapper.selectEventId(id);


        eventDetailVOS.addAll(matingVOS);
        eventDetailVOS.addAll(pregnancyVOS);
        eventDetailVOS.addAll(eventLaborVOS);
        eventDetailVOS.addAll(eventWeanedVO);
        //先按事件发生的时间排序
        eventDetailVOS.sort(Comparator.comparing(EventDetailVO::getCreateTime).reversed());
        //再以胎次分组
        Map<Integer, List<EventDetailVO>> collect = eventDetailVOS.stream().collect(Collectors.groupingBy(EventDetailVO::getParity));

        for (Map.Entry<Integer, List<EventDetailVO>> entry : collect.entrySet()) {
            EventPigBreedingVO pigBreedingEventVO = new EventPigBreedingVO();
            pigBreedingEventVO.setParity(entry.getKey());
            //取到对应胎次的数量,如果当前胎次没有记录，证明没有分娩，数量就为0，否则就将活仔数加起来
            if (!CollectionUtils.isEmpty(eventLaborVOS)) {
                Optional<EventLaborVO> any = eventLaborVOS.stream().filter(x -> x.getParity().equals(entry.getKey())).findAny();
                if (any.isPresent()) {
                    EventLaborVO eventLaborVO = any.get();
                    Integer number = eventLaborVO.getHealthyNumber() + eventLaborVO.getWeakNumber() + eventLaborVO.getDeformityNumber();
                    pigBreedingEventVO.setNumber(number);
                }
            }
            pigBreedingEventVO.setEventDetailVOList(entry.getValue());
            pigBreedingEventVOS.add(pigBreedingEventVO);
        }
        pigBreedingEventVOS.sort(Comparator.comparing(EventPigBreedingVO::getParity).reversed());
        return pigBreedingEventVOS;
    }

    @Override
    public List<EventSemenCollectionVO> eventSemenDetail(Long id) {
        List<EventSemenCollectionVO> eventSemenCollectionVOS = pigSemenCollectionMapper.selectEventId(id);
        return eventSemenCollectionVOS;

    }

    @Override
    public List<EventBoarDetailVO> eventSemenAppDetail(Long id) {
        List<EventBoarDetailVO> eventDetailVOS = new ArrayList<>();
        List<EventBoarDetailVO> eventSemenCollectionVOS = pigSemenCollectionMapper.selectEventById(id);
        //List<EventBoarDetailVO> eventBoarDetailVOS = pigMatingMapper.selectEventByBoarId(id);
        eventDetailVOS.addAll(eventSemenCollectionVOS);
       // eventDetailVOS.addAll(eventBoarDetailVOS);
        //先按事件发生的时间排序
        eventDetailVOS.sort(Comparator.comparing(EventBoarDetailVO::getCreateTime).reversed());
        return eventDetailVOS;
    }

    @Override
    public PageInfo<EventPigBreedingListVO> event(QueryPig queryPigBreeding) {
        PageHelper.startPage(queryPigBreeding.getPage(), queryPigBreeding.getSize());
        List<EventPigBreedingListVO> eventPigBreedingListVOS = baseMapper.event(queryPigBreeding);
        return PageInfo.of(eventPigBreedingListVOS);
    }

    @Override
    public PageInfo<PigBreedingListWebVO> selectByEarNumber(QueryPig queryPigBreeding) {
        PageHelper.startPage(queryPigBreeding.getPage(), queryPigBreeding.getSize());
        List<PigBreedingListWebVO> eventPigBreedingListVOS = baseMapper.selectByEarNumber(queryPigBreeding);
        return PageInfo.of(eventPigBreedingListVOS);
    }

    @Override
    public PageInfo<PigBreedingArchivesListVO> pageArchives(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<PigBreedingArchivesListVO> eventPigBreedingListVOS = baseMapper.selectByTypeId(queryPig);
        return PageInfo.of(eventPigBreedingListVOS);
    }

    @Override
    public List<PigBreedingBoarListVO> boarList() {
        List<PigBreedingBoarListVO> pigs = baseMapper.boarList();
        return pigs;
    }

    @Override
    public List<PigBreedingListWebVO> selectListByEarNumber(QueryPig queryPig) {
        List<PigBreedingListWebVO> eventPigBreedingListVOS = baseMapper.selectByEarNumber(queryPig);
        return eventPigBreedingListVOS;
    }

    @Override
    public List<EventPrintPigBreedingVO> eventPrintDetail(Long id) {
        List<EventPrintPigBreedingVO> eventPrintPigBreedingVOS = new ArrayList<>();
        //查询配种信息
        List<EventMatingVO> matingVOS = pigMatingMapper.selectEventById(id);
        //查询妊娠信息
        List<EventPregnancyVO> pregnancyVOS = pigPregnancyMapper.selectEventId(id);
        //查询分娩记录
        List<EventLaborVO> eventLaborVOS = pigLaborMapper.selectEventId(id);
        //查询断奶记录
        List<EventWeanedVO> eventWeanedVO = pigWeanedMapper.selectEventId(id);

        for (EventMatingVO matingVO : matingVOS) {
            EventPrintPigBreedingVO eventPrintPigBreedingVO = new EventPrintPigBreedingVO();
            eventPrintPigBreedingVO.setMatingDate(matingVO.getMatingDate());
            eventPrintPigBreedingVO.setParity(matingVO.getParity());
            eventPrintPigBreedingVO.setCreateTime(matingVO.getCreateTime());
            eventPrintPigBreedingVOS.add(eventPrintPigBreedingVO);
        }

        for (EventPregnancyVO eventPregnancyVO : pregnancyVOS) {
            EventPrintPigBreedingVO eventPrintPigBreedingVO = new EventPrintPigBreedingVO();
            eventPrintPigBreedingVO.setPregnancyDate(eventPregnancyVO.getPregnancyDate());
            eventPrintPigBreedingVO.setPregnancyResult(eventPregnancyVO.getPregnancyResult());
            eventPrintPigBreedingVO.setParity(eventPregnancyVO.getParity());
            eventPrintPigBreedingVO.setCreateTime(eventPregnancyVO.getCreateTime());
            eventPrintPigBreedingVOS.add(eventPrintPigBreedingVO);
        }

        for (EventLaborVO eventLaborVO : eventLaborVOS) {
            EventPrintPigBreedingVO eventPrintPigBreedingVO = new EventPrintPigBreedingVO();
            eventPrintPigBreedingVO.setLaborDate(eventLaborVO.getLaborDate());
            eventPrintPigBreedingVO.setParity(eventLaborVO.getParity());
            Integer liveNumber = eventLaborVO.getHealthyNumber() + eventLaborVO.getWeakNumber() + eventLaborVO.getDeformityNumber();
            eventPrintPigBreedingVO.setLiveNumber(liveNumber);
            eventPrintPigBreedingVO.setCreateTime(eventLaborVO.getCreateTime());
            eventPrintPigBreedingVOS.add(eventPrintPigBreedingVO);
        }

        for (EventWeanedVO weanedVO : eventWeanedVO) {
            EventPrintPigBreedingVO eventPrintPigBreedingVO = new EventPrintPigBreedingVO();
            eventPrintPigBreedingVO.setWeanedDate(weanedVO.getWeanedDate());
            eventPrintPigBreedingVO.setWeanedNumber(weanedVO.getWeanedNumber());
            eventPrintPigBreedingVO.setParity(weanedVO.getParity());
            eventPrintPigBreedingVO.setCreateTime(weanedVO.getCreateTime());
            eventPrintPigBreedingVOS.add(eventPrintPigBreedingVO);
        }
        eventPrintPigBreedingVOS.sort(Comparator.comparing(EventPrintPigBreedingVO::getCreateTime).reversed());
        return eventPrintPigBreedingVOS;
    }

    @Override
    public List<PigBreedingLoseVo> pigBreedingLoseList() {
        return breedingMapper.pigBreedingLoseList();
    }


    //---------------------------------------------------------------------------------------------------------------//


    @Override
    public List<Pig> chosePig(String keyword, Integer type) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        //后备、怀孕、哺乳、断奶、空怀、在场、其他
        List<Long> status = Arrays.asList(502978L, 502979L, 502980L, 502981L, 502982L, 502986L, 503000L);
        return page(keyword, type, farmId, status, 1, 10);
    }

    @Override
    public Set<SimplePigVo> search(ResourceType source, String keyword) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        if (ResourceType.JX.equals(source)) {
            return cacheJxPig(farmId, keyword).stream().limit(6).collect(toSet());
        } else if (ResourceType.YHY.equals(source)) {
            //todo
        }

        return null;
    }

    @Override
    public List<PigBreeding> findByCol(Long colId) {
        return baseMapper.selectList(Wrappers.lambdaQuery(PigBreeding.class).eq(PigBreeding::getPigHouseColumnsId, colId)
                .orderByAsc(PigBreeding::getEarNumber));
    }

    @Override
    public List<PigBreeding> findByHouse(Long houseId) {
        return baseMapper.selectList(Wrappers.lambdaQuery(PigBreeding.class).eq(PigBreeding::getPigHouseId, houseId)
                .orderByAsc(PigBreeding::getEarNumber));
    }

    @Override
    public Optional<Pig> findPig(Long id) {
        if (ObjectUtil.isNull(id)) {
            return Optional.empty();
        }
        PigBreeding breeding = baseMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(breeding)) {
            PigMating mating = pigMatingMapper.selectByPigBreedingId(id);
            return Optional.of(Pig.wrap(breeding, mating, null));
        }
        RBucket<Pig> bucket = redis.getBucket(CacheKey.Web.sph_pig.key + id);
        Pig pig;
        if (!bucket.isExists()) {
            pig = findByJx(id);
            bucket.set(pig);
            bucket.expire(CacheKey.Web.sph_pig.duration);
        } else {
            pig = bucket.get();
        }
        return Optional.of(pig);
    }

    private Pig findByJx(Long pigId) {
        String sql = "select a.ID_KEY, a.Z_OVERBIT, a.Z_ONE_NO, a.Z_SEX,\n" +
                "       a.Z_BIRTHDAY, a.Z_ARRIVE_DATE, a.Z_BREED, a.Z_STRAIN, a.M_ORG_ID, a.Z_ORG_ID,\n" +
                "       a.Z_DQ_DORM, a.Z_DQ_STATUS, a.Z_DQ_YCTS, a.Z_DQ_TC, a.Z_DQ_QQ, a.Z_IF_LC, a.Z_LC_DATE,\n" +
                "       a.Z_LC_FS, a.Z_FIRST_PZDT, a.Z_OFFSPRING_ID, a.Z_PIG_TYPE, bd.Z_BREED_DATE\n" +
                "from (select ID_KEY, Z_ONE_NO, Z_OVERBIT, Z_SEX, to_char(Z_BIRTHDAY, 'yyyy-MM-dd') Z_BIRTHDAY,\n" +
                "           Z_ARRIVE_DATE, Z_BREED, Z_STRAIN, Z_DQ_DORM, Z_DQ_STATUS, Z_DQ_YCTS, Z_DQ_TC, Z_DQ_QQ,\n" +
                "           Z_IF_LC, Z_LC_DATE, Z_LC_FS, Z_FIRST_PZDT, Z_ORG_ID, M_ORG_ID, Z_OFFSPRING_ID, Z_PIG_TYPE\n" +
                "     FROM ZLT_ORIGINAL_ARCHIVES where ID_KEY = " + pigId + " or Z_OFFSPRING_ID = " + pigId + "\n" +
                "    union all\n" +
                "    select ID_KEY, Z_ONE_NO, Z_OVERBIT, Z_SEX, to_char(Z_BIRTHDAY, 'yyyy-MM-dd') Z_BIRTHDAY,\n" +
                "           Z_ARRIVE_DATE, Z_BREED, Z_STRAIN, Z_DQ_DORM, Z_DQ_STATUS, Z_DQ_YCTS, Z_DQ_TC, Z_DQ_QQ,\n" +
                "           Z_IF_LC, Z_LC_DATE, Z_LC_FS, null Z_FIRST_PZDT, Z_ORG_ID, M_ORG_ID, null Z_OFFSPRING_ID, 503600 Z_PIG_TYPE\n" +
                "    from zlt_offspring_archives where ID_KEY = " + pigId + " and ID_KEY not in (select arc.Z_OFFSPRING_ID from ZLT_ORIGINAL_ARCHIVES arc where arc.Z_OFFSPRING_ID is not null)) a\n" +
                "left join (SELECT m.Z_ZZDA_ID, l.Z_BREED_DATE, row_number() over(partition by m.Z_ZZDA_ID ORDER BY l.Z_BREED_DATE DESC ) as rn \n" +
                "            from zlt_breed_note_m m left join zlt_breed_note_l l on m.ID_KEY = l.VOU_ID where m.AUDIT_MARK <> 0) bd on a.ID_KEY = bd.Z_ZZDA_ID and bd.rn = 1";
        return jxJdbcTemplate.queryForObject(sql, (rs, i) -> {
            Pig pig = new Pig();
            pig.setSource(ResourceType.JX);
            pig.setId(rs.getLong("ID_KEY"));
            pig.setEarNumber(rs.getString("Z_OVERBIT"));
            pig.setIndividualNumber(rs.getString("Z_ONE_NO"));
            pig.setSex(StrUtils.parseInt(rs.getString("Z_SEX")));
            pig.setBornDate(StrUtils.parseLocalDate(rs.getString("Z_BIRTHDAY")));
            pig.setFarmId(StrUtils.parseLong(rs.getString("Z_ORG_ID")));
            pig.setHouseId(StrUtils.parseLong(rs.getString("Z_DQ_DORM")));
            pig.setStatusId(StrUtils.parseLong(rs.getString("Z_DQ_STATUS")));
            pig.setParity(rs.getInt("Z_DQ_TC"));
            pig.setPigTypeId(StrUtils.parseLong(rs.getString("Z_PIG_TYPE")));
            pig.setBreedDate(rs.getString("Z_BREED_DATE"));
            if (ObjectUtil.isNotEmpty(pig.getBreedDate())) {
                pig.setStage((int) DateUtil.between(DateUtil.parseDate(pig.getBreedDate()), new Date(), DateUnit.DAY));
            }
            PigBackFat fat = backFatService.lastBackFat(pig.getId());
            if (ObjectUtil.isNotEmpty(fat)) {
                pig.setBackFat(fat.getBackFat());
                pig.setBackFatStage(fat.getStage());
                pig.setBackFatDate(fat.getCreateTime());
            }
            return pig;
        });
    }

    @Override
    public void bindToField(ResourceType source, Long colId, Long pigId) {
        if (source.equals(ResourceType.JX)) {
            findPig(pigId).ifPresent(pig -> {
                //解绑历史栏位
                this.moveOutForField(source, pigId);
                //重新绑定
                PigHouseColumns col = columnsMapper.selectById(colId);
                col.setPigId(pigId);
                columnsMapper.updateById(col);
            });
        } else if (source.equals(ResourceType.YHY)) {
            bindYhyPig(source, colId, pigId);
        }
    }

    private void bindYhyPig(ResourceType source, Long colId, Long pigId) {
        Long userId = RequestContextUtils.getUserId();
        //栏位原有猪只解绑
        Optional<Pig> opt = columnsService.findByCol(colId);
        opt.ifPresent(pig -> moveOutForField(source, pig.getId()));

        PigBreedingChangeHouseDTO dto = new PigBreedingChangeHouseDTO();
        dto.setOperatorId(userId);
        dto.setRemark("扫码绑定猪只调栏");
        PigBreedingChangeHouseDetailDTO detailDTO = new PigBreedingChangeHouseDetailDTO();
        detailDTO.setHouseColumnsInId(colId);
        detailDTO.setPigBreedingId(pigId);
        dto.setList(Collections.singletonList(detailDTO));
        dto.setChangeHouseTime(new Date());
        pigBreedingChangeHouseService.change(dto);
    }

    @Override
    public void moveOutForField(ResourceType source, Long pigId) {
        if (source.equals(ResourceType.JX)) {
            PigHouseColumns col = columnsMapper.selectOne(
                    Wrappers.lambdaQuery(PigHouseColumns.class).eq(PigHouseColumns::getPigId, pigId));
            if (ObjectUtil.isNotEmpty(col)) {
                col.setPigId(null);
                columnsMapper.updateById(col);
            }
        } else if (source.equals(ResourceType.YHY)) {
            PigBreeding pig = baseMapper.selectById(pigId);
            if (ObjectUtil.isNotEmpty(pig.getPigHouseColumnsId())) {
                pig.setPigHouseColumnsId(null);
                baseMapper.updateById(pig);
                pigBreedingChangeHouseService.change(pig);
            }
        }
    }


    @Override
    public void moveOutForField(ResourceType source, Long colId, Long pigId) {
        if (source.equals(ResourceType.JX)) {
            PigHouseColumns col = columnsMapper.selectOne(
                    Wrappers.lambdaQuery(PigHouseColumns.class).eq(PigHouseColumns::getPigId, pigId));
            if (ObjectUtil.isNotEmpty(col)) {
                col.setPigId(null);
                columnsMapper.updateById(col);
            }
        } else if (source.equals(ResourceType.YHY)) {
            PigBreeding pig = baseMapper.selectById(pigId);
            if (ObjectUtil.isNotEmpty(pig.getPigHouseColumnsId())) {
                pig.setPigHouseColumnsId(null);
                baseMapper.updateById(pig);
            }
        }
    }

    @Override
    public void batchBind(ResourceType source, List<BatchBindDto> dtos) {
        Long userId = RequestContextUtils.getUserId();
        String key = ColumnOperateType.exactTransferPig.key() + userId;
        dtos.stream()
                .filter(param -> ObjectUtil.isAllNotEmpty(param.getColId(), param.getPigId()))
                .forEach(param -> bindToField(source, param.getColId(), param.getPigId()));
        redis.getBucket(key).delete();
    }

    private Set<SimplePigVo> cacheOtherPig(Long farmId, String earNumber) {
        RSet<SimplePigVo> simplePigVos = redis.getSet(CacheKey.Web.simple_pig.key + farmId);
        if (simplePigVos.isEmpty() ||
                simplePigVos.stream().noneMatch(s -> ObjectUtil.isNotEmpty(earNumber) && s.getEarNumber().contains(earNumber))) {
            LambdaQueryWrapper<PigBreeding> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PigBreeding::getPigFarmId, farmId);
            wrapper.eq(PigBreeding::getPresenceStatus, 1);
            wrapper.like(PigBreeding::getEarNumber, earNumber);
            wrapper.eq(PigBreeding::isDel, 0);
            List<PigBreeding> pigs = baseMapper.selectList(wrapper);
            Set<SimplePigVo> vos = pigs.stream()
                    .map(p -> SimplePigVo.builder().pigId(p.getId()).earNumber(p.getEarNumber()).build())
                    .collect(toSet());
            simplePigVos.addAll(vos);
            simplePigVos.expire(CacheKey.Web.simple_pig.duration);
        }
        return simplePigVos;
    }

    private List<Pig> page(String keyword, Integer type, Long farmId, List<Long> status, Integer page, Integer size) {
        String sql = "select a.ID_KEY pigId, a.Z_OVERBIT earNumber, a.Z_ONE_NO individualNumber, a.Z_SEX sex,\n" +
                "       a.Z_BIRTHDAY bornDate, a.Z_DQ_TC currParities, a.Z_DQ_YCTS ycts,\n" +
                "       ROUND(TO_NUMBER(sysdate - a.Z_BIRTHDAY)) ageOfDay, bl.ID_KEY statusId, bl.Z_VALUE status,\n" +
                "       bf.Z_SOURCE backFat, bf.Z_DATE,\n" +
                "       a.Z_BREED varietyId, vl.Z_BREED_NM variety, a.Z_STRAIN strainId,\n" +
                "       a.Z_ORG_ID farmId, a.Z_DQ_DORM houseId, h.Z_ITEM_NM houseName,\n" +
                "       a.Z_OFFSPRING_ID, a.Z_PIG_TYPE pigTypeId\n" +
                "from (select ID_KEY, Z_ONE_NO, Z_OVERBIT, Z_SEX, Z_BIRTHDAY,\n" +
                "           Z_ARRIVE_DATE, Z_BREED, Z_STRAIN, Z_DQ_DORM, Z_DQ_STATUS, Z_DQ_YCTS, Z_DQ_TC, Z_DQ_QQ,\n" +
                "           Z_IF_LC, Z_LC_DATE, Z_LC_FS, Z_FIRST_PZDT, Z_ORG_ID, M_ORG_ID, Z_OFFSPRING_ID, Z_PIG_TYPE\n" +
                "     FROM ZLT_ORIGINAL_ARCHIVES where Z_ORG_ID = " + farmId + "\n" +
                "    union all\n" +
                "    select ID_KEY, Z_ONE_NO, Z_OVERBIT, Z_SEX, Z_BIRTHDAY,\n" +
                "           Z_ARRIVE_DATE, Z_BREED, Z_STRAIN, Z_DQ_DORM, Z_DQ_STATUS, Z_DQ_YCTS, Z_DQ_TC, Z_DQ_QQ,\n" +
                "           Z_IF_LC, Z_LC_DATE, Z_LC_FS, null Z_FIRST_PZDT, Z_ORG_ID, M_ORG_ID, null Z_OFFSPRING_ID, 503600 Z_PIG_TYPE\n" +
                "    from zlt_offspring_archives where ID_KEY not in (select arc.Z_OFFSPRING_ID from ZLT_ORIGINAL_ARCHIVES arc where arc.Z_OFFSPRING_ID is not null) and Z_ORG_ID = " + farmId + ") a\n" +
                "left join jc.zlt_base_lov bl on a.Z_DQ_STATUS = bl.ID_KEY\n" +
                "left join jc.jct_pig_farm h on a.Z_DQ_DORM = h.ID_KEY\n" +
                "left join ZLT_ZZ_VARIETIES_LOV vl on a.Z_BREED = vl.ID_KEY\n" +
                "left join (SELECT a.*,row_number() over(partition by a.Z_ZZDA_ID ORDER BY a.Z_DATE DESC ) as rn from zlt_backfat_note a ) bf on a.ID_KEY = bf.Z_ZZDA_ID and bf.rn = 1\n" +
                "where 1=1";

        if (ObjectUtil.isNotEmpty(keyword)) {
            sql += " and (a.Z_OVERBIT like '%" + keyword + "%' or a.Z_ONE_NO like '%" + keyword + "%')\n";
        }
        if (ObjectUtil.isNotEmpty(type)) {
            sql += " and a.Z_PIG_TYPE = " + type + "\n";
        }
        if (ObjectUtil.isNotEmpty(status)) {
            String statusStr = status.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql += " and a.Z_DQ_STATUS in (" + statusStr + ")";
        }
        sql += " order by a.Z_ONE_NO asc";
        Page pageModel = PageHelper.startPage(page, size);
        String allSql = "SELECT * FROM (SELECT tt.*, ROWNUM AS rowno FROM (" + sql + ") tt WHERE ROWNUM <= " + pageModel.getEndRow() + ") table_alias WHERE table_alias.rowno > " + pageModel.getStartRow();
        return jxJdbcTemplate.query(allSql, (rs, i) -> {
            Pig vo = new Pig();
            vo.setSource(ResourceType.JX);
            vo.setId(StrUtils.parseLong(rs.getString("pigId")));
            vo.setEarNumber(rs.getString("earNumber"));
            vo.setIndividualNumber(rs.getString("individualNumber"));
            vo.setSex(StrUtils.parseInt(rs.getString("sex")));
            vo.setBornDate(ObjectUtil.isEmpty(rs.getDate("bornDate")) ? null : rs.getDate("bornDate").toLocalDate());
            vo.setAgeOfDay(StrUtils.parseInt(rs.getString("ageOfDay")));
            vo.setParity(rs.getInt("currParities"));
            vo.setStatusId(StrUtils.parseLong(rs.getString("statusId")));
            vo.setStatus(rs.getString("status"));
            vo.setBackFat(rs.getInt("backFat"));
            vo.setVarietyId(StrUtils.parseLong(rs.getString("varietyId")));
            vo.setVariety(rs.getString("variety"));
            vo.setStrainId(StrUtils.parseLong(rs.getString("strainId")));
            vo.setFarmId(StrUtils.parseLong(rs.getString("farmId")));
            vo.setHouseId(StrUtils.parseLong(rs.getString("houseId")));
            vo.setHouseName(rs.getString("houseName"));
            vo.setPigTypeId(StrUtils.parseLong(rs.getString("pigTypeId")));
            return vo;
        });
    }

    private RSet<SimplePigVo> cacheJxPig(Long farmId) {
        RSet<SimplePigVo> simplePigs = redis.getSet(CacheKey.Web.simple_pig.key + farmId);
        if (ObjectUtil.isEmpty(simplePigs)) {
            String sql = "select a.ID_KEY pigId, a.Z_OVERBIT earNumber, a.Z_ONE_NO individualNumber, a.Z_OFFSPRING_ID\n" +
                    "from (select ID_KEY, Z_ONE_NO, Z_OVERBIT, Z_OFFSPRING_ID FROM ZLT_ORIGINAL_ARCHIVES where Z_ORG_ID = " + farmId + "\n" +
                    "    union all\n" +
                    "    select ID_KEY, Z_ONE_NO, Z_OVERBIT, null Z_OFFSPRING_ID from zlt_offspring_archives where ID_KEY not in (select arc.Z_OFFSPRING_ID from ZLT_ORIGINAL_ARCHIVES arc where arc.Z_OFFSPRING_ID is not null) and Z_ORG_ID = " + farmId + ") a\n" +
                    "where 1=1";
            simplePigs.addAll(jxJdbcTemplate.query(sql, (rs, i) -> {
                SimplePigVo vo = new SimplePigVo();
                vo.setPigId(StrUtils.parseLong(rs.getString("pigId")));
                vo.setEarNumber(rs.getString("earNumber"));
                vo.setIndividualNumber(rs.getString("individualNumber"));
                return vo;
            }));
            simplePigs.expire(CacheKey.Web.simple_pig.duration);
        }
        return simplePigs;
    }

    private Set<SimplePigVo> cacheJxPig(Long farmId, String individualNumber) {
        if (ObjectUtil.isEmpty(individualNumber)) {
            return null;
        }
        RSet<SimplePigVo> simplePigVos = cacheJxPig(farmId);
        Set<SimplePigVo> exists = simplePigVos.stream().filter(p -> p.getIndividualNumber().contains(individualNumber)).collect(toSet());
        if (ObjectUtil.isEmpty(exists)) {
            String base = "select a.ID_KEY pigId, a.Z_OVERBIT earNumber, a.Z_ONE_NO individualNumber, a.Z_OFFSPRING_ID\n" +
                    "from (select ID_KEY, Z_ONE_NO, Z_OVERBIT, Z_OFFSPRING_ID FROM ZLT_ORIGINAL_ARCHIVES where Z_ORG_ID = " + farmId + "\n" +
                    "    union all\n" +
                    "    select ID_KEY, Z_ONE_NO, Z_OVERBIT, null Z_OFFSPRING_ID from zlt_offspring_archives where ID_KEY not in (select arc.Z_OFFSPRING_ID from ZLT_ORIGINAL_ARCHIVES arc where arc.Z_OFFSPRING_ID is not null) and Z_ORG_ID = " + farmId + ") a\n" +
                    "where 1=1 and a.Z_ONE_NO like '%" + individualNumber + "%'";
            List<SimplePigVo> vos = jxJdbcTemplate.query(base, (rs, i) -> {
                SimplePigVo vo = new SimplePigVo();
                vo.setPigId(StrUtils.parseLong(rs.getString("pigId")));
                vo.setEarNumber(rs.getString("earNumber"));
                vo.setIndividualNumber(rs.getString("individualNumber"));
                return vo;
            });
            simplePigVos.addAll(vos);
            return new HashSet<>(vos);
        } else {
            return exists;
        }
    }

}
