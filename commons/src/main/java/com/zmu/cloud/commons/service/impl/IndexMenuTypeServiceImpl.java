package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.app.IndexMenuTypeQuery;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.utils.ZmDateUtil;
import com.zmu.cloud.commons.utils.ZmMathUtil;
import com.zmu.cloud.commons.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author YH
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IndexMenuTypeServiceImpl extends ServiceImpl<IndexMenuTypeMapper, IndexMenuType> implements IndexMenuTypeService {

    private final IndexMenuUserTypeMapper indexMenuUserTypeMapper;
    private final PigHouseService houseService;
    private final PigHouseColumnsMapper columnsMapper;
    final GatewayService gatewayService;
    final ReportService reportService;
    final TowerService towerService;
    final TowerLogService towerLogService;
    final FourGService fourGService;
    final FeedTowerLogMapper logMapper;
    final FeedTowerMapper towerMapper;
    final PigPorkStockMapper pigPorkStockMapper;



    @Override
    public IndexMenuType getById(Long id) {
        return super.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(IndexMenuType indexMenuType) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        indexMenuType.setCreateTime(LocalDateTime.now());
        indexMenuType.setCreateBy(info.getUserId());
        save(indexMenuType);
        return indexMenuType.getId();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(IndexMenuType indexMenuType) {
        indexMenuType.setUpdateBy(RequestContextUtils.getRequestInfo().getUserId());
        return baseMapper.updateById(indexMenuType) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        int count = indexMenuUserTypeMapper.countUserByTypeId(id);
        if (count > 0) {
            throw new BaseException("该首页菜单类型已分配给 " + count + " 个用户，请先解除关联后再删除");
        }
        RequestInfo info = RequestContextUtils.getRequestInfo();
        IndexMenuType indexMenuType = getById(id);
        indexMenuType.setUpdateBy(info.getUserId());
        LambdaUpdateWrapper<IndexMenuType> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(IndexMenuType::getUpdateBy, info.getUserId())
                .set(IndexMenuType::getDel, true)
                .eq(IndexMenuType::getId, id);
        return update(lambdaUpdateWrapper) && indexMenuUserTypeMapper.deleteByTypeId(id) > 0;

    }

    @Override
    public PageInfo<IndexMenuType> typeList(IndexMenuTypeQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(baseMapper.listIndexMenuType(query));
    }

    @Override
    public void saveMyAppMenu(List<Long> menuIds, ZmuApp zmuApp) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        indexMenuUserTypeMapper.deleteByUserId(info.getUserId(), zmuApp.name());
        for(Long menuId: menuIds){
            IndexMenuUserType indexMenuUserType = new IndexMenuUserType();
            indexMenuUserType.setUserId(info.getUserId());
            indexMenuUserType.setTypeId(menuId);
            indexMenuUserType.setApp(zmuApp.name());
            indexMenuUserTypeMapper.insert(indexMenuUserType);
        }
    }

    @Override
    public List<IndexMenuTypeVO> listCurrent(ZmuApp zmuApp) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        List<IndexMenuTypeVO> indexMenuTypeVOS;
        if (info.getUserRoleType() == UserRoleTypeEnum.SUPER_ADMIN) {
            indexMenuTypeVOS = baseMapper.listIndexMenuTypeByUserAndApp(null,zmuApp,null);
        } else {
            indexMenuTypeVOS = baseMapper.listIndexMenuTypeByUserAndApp(info.getUserId(),zmuApp,ResourceType.JX.equals(info.getResourceType()) ? "jx" : "yhy");
        }
        indexMenuTypeVOS.forEach(oneMenu->{
            if(IndexMenuTypeEnum.Tower.getType().equals(oneMenu.getMenuTypeKey())){
                LambdaQueryWrapper<FeedTower> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.ne(FeedTower::getTemp,1);
                List<FeedTower> towers = towerMapper.selectList(queryWrapper);
                oneMenu.setTowerTotal(towers.size());
                oneMenu.setMachineTotal(towers.stream().filter(tower -> ObjectUtils.isNotEmpty(tower.getDeviceNo())).count());
            }else if (IndexMenuTypeEnum.Feeder.getType().equals(oneMenu.getMenuTypeKey())){
                long count = columnsMapper.selectCount(Wrappers.lambdaQuery(PigHouseColumns.class)
                        .gt(PigHouseColumns::getClientId, 0).gt(PigHouseColumns::getFeederCode, 0));
                oneMenu.setMachineTotal(count);
            }else if (IndexMenuTypeEnum.FourGCard.getType().equals(oneMenu.getMenuTypeKey())){
                try {
                    PigFarm4GCardInfoVO cards = fourGService.getFarmCards(info.getPigFarmId(), null);
                    oneMenu.setCardNum(cards.getCards()==null?0:cards.getCards().size());
                }catch (Exception e){
                    log.error("查询卡片数量第三方接口失败!",e);
                    oneMenu.setCardNum(0);
                }
            }else if (IndexMenuTypeEnum.Production.getType().equals(oneMenu.getMenuTypeKey())){
                try {
                    HomeStatisticsSummaryVO homeStatisticsSummaryVO = pigPorkStockMapper.statisticsSummaryVO(info.getPigFarmId());
                    oneMenu.setPigTotal(homeStatisticsSummaryVO.getBoar() + homeStatisticsSummaryVO.getSow());
                }catch (Exception e){
                    oneMenu.setPigTotal(0);
                }
            }
        });
       return indexMenuTypeVOS;
    }


    @Override
    public IndexMenuFeederDataShowVO appIndexMenuFeederDataShow() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        IndexMenuFeederDataShowVO indexMenuFeederDataShowVO = new IndexMenuFeederDataShowVO();

        //配怀饲喂器数量
        List<PigHouse> all = houseService.list(Wrappers.lambdaQuery(PigHouse.class).eq(PigHouse::getPigFarmId, info.getPigFarmId()));
        List<Long> ids = all.stream()
                .filter(h -> Arrays.asList(HouseTypeForSph.SPH_PZ.getType(), HouseType.PZ.getType()).contains(h.getType()))
                .map(PigHouse::getId).collect(Collectors.toList());
        long count = 0;
        if (CollUtil.isNotEmpty(ids)) {
            count = columnsMapper.selectCount(Wrappers.lambdaQuery(PigHouseColumns.class)
                    .in(PigHouseColumns::getPigHouseId, ids)
                    .gt(PigHouseColumns::getClientId, 0).gt(PigHouseColumns::getFeederCode, 0));
        }

        indexMenuFeederDataShowVO.setPh(count);

        //后备饲喂器数量
        indexMenuFeederDataShowVO.setHb(0);

        //分娩饲喂器数量
        indexMenuFeederDataShowVO.setFm(0);


        //公猪饲喂器数量
        ids = all.stream()
                .filter(h -> Arrays.asList(HouseTypeForSph.SPH_GZ.getType(), HouseType.GZ.getType()).contains(h.getType()))
                .map(PigHouse::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(ids)) {
            count = columnsMapper.selectCount(Wrappers.lambdaQuery(PigHouseColumns.class)
                    .in(PigHouseColumns::getPigHouseId, ids)
                    .gt(PigHouseColumns::getClientId, 0).gt(PigHouseColumns::getFeederCode, 0));
        }
        indexMenuFeederDataShowVO.setGz(count);

        //保育饲喂器数量
        indexMenuFeederDataShowVO.setBy(0);

        List<Integer> houseTypes = ListUtil.of(HouseTypeForSph.SPH_PZ.getType(),
                HouseTypeForSph.SPH_HB.getType(),
                HouseTypeForSph.SPH_FM.getType(), HouseTypeForSph.SPH_GZ.getType(), HouseTypeForSph.SPH_BY.getType());
        //今日饲喂量
        FeedReportVo reportsVoDay = reportService.ymdFeedingAmountReport(houseTypes, ReportType.day, System.currentTimeMillis());
        reportsVoDay.getData().forEach(oneData->{
            int houseTypeNo = oneData.getHouseType();
            switch (houseTypeNo) {
                case 531179:
                    indexMenuFeederDataShowVO.setSph(ZmMathUtil.kgToTString(oneData.getAmounts()));
                    indexMenuFeederDataShowVO.setSphPercent(oneData.getPercentage());
                    break;
                case 500289:
                    indexMenuFeederDataShowVO.setShb(ZmMathUtil.kgToTString(oneData.getAmounts()));
                    indexMenuFeederDataShowVO.setShbPercent(oneData.getPercentage());
                    break;
                case 500286:
                    indexMenuFeederDataShowVO.setSfm(ZmMathUtil.kgToTString(oneData.getAmounts()));
                    indexMenuFeederDataShowVO.setSfmPercent(oneData.getPercentage());
                    break;
                case 513419:
                    indexMenuFeederDataShowVO.setSgz(ZmMathUtil.kgToTString(oneData.getAmounts()));
                    indexMenuFeederDataShowVO.setSgzPercent(oneData.getPercentage());
                    break;
                case 500287:
                    indexMenuFeederDataShowVO.setSby(ZmMathUtil.kgToTString(oneData.getAmounts()));
                    indexMenuFeederDataShowVO.setSbyPercent(oneData.getPercentage());
                    break;
            }
        });
        //年度总饲喂量
        FeedReportVo reportsVoYear = reportService.ymdFeedingAmountReport(houseTypes, ReportType.year, System.currentTimeMillis());
        indexMenuFeederDataShowVO.setYs(ZmMathUtil.kgToTString(reportsVoYear.getTotal()));
        return indexMenuFeederDataShowVO;
    }

    @Override
    public IndexMenuTowerDataShowVO appIndexMenuTowerDataShow() {
        Date now = new Date();
        IndexMenuTowerDataShowVO indexMenuTowerDataShowVO = new IndexMenuTowerDataShowVO();
        //时间线
        String timeLine = ZmDateUtil.getTimeLine(new Date(), DateUtil.offsetDay(new Date(), -6));
        indexMenuTowerDataShowVO.setTimeLine(timeLine);
        List<FeedTower> towers = towerMapper.selectList(Wrappers.emptyWrapper());
        if (CollUtil.isEmpty(towers)) {
            return indexMenuTowerDataShowVO;
        }
        List<Long> towerIds = towers.stream().map(FeedTower::getId).collect(Collectors.toList());

        //猪场下所有料塔的当日用料和补料
        indexMenuTowerDataShowVO.setTodayUse(ZmMathUtil.gToTString(towerService.getOneTowerTodayUseORAnd(towerIds, TowerLogStatusEnum.USE)));
        indexMenuTowerDataShowVO.setTodayAdd(ZmMathUtil.gToTString(towerService.getOneTowerTodayUseORAnd(towerIds,TowerLogStatusEnum.ADD)));


        //猪场下所有料塔的昨日用料
        indexMenuTowerDataShowVO.setYesterdayUse(ZmMathUtil.gToTString(towerService.getTowerOneDayUseORAnd(towerIds, DateUtil.yesterday(),TowerLogStatusEnum.USE)));
        indexMenuTowerDataShowVO.setYesterdayAdd(ZmMathUtil.gToTString(towerService.getTowerOneDayUseORAnd(towerIds, DateUtil.yesterday(),TowerLogStatusEnum.ADD)));




        //猪场下所有料塔本年度用料和补料

        List<TowerLogReportVo> useVos = logMapper.getTowerMonthsOfYearUseORAnd(towerIds, TowerLogStatusEnum.USE);
        List<TowerLogReportVo> addVos = logMapper.getTowerMonthsOfYearUseORAnd(towerIds, TowerLogStatusEnum.ADD);
        //当月用料明细和统计
        Optional<TowerLogReportVo> optVo = useVos.stream().filter(vo -> vo.getDayStr().equals(DateUtil.format(now, DatePattern.NORM_MONTH_PATTERN))).findFirst();
        Optional<TowerLogReportVo> optAddVo = addVos.stream().filter(vo -> vo.getDayStr().equals(DateUtil.format(now, DatePattern.NORM_MONTH_PATTERN))).findFirst();
        indexMenuTowerDataShowVO.setCurrentMonthUse(ZmMathUtil.gToTString(optVo.isPresent()?optVo.get().getVariation():0));
        //猪场下所有料塔的上月用料
        Optional<TowerLogReportVo> lastMonthUseOpt = useVos.stream().filter(vo -> vo.getDayStr().equals(DateUtil.format(DateUtil.lastMonth(), DatePattern.NORM_MONTH_PATTERN))).findFirst();
        indexMenuTowerDataShowVO.setLastMonthUse(ZmMathUtil.gToTString(lastMonthUseOpt.isPresent()?lastMonthUseOpt.get().getVariation():0));
        //上月补料
        Optional<TowerLogReportVo> lastMonthAddOpt = addVos.stream().filter(vo -> vo.getDayStr().equals(DateUtil.format(DateUtil.lastMonth(), DatePattern.NORM_MONTH_PATTERN))).findFirst();
        indexMenuTowerDataShowVO.setLastMonthAdd(ZmMathUtil.gToTString(lastMonthAddOpt.isPresent()?lastMonthAddOpt.get().getVariation():0));

        indexMenuTowerDataShowVO.setCurrentMonthAdd(ZmMathUtil.gToTString(optAddVo.isPresent()?optAddVo.get().getVariation():0));
        indexMenuTowerDataShowVO.setCurrentAddCount(optAddVo.isPresent()?optAddVo.get().getVariationModifyTimes():0);

        List<DateTime> yearToMonths = DateUtil.rangeToList(DateUtil.beginOfYear(now), DateUtil.endOfYear(now), DateField.MONTH);
        List<TowerLogReportVo> monthsUseData = yearToMonths.stream().map(month -> {
            String mm = DateUtil.format(month, "MM");
            String ym = DateUtil.format(month, DatePattern.NORM_MONTH_PATTERN);
            TowerLogReportVo reportVo = useVos.stream().filter(vo -> vo.getDayStr().equals(ym)).findFirst().orElse(new TowerLogReportVo());
            reportVo.setDayStr(mm);
            reportVo.setSpot(Integer.parseInt(mm));
            reportVo.setVariationString(ZmMathUtil.gToTString(reportVo.getVariation()));
            return reportVo;
        }).sorted(Comparator.comparing(TowerLogReportVo::getSpot)).collect(Collectors.toList());

        indexMenuTowerDataShowVO.setCurrentYearMonthUse(monthsUseData);
//        indexMenuTowerDataShowVO.setCurrentYearMonthAdd(monthsAddData);

        //近一个月用料补料

        List<TowerLogReportVo> towerNearMonthUseORAndList = towerService.getTowerNearWeekUseORAnd(towerIds);

        indexMenuTowerDataShowVO.setNearMonthUse(towerNearMonthUseORAndList);
//        indexMenuTowerDataShowVO.setNearMonthUse(tempList);
        //年度总用料
        Long currentYearUse = indexMenuTowerDataShowVO.getCurrentYearMonthUse().stream().filter(one->one.getVariation() != null).mapToLong(TowerLogReportVo::getVariation).sum();
        indexMenuTowerDataShowVO.setCurrentYearUse(ZmMathUtil.gToTString(currentYearUse));

        //料塔余料之和
        Long remain = towers.stream().map(FeedTower::getResidualWeight).filter(ObjectUtils::isNotEmpty).reduce(0L, Long::sum);
        //料塔总料之和
        Long total = towers.stream().map(FeedTower::getInitVolume).filter(ObjectUtils::isNotEmpty).reduce(0L, Long::sum);
        //余料之和占比
        int towerPercentage = 0;
        if (remain > 0) {
            towerPercentage =  ZmMathUtil.getPercent(remain,total);
        }
        indexMenuTowerDataShowVO.setTowerRemain(ZmMathUtil.gToTString(remain));
        indexMenuTowerDataShowVO.setTowerTotal(ZmMathUtil.gToTString(total));
        indexMenuTowerDataShowVO.setTowerPercentage(towerPercentage);
        return indexMenuTowerDataShowVO;
    }
}
