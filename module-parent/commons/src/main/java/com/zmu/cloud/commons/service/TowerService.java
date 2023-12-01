package com.zmu.cloud.commons.service;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.*;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.Enable;
import com.zmu.cloud.commons.enums.MeasureModeEnum;
import com.zmu.cloud.commons.enums.TowerLogStatusEnum;
import com.zmu.cloud.commons.vo.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author YH
 */
public interface TowerService {

    String TOPIC_TX = "/tower/%s/TX";
    String TOPIC_RX = "/tower/%s/RX";
    String TOPIC_REFRESH = "/tower/%s/REFRESH";
    String TOPIC_LOG_OPERATE = "/tower/LOG/OPERATE/";
    String TOPIC_LOG_STATUS = "/tower/LOG/STATUS/";
    String TOPIC_MEASURE_SCHEDULE = "/tower/%s/SCHEDULE";
    String TOPIC_UPGRADE_SCHEDULE = "/tower/%s/UPGRADE_SCHEDULE";
    String TOPIC_STATUS = "/tower/%s/STATUS";

    List<TowerVo> listVo();
    TowerPageData listVoPage(Integer pageNum,Integer pageSize);
    TowerStatusInfoVo towersInfo();
    List<FeedTower> all();
    List<FeedTower> list(String deviceNo, Long farmId, String name, Long myTower);
    Map<Long,List<FeedTower>> listPage(String deviceNo, Long farmId, String name, Long myTower,Integer pageNum,Integer pageSize);
    Optional<FeedTower> find(String deviceNo);

    List<FeedTypeVo> feedTypes();
    @Transactional
    void feedTypeSave(Long id, String name, Double density);
    @Transactional
    void feedTypeDel(Long feedTypeId);
    FeedTower save(TowerDto towerDto);
    void associationHouse(String towerId, String houseIds);
    List<String> defaultCapacity();
    List<String> defaultTimer();
    @Transactional
    void del(Long towerId,Boolean check);
    @Transactional
    void bind(Long towerId, String deviceNo, String wifiAccount, String wifiPwd) throws InterruptedException;
    @Transactional
    void bind(String deviceNo, String towerId, String towerName) throws InterruptedException;
    @Transactional
    void unbind(Long towerId, Boolean check) ;
    @Transactional
    void sendModbusId(Long deviceId, Integer modbusId) throws InterruptedException;
    void openBle(String deviceNo, Enable enable);
    TowerVo detail(Long towerId);
    TowerVo detailIn(Long towerId);
    TowerReportByDayVo oneTimeDetail(Long towerId, Date startTime, Date endTime);

    TowerReportByDayVo reportByTime(Long towerId, String time, String type);


    FeedTowerDevice scanFind(String content);
    List<FeedTowerDevice> search(String deviceNo);
    @Transactional
    void deviceModify(Long deviceId, String name);
    void deviceWifi(Long deviceId, String wifiAccount, String wifiPwd) throws InterruptedException;

    Long getOneTowerTodayUseORAnd(List<Long> towerIds, TowerLogStatusEnum statusEnum);
    Long getTowerOneDayUseORAnd(List<Long> towerIds, Date date, TowerLogStatusEnum statusEnum);
    List<TowerLogReportVo> getTowerTimeLineEveryDayUseORAnd(List<Date> dateList, List<Long> towerIds);
    List<TowerLogReportVo> getTowerNearWeekUseORAnd(List<Long> towerIds);
    List<TowerLogReportVo> getTowerNearMonthUseORAnd(List<Long> towerIds);
    UseFeedReport UseFeedReport(String dayOrMonth, Integer houseType);
    List<Last5DayUseFeedByTowerVo> last5DayUseFeedByTower(Long towerId);

    /**
     *
     * @param towerId
     * @return Map<K, V> K:表示日期，V:{-1:表示用料、0：都有、1：补料}
     */
    List<Map<Object,Object>> dateDetail(Long towerId);
    List<Map<Object,Object>> dateDetailIn(Long towerId);
    String farmAllCardIds(Long farmId);
    Map<String,FeedTower> farmAllTowerCard(Long farmId);
    FeedTowerDeviceVo getDeviceByNumber(String deviceNo);

    /**
     * 启动测量前进行设备检查
     * @param deviceNo
     * @param towerOpt
     */
    void measureCheck(String deviceNo, Optional<FeedTower> towerOpt, MeasureModeEnum mode);

    /**
     * 启动测量前进行设备检查-老化
     * @param deviceNo
     * @param towerOpt
     */
    String measureCheckAging(String deviceNo, Optional<FeedTower> towerOpt, MeasureModeEnum mode);

    /**
     * 初始化校准测量
     * @param deviceNo
     * @param mode
     * @return
     */
    FeedTowerLog measureInit(String deviceNo, MeasureModeEnum mode);

    /**
     * 初始化校准测量--平均值
     * @param deviceNo
     * @param mode
     * @return
     */
    void measureInitAverage(String deviceNo, MeasureModeEnum mode,Integer checkNo);

    /**
     * 启动余料测量(打料)
     * @param deviceNo
     */
    FeedTowerLog measureStartWithMode(String deviceNo, MeasureModeEnum mode);


    /**
     * 多次启动体积校准求平均值
     * @param deviceNo--校准次数
     */
    FeedTowerLog measureStartWithModeAverageFirst(String deviceNo, MeasureModeEnum mode,Integer checkNo);

    /**
     * 多次启动体积校准求平均值
     * @param deviceNo--校准次数
     */
    void measureStartWithModeAverage(String deviceNo, MeasureModeEnum mode, Long initId);



    /**
     * 老化检测任务延迟20秒启动任务
     * @param deviceNo
     */
    FeedTowerLog measureStartAgingWithDelay(String deviceNo);

    /**
     * 启动余料测量(打料)-老化
     * @param deviceNo
     */
    FeedTowerLog measureStartWithModeAging(String deviceNo, MeasureModeEnum mode,Long agingId);


    /**
     * 老化检测任务延迟20秒启动任务-老化
     * @param deviceNo
     */
    void measureStartAgingWithDelayAging(String deviceNo,Long agingId);

    /**
     * 停止余料测量
     * @param deviceNo
     * @param taskNo
     */
    void measureStop(String deviceNo, String taskNo);

    /**
     * 老化停止
     * @param deviceNo
     * @param taskNo
     */
    void agingStop(String deviceNo, String taskNo,Long agingId);

    /**
     * 测量信息
     * @return
     */
    TowerMeasureInfoVo measureInfo(Long towerId, String mode) ;
    /**
     * 测量信息-平均值
     * @return
     */
    TowerMeasureInfoVo measureInfoAverage(Long towerId, String mode) ;

    void initConfirm(Long towerId, boolean confirm);

    /**
     * 强制重启设备
     * @param deviceNo
     */
    void reboot(String deviceNo);

    /**
     * 寻声查找设备
     * @param deviceNo
     * @param enable
     */
    void findDeviceBySound(String deviceNo, Enable enable);


    /**
     * 扫灰
     * @param deviceNo
     * @param enable
     */
    void cleanDust(String deviceNo, Enable enable);



    /**
     * 设备恢复出厂设置
     * @param deviceNo
     */
    void factoryDefault(String deviceNo);
    void delTowerConfigCache(String deviceNo);

    /**
     * 启动所有正常的料塔设备
     */
    void startAll();

    PageInfo<FeedTowerApplyVO> page(QueryFeedTowerApply queryFeedTowerApply);

    /**
     * 报料
     * 1、余料量 <= 警戒值时报料并生成报料单
     * 2、爆料量 = (容量*警戒值/100)*0.8 报料剩余容量的80%
     */
    void feedApply(FeedTower tower);

    PigFarmDataPage towerPageIn(QueryTower queryTower);
    PageInfo<FeedTower> towerPage(QueryTower queryTower);

    void startPy(Long logId,Integer thread);

    FeedTowerLogVo points(Long towerId);
    FeedTowerLogVo pointsNew(Long towerId,Long logId);

    void cachePoints(Long towerId);

    CalResponse volume(Long towerLogId);

    FeedTowerGrowthAbility growthAbility(GrowthAbilityDto dto);
    PageInfo<FeedTowerGrowthAbility> growthAbilityRecord(GrowthAbilityRecordQuery query);

    /**
     *
     * @param tower 料塔
     * @param pointCloud 点云json
     * @param type 方箱、料塔
     * @return
     */
    CalResponse calForSelfDevNew(FeedTower tower, String pointCloud, String type);

    List<TowerMeasureBatchInfoVo> batchMeasureInfo();

    void resetMeasureInfo();

    void measureStopAverage(String deviceNo, String taskNo, Long initId);

    List<TowerWarningVo> warning();

    List<FarmTowerPointVo> farmTowerPoint(String farmName, String deviceNo);


    TowerOverViewVo capView();

    void forceUnbind(String deviceNo,Integer isReset);

    zmuData exceptionView(Date startTime, Date endTime, Integer pageSize, Integer pageNum);

    List<TowerFarmLogVo> towerFarmLogList(QueryTowerFarmLog queryTowerFarmLog);

    List<FeedTowerLog> towerFarmLogDetailList(QueryTowerFarmLog queryTowerFarmLog);

    void updateWeightAndVolume(Long towerId, Long weight, Long volume);

    void updateFeedTowerLogDetail(FeedTowerLog feedTowerLog);

    void addFeedTowerLogDetail(FeedTowerLogDto feedTowerLog);

    List<TowerFarmLogVo> towerExportFind(QueryTowerFarmLog queryTowerFarmLog);

    List<TowerLogExportVo> logeExportOne(QueryTowerFarmLog queryTowerFarmLog);

    List<TowerLogExportVo> logeExportMore(QueryTowerFarmLog queryTowerFarmLog);


    void bdCorrection(Long towerId, Double bdWeight,Boolean ifExpend);

    FarmTowerDeviceVo deviceInfoByNo(String deviceNo);

    boolean chooseNetMode(String deviceNo, Long netMode);
}
