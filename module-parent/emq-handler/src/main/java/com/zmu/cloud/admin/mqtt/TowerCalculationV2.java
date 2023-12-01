package com.zmu.cloud.admin.mqtt;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zmu.cloud.commons.config.PythonProperties;
import com.zmu.cloud.commons.dto.TowerTreatyV2;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.DeviceAgingCheckService;
import com.zmu.cloud.commons.service.TowerLogService;
import com.zmu.cloud.commons.service.TowerMsgService;
import com.zmu.cloud.commons.service.TowerService;
import com.zmu.cloud.commons.utils.CRC16Util;
import com.zmu.cloud.commons.utils.CallPythonScriptUtil;
import com.zmu.cloud.commons.utils.StringUtils;
import com.zmu.cloud.commons.utils.ZmMathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.math3.filter.MeasurementModel;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.zmu.cloud.admin.mqtt.TowerMessageHandleForV2Service.*;
import static com.zmu.cloud.commons.service.TowerService.TOPIC_REFRESH;
import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TowerCalculationV2 {

    final FeedTowerMapper towerMapper;
    final TowerService towerService;
    final FeedTowerLogSlaveMapper towerLogSlaveMapper;
    final TowerLogService towerLogService;
    final TowerMsgService towerMsgService;
    final RedissonClient redissonClient;
    final MqttServer mqttServer;
    final PythonProperties pythonProperties;
    final ThreadPoolTaskExecutor taskExecutor;
    final DeviceAgingCheckService deviceAgingCheckService;
    final DeviceInitCheckMapper deviceInitCheckMapper ;
    final FeedTowerLogMapper feedTowerLogMapper ;
    final TowerProperty towerProperty ;
    final DeviceAgingCheckService agingCheckService;
    final DeviceAgingCheckMapper agingCheckMapper;

    @Value("${spring.mqtt.enable}")
    private Boolean enable;
    @Value("${file.measure}")
    private String measureDataSavePath;

    @Async
    public void calculationVolume(Long towerId, String taskNo, String deviceNo,String content) {
        FeedTower tower = towerMapper.selectById(towerId);
        Map<Integer, Map<Integer, Integer>> steeringAngle = convertAndSaveData(tower.getDeviceNo(), taskNo);
        try {
            Optional<FeedTowerLog> opt = towerLogService.findByTaskNo(tower.getDeviceNo(), taskNo);
            FeedTowerLog towerLog;
            if (opt.isPresent()) {
                towerLog = opt.get();
            } else {
                log.error("未找设备【{}】到任务【{}】对应的日志", tower.getDeviceNo(), taskNo);
                return;
            }
            //判断启动方式
            String type;
            if(MeasureModeEnum.QualityInspection.getDesc().equals(towerLog.getStartMode())){
                type = "方箱";
            }else{
                type = "料塔";
            }
            String pointCloud = JSONUtil.toJsonStr(steeringAngle);
            CalResponse calResponse = towerService.calForSelfDevNew(tower, pointCloud, type);
            setTowerLog(towerLog, calResponse);
            refreshTower(calResponse, JSONUtil.parseObj(steeringAngle),tower,towerLog,content);
        }catch (IORuntimeException f){
            saveExceptionTowerLog(tower, taskNo, "算法调用异常,请联系管理员!",content,JSONUtil.parseObj(steeringAngle));
        }catch (BaseException d) {
            saveExceptionTowerLog(tower,taskNo,d.getMsg(),content,JSONUtil.parseObj(steeringAngle));
        } catch (Exception e) {
            log.error("计算点云体积出错:",e);
            saveExceptionTowerLog(tower,taskNo,"无效测量：点云数据异常",content,JSONUtil.parseObj(steeringAngle));
        }finally {
            redissonClient.getList(CacheKey.Admin.tower_cache_data.key + deviceNo + ":" + taskNo).delete();
            mqttServer.sendToMqtt(String.format(TOPIC_REFRESH, deviceNo), 1, "");
        }
    }

    public void setTowerLog(FeedTowerLog towerLog, CalResponse calResponse) {
        towerLog.setTooFull(calResponse.getTooFull()?1:0);
        towerLog.setEmptyTower(calResponse.getEmptyTower()?1:0);
        towerLog.setTooMuchDust(calResponse.getTooMuchDust()?1:0);
        towerLog.setCaking(calResponse.getCaking()?1:0);

        //以下为2023-10-13新加字段
        towerLog.setCalVolume(ObjectUtil.isNotNull(calResponse.getVolume())?calResponse.getVolume():null);
        towerLog.setCalRealVolume(ObjectUtil.isNotNull(calResponse.getRealVolume())?calResponse.getRealVolume():null);
        towerLog.setCalCompensatePercent(ObjectUtil.isNotNull(calResponse.getCompensatePercent())?calResponse.getCompensatePercent():null);
        towerLog.setCalDensity(ObjectUtil.isNotNull(calResponse.getDensity())?calResponse.getDensity():null);
        towerLog.setCalCavityVolume(ObjectUtil.isNotNull(calResponse.getCavity_volume())?calResponse.getCavity_volume():null);
        towerLog.setCalFeedVolume(ObjectUtil.isNotNull(calResponse.getFeed_volume())?calResponse.getFeed_volume():null);
        towerLog.setCalDistanceLeftRight(ObjectUtil.isNotNull(calResponse.getDistanceLeftRight())?calResponse.getDistanceLeftRight():null);
        towerLog.setCalDistanceBeforeAfter(ObjectUtil.isNotNull(calResponse.getDistanceBeforeAfter())?calResponse.getDistanceBeforeAfter():null);
        towerLog.setCalDistanceUpDown(ObjectUtil.isNotNull(calResponse.getDistanceUpDown())?calResponse.getDistanceUpDown():null);
        towerLog.setCalWeight(ObjectUtil.isNotNull(calResponse.getWeight())?calResponse.getWeight():null);
        towerLog.setCalNote(StringUtils.isNotBlank(calResponse.getNote())?calResponse.getNote():null);
        towerLog.setCalWeightPrediction(ObjectUtil.isNotNull(calResponse.getWeightPrediction())?calResponse.getWeightPrediction():null);
    }

    private FeedTowerLog  saveExceptionTowerLog(FeedTower tower,String taskNo, String remark,String content,JSONObject data){
        Optional<FeedTowerLog> towerLog = towerLogService.findByTaskNo(tower.getDeviceNo(), taskNo);
        FeedTowerLog feedTowerLog = towerLog.get();
        feedTowerLog.setCompletedTime(LocalDateTime.now());
        feedTowerLog.setVolume(tower.getResidualVolume());
        feedTowerLog.setVolumeBase(null);
        feedTowerLog.setWeight(tower.getResidualWeight());
        feedTowerLog.setVariation(null);
        feedTowerLog.setModified(0);
        feedTowerLog.setStatus(TowerStatus.invalid.name());
        feedTowerLog.setRemark(remark);
        towerLogService.updateById(feedTowerLog);
        //校准
        isRunInitCheck(feedTowerLog);
        //老化
        isRunAgingCheck(feedTowerLog,content);
        //缓存点云数据
        feedTowerLog.setData(data.toString());
        cachePointDataed(tower, feedTowerLog);
        return feedTowerLog;
    }

    /**
     * 杨总算法
     */
    private void calForYang(FeedTower tower, String taskNo, Map<Integer, Map<Integer, Integer>> steeringAngle) {
        JSONObject param = new JSONObject();
        param.putOpt("data", JSONUtil.parseObj(steeringAngle));
        String resStr = HttpUtil.post("http://192.168.0.176:9000/calculate_A", param.toString());
        JSONObject res = JSONUtil.parseObj(resStr);
        if (res.getInt("code") == 200) {
            Optional<FeedTowerLog> opt = towerLogService.findByTaskNo(tower.getDeviceNo(), taskNo);
            opt.ifPresent(towerLog -> {
                towerLog.setVolumeYang(ZmMathUtil.m3ToCm3(res.getDouble("data")));
                towerLogService.updateById(towerLog);
            });
        } else {
            throw new BaseException(res.getStr("msg"));
        }
    }

    // 根据料塔大小动态设置冗余值范围
    private HashMap<String, Long> getDynamicReduceAnd(Long initVolume) {
        HashMap<String, Long> hashMap = new HashMap<>();
        // 7立方以内的小料塔
        long reduce = towerProperty.getReduceDefault();
        long add = towerProperty.getAddDefault();
        long residualLeft  = towerProperty.getResidualLeftDefault();
        long residualToZero  = towerProperty.getResidualToZeroDefault();
        long add_min_default_expansion = towerProperty.getAdd_min_default_Expansion();
        long add_min_default_percent_Expansion = towerProperty.getAdd_min_default_percent_Expansion();
        long add_max_default_percent_Expansion = towerProperty.getAdd_max_default_percent_Expansion();
        if(ObjectUtil.isNotEmpty(initVolume)){
            if (initVolume > 28000000L) {
                // 28立方以以上
                add = towerProperty.getAdd28();
                reduce = towerProperty.getReduce28();
                residualLeft = towerProperty.getResidualLeft28();
                residualToZero  = towerProperty.getResidualToZero28();
                add_min_default_expansion = towerProperty.getAdd_min_default_Expansion28();
                add_min_default_percent_Expansion = towerProperty.getAdd_min_default_percent_Expansion28();
                add_max_default_percent_Expansion = towerProperty.getAdd_max_default_percent_Expansion28();
            } else if (initVolume > 21000000L) {
                // 21立方以以上
                add = towerProperty.getAdd21();
                reduce = towerProperty.getReduce21();
                residualLeft = towerProperty.getResidualLeft21();
                residualToZero  = towerProperty.getResidualToZero21();
                add_min_default_expansion = towerProperty.getAdd_min_default_Expansion21();
                add_min_default_percent_Expansion = towerProperty.getAdd_min_default_percent_Expansion21();
                add_max_default_percent_Expansion = towerProperty.getAdd_max_default_percent_Expansion21();
            } else if (initVolume > 14000000L) {
                // 14立方以以上
                add = towerProperty.getAdd14();
                reduce = towerProperty.getReduce14();
                residualLeft = towerProperty.getResidualLeft14();
                residualToZero  = towerProperty.getResidualToZero14();
                add_min_default_expansion = towerProperty.getAdd_min_default_Expansion14();
                add_min_default_percent_Expansion = towerProperty.getAdd_min_default_percent_Expansion14();
                add_max_default_percent_Expansion = towerProperty.getAdd_max_default_percent_Expansion14();
            } else if (initVolume > 7000000L) {
                // 7立方以以上
                add = towerProperty.getAdd7();
                reduce = towerProperty.getReduce7();
                residualLeft = towerProperty.getResidualLeft7();
                residualToZero  = towerProperty.getResidualToZero7();
                add_min_default_expansion = towerProperty.getAdd_min_default_Expansion7();
                add_min_default_percent_Expansion = towerProperty.getAdd_min_default_percent_Expansion7();
                add_max_default_percent_Expansion = towerProperty.getAdd_max_default_percent_Expansion7();
            }else if (initVolume > 4500000L) {
                // 4.5立方以以上
                add = towerProperty.getAdd4();
                reduce = towerProperty.getReduce4();
                residualLeft = towerProperty.getResidualLeft4();
                residualToZero  = towerProperty.getResidualToZero4();
                add_min_default_expansion = towerProperty.getAdd_min_default_Expansion4();
                add_min_default_percent_Expansion = towerProperty.getAdd_min_default_percent_Expansion4();
                add_max_default_percent_Expansion = towerProperty.getAdd_max_default_percent_Expansion4();
            }else if (initVolume >= 0L) {
                // 0-4.5立方以以上
                add = towerProperty.getAdd0To4();
                reduce = towerProperty.getReduce0To4();
                residualLeft = towerProperty.getResidualLeft0To4();
                residualToZero  = towerProperty.getResidualToZero0To4();
                add_min_default_expansion = towerProperty.getAdd_min_default_Expansion0To4();
                add_min_default_percent_Expansion = towerProperty.getAdd_min_default_percent_Expansion0To4();
                add_max_default_percent_Expansion = towerProperty.getAdd_max_default_percent_Expansion0To4();
            }
        }
        hashMap.put("reduce", reduce);
        hashMap.put("add", add);
        hashMap.put("residualLeft", residualLeft);
        hashMap.put("residualToZero", residualToZero);
        hashMap.put("add_min_default_expansion", add_min_default_expansion);
        hashMap.put("add_min_default_percent_Expansion", add_min_default_percent_Expansion);
        hashMap.put("add_max_default_percent_Expansion", add_max_default_percent_Expansion);
        return hashMap;
    }



    public FeedTowerLog refreshTower(CalResponse calResponse,JSONObject data,FeedTower tower,FeedTowerLog towerLog,String content) {
        int modified;
        Long initVolume = tower.getInitVolume();
        HashMap<String, Long> dynamicReduceAnd = getDynamicReduceAnd(initVolume);
        long reduce = dynamicReduceAnd.get("reduce");
        long add = dynamicReduceAnd.get("add");
        long residualLeft = dynamicReduceAnd.get("residualLeft");
        long residualToZero = dynamicReduceAnd.get("residualToZero");

        long add_min_default_expansion = dynamicReduceAnd.get("add_min_default_expansion");
        long add_min_default_percent_Expansion = dynamicReduceAnd.get("add_min_default_percent_Expansion");
        long add_max_default_percent_Expansion = dynamicReduceAnd.get("add_max_default_percent_Expansion");

        double variation = 0D;

        if (MeasureModeEnum.Init.getDesc().equals(towerLog.getStartMode())) {
            //towerLog.setStartMode(MeasureModeEnum.Init.getDesc());
            /*if (ObjectUtil.equals(calResponse.getEmptyTower(), Boolean.TRUE)) {
                tower.setInitVolume(calResponse.getVolume());
                tower.setInit(1);
                tower.setInitTime(LocalDateTime.now());
                // 校准完成之后重置余料量和余料体积为0
                tower.setResidualWeight(0L);
                tower.setResidualVolume(0L);
                tower.setResidualDate(LocalDateTime.now());
                towerMapper.updateById(tower);
                towerService.delTowerConfigCache(tower.getDeviceNo());
            } else {
                //缓存起来等待用户确认
                RBucket<Long> init = redissonClient.getBucket(CacheKey.Admin.tower_init_data.key + tower.getDeviceNo());
                init.set(calResponse.getVolume());
                init.expire(CacheKey.Admin.tower_init_data.duration);
            }*/
        } else {
            Long residual = calResponse.getFeed_volume();
            if(residual < residualLeft){
                //如果出现空腔比空料塔体积还大,证明是无效数据(可能是料塔盖子被打开) 忽略本次测量
                towerLog.setCompletedTime(LocalDateTime.now());
                towerLog.setVolume(tower.getResidualVolume());
                towerLog.setVolumeBase(calResponse.getVolume());
                towerLog.setWeight(tower.getResidualWeight());

                setCalData(calResponse, towerLog);

                towerLog.setVariation((long) Math.abs(variation));
                towerLog.setModified(0);
                towerLog.setStatus(TowerStatus.invalid.name());
                towerLog.setRemark("无效测量：测量体积超过初始值");
                towerLogService.updateById(towerLog);
                //缓存点云数据
                cachePointDataed(tower, towerLog);

                //校准
                isRunInitCheck(towerLog);
                return towerLog;
            } else if (residual < residualToZero) {
                residual = 0L;
                calResponse.setWeight(0L);
            }

            long modify = residual - (ObjectUtil.isEmpty(tower.getResidualVolume()) ? 0 : tower.getResidualVolume());
            if (modify < reduce || modify > add) {
                tower.setResidualVolume(residual);
                if (ObjectUtil.isNotEmpty(tower.getDensity())) {
                    // 如果开启了磅单优化
                    if(tower.getBdOptimization() == 1){
                        //有密度且变化量大于冗余值,刷新料塔余料量
                        Long oldTowerResidualWeight = ObjectUtil.isNull(tower.getResidualWeight())?0:tower.getResidualWeight();
                        double residualWeight = calResponse.getWeight()<0?0:calResponse.getWeight();
                        variation = (ObjectUtil.isNull(tower.getResidualWeight())?0:tower.getResidualWeight()) + tower.getGapWeight() - residualWeight;
                        // 历史重量 - 当前重量 > 0 是用料，反之则加料， == 0 表示没变化
                        modified = variation > 0 ? -1 : (variation < 0 ? 1 : 0);
                        if(ObjectUtil.isEmpty(tower.getDensity()) || tower.getDensity()==0){
                            modified = 0;
                        }
                        log.info("料塔{}===id{}开启了磅单优化,使用磅单优化方式进行计算...",tower.getName(),tower.getId());
                        //如果是加料并且开启了打料膨胀计算
                        if(modified == 1){
                            if(tower.getSwitchFeedAddExpansion()==1){
                                log.info("料塔{}===id{}操作是加料并且料塔开启了膨胀计算..",tower.getName(),tower.getId());
                                // 根据打料量和当前余料百分比判定是否膨胀
                                //1.加料量>最小膨胀加料量 2.加料后余料量介于 饲料膨胀 余料比例范围
                                //计算余料占比(体积占比)
                                long feedVolumePercent = Math.round(((double) calResponse.getFeed_volume() / tower.getInitVolume()) * 100);
                                if(-variation>add_min_default_expansion && feedVolumePercent>add_min_default_percent_Expansion && feedVolumePercent<add_max_default_percent_Expansion){
                                    log.info("料塔{}===id{}加料结果符合膨胀计算规则..",tower.getName(),tower.getId());
                                    //调整过的饲料体积*密度
                                    Long correctWeight = (calResponse.getWeight() -tower.getGapWeight()) ;

                                    tower.setResidualWeight(correctWeight <0?0:correctWeight);
                                }else{
                                    log.info("料塔{}===id{}加料结果不符合膨胀计算规则,不进行膨胀计算..",tower.getName(),tower.getId());
//                                Long correctWeight = (Long)(residual - tower.getCorrectEmptyTowerVolume())*tower.getDensity()/1000000;
                                    Long correctWeight = (calResponse.getWeight() -(Math.round(tower.getGapWeight() *tower.getFeedAddExpansionValue()))) ;
                                    tower.setResidualWeight(correctWeight <0?0:correctWeight);
                                }
                            }else{
                                log.info("料塔{}===id{}正常通过磅单校正方式计算余料值",tower.getName(),tower.getId());
                                //调整过的饲料体积*密度
//                            Long correctWeight = (Long)(residual - tower.getCorrectEmptyTowerVolume())*tower.getDensity()/1000000;
                                Long correctWeight = (calResponse.getWeight() -tower.getGapWeight()) ;
                                tower.setResidualWeight(correctWeight <0?0:correctWeight);
                            }
                        }else if(modified == -1){
                            log.info("料塔{}===id{}操作是放料..",tower.getName(),tower.getId());
                            //调整过的饲料体积*密度
//                            Long correctWeight = (Long)(residual - tower.getCorrectEmptyTowerVolume())*tower.getDensity()/1000000;
                            Long correctWeight;
                            if(tower.getSwitchFeedAddExpansion()==1){
                                 correctWeight = (calResponse.getWeight() -(Math.round(tower.getGapWeight() *tower.getFeedAddExpansionValue()))) ;
                            }else{
                                 correctWeight = (calResponse.getWeight() -tower.getGapWeight()) ;
                            }
                            //如果最近一次的打料或者放料记录是 [打料],那么调大更新重量的冗余值,只有超过调大的冗余值才显示放料
                            //TODO
                            LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
                            wrapper.eq(FeedTowerLog::getDeviceNo,tower.getDeviceNo());
                            wrapper.ne(FeedTowerLog::getModified,0);
                            wrapper.orderByDesc(FeedTowerLog::getId);
                            wrapper.last("limit 1");
                            FeedTowerLog feedTowerLog = feedTowerLogMapper.selectOne(wrapper);
                            if(ObjectUtil.isNotEmpty(feedTowerLog) && feedTowerLog.getModified() ==1){
                                log.info("料塔{}===id{}最近一次的打料或者放料记录是 [打料]..",tower.getName(),tower.getId());
                                //调大更新重量的冗余值,只有超过调大的冗余值才显示放料
                                if (modify < reduce*2 && correctWeight<tower.getResidualWeight()){
                                    log.info("料塔{}===id{}满足调大过的冗余值,允许进行放料刷新..",tower.getName(),tower.getId());
                                    tower.setResidualWeight(correctWeight <0?0:correctWeight);
                                }else{
                                    log.info("料塔{}===id{}不满足调大过的冗余值,不允许进行放料刷新..",tower.getName(),tower.getId());
                                    //把变化量调整为0
                                    variation = 0D;
                                }
                            }else{
                                //只有当料塔料当前余料计算量低于了之前的量才会设置,防止膨胀调整过后,短时间内饲料下沉导致料量计算结果上涨的情况
                                if(correctWeight<tower.getResidualWeight()){
                                    tower.setResidualWeight(correctWeight <0?0:correctWeight);
                                }else{
                                    log.info("料塔{}===id{}膨胀调整过后,短时间内饲料下沉导致料量计算结果上涨的情况.不允许刷新余料值..",tower.getName(),tower.getId());
                                    //把变化量调整为0
                                    variation = 0D;
                                }
                            }
                        }else{
                            log.info("料塔{}===id{}正常通过磅单校正方式计算余料值",tower.getName(),tower.getId());
                            //调整过的饲料体积*密度
//                            Long correctWeight = (Long)(residual - tower.getCorrectEmptyTowerVolume())*tower.getDensity()/1000000;
                            Long correctWeight;
                            if(tower.getSwitchFeedAddExpansion()==1){
                                correctWeight = (calResponse.getWeight() -(Math.round(tower.getGapWeight() *tower.getFeedAddExpansionValue()))) ;
                            }else{
                                correctWeight = (calResponse.getWeight() -tower.getGapWeight()) ;
                            }
                            tower.setResidualWeight(correctWeight <0?0:correctWeight);
                        }
                        //重新再次修正纠正后的变化量
                        variation = oldTowerResidualWeight - tower.getResidualWeight();
                    }else{
                        //有密度且变化量大于冗余值,刷新料塔余料量
                        double residualWeight = calResponse.getWeight()<0?0:calResponse.getWeight();
                        variation = (ObjectUtil.isNull(tower.getResidualWeight())?0:tower.getResidualWeight()) - residualWeight;
                        log.info("料塔{}===id{}未开启了磅单优化,直接使用算法返回结果...",tower.getName(),tower.getId());
                        tower.setResidualWeight((long) residualWeight);
                    }
                }
                tower.setResidualDate(LocalDateTime.now());
                //调整饲料体积和计算结果一致 TODO (暂时不需要)
//                tower.setResidualVolume(Math.round((double)tower.getResidualWeight()/tower.getDensity() * 1000000D));
                //如果重量为0，那么体积也为0 TODO (需要)
                if(0L==tower.getResidualWeight() && ObjectUtil.isNotEmpty(tower.getDensity()) && tower.getDensity() != 0){
                    tower.setResidualVolume(0L);
                }
                towerMapper.updateById(tower);
                redissonClient.getBucket(CacheKey.Admin.tower_config.key + tower.getDeviceNo()).delete();
                //余料量少于警戒值时进行报料并生成报表单
//                towerService.feedApply(tower);
                sendResidualWeight(tower.getDeviceNo(), (int)(ObjectUtil.isNull(tower.getResidualWeight())?0:tower.getResidualWeight()/1000));
            }
        }


        // 历史重量 - 当前重量 > 0 是用料，反之则加料， == 0 表示没变化
        modified = variation > 0 ? -1 : (variation < 0 ? 1 : 0);
        if(ObjectUtil.isEmpty(tower.getDensity()) || tower.getDensity()==0){
            modified = 0;
        }
        towerLog.setCompletedTime(LocalDateTime.now());
        towerLog.setVolume(tower.getResidualVolume());
        towerLog.setVolumeBase(calResponse.getVolume());
        towerLog.setWeight(tower.getResidualWeight());

        setCalData(calResponse, towerLog);

        towerLog.setVariation((long) Math.abs(variation));
        towerLog.setModified(modified);
        towerLog.setStatus(TowerStatus.completed.name());
        towerLog.setRemark("正常");

        //调用因福克斯的接口记录重量
        String yhbRequestData = getYHBRequestData(tower.getId());
        if(ObjectUtil.isNotEmpty(yhbRequestData)){
            Long hybResult = transferHYB(yhbRequestData);
            towerLog.setVolumeYang(hybResult);
        }

        towerLogService.updateById(towerLog);

        //校准
        isRunInitCheck(towerLog);
        //老化
        isRunAgingCheck(towerLog,content);

        //如果是有效测量,缓存点云数据
        towerLog.setData(data.toString());
        cachePointDataed(tower, towerLog);

        return towerLog;
    }

    /**
     * 缓存点云数据
     * @param tower
     * @param towerLog
     */
    public void cachePointDataed(FeedTower tower, FeedTowerLog towerLog) {
        RList<FeedTowerLog> list = redissonClient.getList(CacheKey.Admin.tower_data.key.concat(tower.getId().toString()));
        //从缓存中删除最旧的一条点云，新增最新的一条点云
        List<FeedTowerLog> feedTowerLogs = updateCachePointData(towerLog, list);
        // 执行您需要的操作，例如添加、删除、迭代等
        list.clear();
        list.addAll(feedTowerLogs);
    }

    /**
     * 从缓存中删除最旧的一条点云，新增最新的一条点云
     * @param towerLog
     * @param list
     */
    public List<FeedTowerLog> updateCachePointData(FeedTowerLog towerLog, RList<FeedTowerLog> list) {
        List<FeedTowerLog> collect = list.stream().sorted(Comparator.comparing(FeedTowerLog::getCompletedTime)).collect(Collectors.toList());
        if (ObjectUtil.isNotNull(list) && list.size()>=10) {
            collect.remove(0);//删除最旧的一条数据
        }
        collect.add(towerLog);
        return collect.stream().sorted(Comparator.comparing(FeedTowerLog::getCompletedTime).reversed()).collect(Collectors.toList());
    }


    public void setCalData(CalResponse calResponse, FeedTowerLog towerLog) {
        if (!MeasureModeEnum.Manual.getDesc().equals(towerLog.getStartMode()) && !MeasureModeEnum.Auto.getDesc().equals(towerLog.getStartMode())){
            towerLog.setVolume(calResponse.getFeed_volume());
            towerLog.setVolumeBase(calResponse.getVolume());
            towerLog.setWeight(calResponse.getWeight());
        }
    }

    public static void main(String[] args) {
//        transferHYB(getYHBRequestData(217L));
//        sendPostRequest();

    }

    public static String getYHBRequestData(Long key){
        HashMap<Long, String> hashMap = new HashMap<>();
        // #1 后备2
        hashMap.put(140L,"farmId=fO09/kQ/cEqEzkhc16jKR3SENN%2Br37TcASuXHJqcr/1sq8ZQxtO0%2BGQaZv1%2Bbkioxa/KkuN9XetE0TYGdKE%2B0Sa4gU6pWS5qljQYNRnBoaMd664RLAbgrE9Avhl2nb1rfkJrc2K779a%2B1oNsIz6XskaljLtWtEbnfkUE1GbFwm4=&feedTowerId=yGNgmN0Vd7QkbeYY3xswXhIFsX0hoK3R3yNw9BaTgQFp%2BVuRlwFSjV8Kmn/kBCfCVzfN56VcahQ0w3EP9NAWhcOUnNr3HBXJUza7/QlFlfA75KCgHCamRXe1aEzpml5saflxB9SldzlsXgyQuc0TMM/Y3m0uBkBCZHkiv7zTJSQ=");

        // #3 公猪
        hashMap.put(135L,"farmId=KuD5xS0oDFxesUEB/ztWVoXGzkStje0OX0tG0ZnJxe8Vmhlp8RzjpcPlFxkmyubotgsZ6ynVaolniZSfpGBFmI04mF3sWl8mNiyyX%2BW0N6BEZoST6IT2dCXzErNbdnRnp7EBkV5l5CR6%2B0l/gwvFs%2BO9nqQXlJumh0bOMWhm%2BA4=&feedTowerId=As2xlnqUi4q8ajjGdLjj5XOa5ajWFafy6H4jdA0FfDVHKMZQQS8fBeQO2dvSK1lmE69UbwFPtzvSVF5CIoCwrKSqdLUKgCdKwmS5LzigR70SrzFsMqlNezVs5Ld7/htcUdqMOJFN54y3HKBuc4vPa9Hmm5BCjksMt0KlxzsOlzk=");


        // #4 后备1
        hashMap.put(141L,"farmId=XsITJ5zQpIAHJYYAylOVGA6EvMsNZL6HDORqrQ4LDeqLgFBB1nI4OtmjQx5cOrlZ1paifQ0Ho3Gh5kT/7vyeO06di6MddJxiGRtj2niSVEUkFcdR9v3q7PrjffdsfagAkyhM3VWFJrbvKd3NDpSRv%2BsPh1GfKrXHoXRagOnvdeY=&feedTowerId=WEf0c18GgVeaosNjbImtQEI4cUrRHaka3BrlgEPSQhtwbf8a38eTCP1ZN7aoNAOoVMimb2H7aFB8uzkzw4JTBkvxFqwNi38%2BLHVBkLgQx9NDeTFBO/x59s3IKafPBmeuU44vUweD1KLjWdWwdSxG4NxTA/9/LX5RWrThngKoU1g=");


        // #8 保育2
        hashMap.put(137L,"farmId=Z3zhPSxoOvd0exBKS9hqTFJpLWK%2BONWx3rvwjtxuFD5MUMP8A5WD2esE9f3c2Qz/WnYbeeTxoE/abAf7vym22niJjfgQw%2BsAo/Q5Gpaizo98FnO%2BKrKpQ7c0sWElec/RWPE78xMsqYHBqKgVeiTXNlQO%2BmGu5DkYjio%2BU3m0vDc=&feedTowerId=lCdaaOm0dodF3Z2ivv6KbzZFPLglcsz44GJT2IWEKYgtGpre%2B%2Bq%2BEhGUNeEL0TbnaK3YAAbHHTcMn963e3m43E4jsBu7mYTGhrbE1G3ZgAJoE%2B2CjfVHIx2LzhiXw1elFk6DxmfCVr8UYqmHNHy5A2WgH%2BPt47eQJ3qweLyi4fA=");


        // #5 保育1
        hashMap.put(138L,"farmId=bmBZn5c21OHtTmjt6DAaleuUXAcHeHl0CZ/Z6NO/09IND9bGipVnhIFsB1Us6BdiAdccMYFGbCOTR0pSgGD6mSGhJD/6OxK20weERfAYjswa6/ouehnQS/7XrizB4n9EHxZmjpRJv3xMiKSCoFg9/yddohoa57CzPxxyEc8omMU=&feedTowerId=vqo9trpH4YQeRaHX1y0tzDqXSXoTjuqAPwI3nJtQMeX1OlHcFz13NsIUIhHIS5fmI2mEATHrLCRy4QuraavVMWD18sbgYEMvrBFh1cHne7xtOstVqsEJSsmzUUzCDxcMgThTdsSuMBw03sIA2nShdEoO6EyX8VqJM1Fch2nj4ZA=");

        // #9 兴裕配怀1
        hashMap.put(940L,"farmId=sd4q8g4wKI7cMWsQtxvurgA4bUigiAw24C0ePPtqgOobfEzH0jv/jXWWGnwqR%2Becmp4I3KmBR42V77wgQ1pmqD6cB/%2BPKbyRYF4m0g4vQZEUz%2B8J2CAm02GF%2B011KMQkwqa29aE0Xca2cFoO405lgNCqEUy0lszE6rL9oBqLD14=&feedTowerId=gGp/60jvX%2B1S4rwjHGT6lHlnv2tMZ%2BVhCYISgy4TFg0JVf6P5vetAzfh8o5FzDJeZs4mHwETS0G0XpxpDklT2%2BLJlRRipCRTOh6OZeQtrJvLhDAUIbUAxPAdXWLiYK/FvWlR5gZFW4rw/C3xikPYCTen3VExJQsIQvyZdFfNSXc=");


        // #7 配怀2
        hashMap.put(533L,"farmId=DQqInD/n8h8eem3r/SJ9EWLCNekbb2BS8B9Tu4z7uqhqc6CUCE615hgJEXQMPIB5LuxR0xfvh/n5oAm9q%2Bin4xOWMeXQQFNF27VSTQvJpqhdafVMYOTCpRJXOQJ2a/CgR5AVJqt5qs6pfEnetwUu9jcy8p41R0CAx/6l4fkZgTQ=&feedTowerId=Vydpruq0KBpJ5x1rKj2aexsOVRmd1SE70GZ43N9svxn6C14cCSzwqT5ep2nU5jBYVpjAB8jV0CrF/fKE9v0HQ3T6vFRmb9t7qrei7nZcGpPpEkLyMExmDWcvAstpFT/pwNlqUHd22aZMksXF6KChLCRlouoPGibKxc4iI41hvhQ=");

        return hashMap.get(key);
    }




    public Map<Integer, Map<Integer, Integer>> convertAndSaveData(String deviceNo, String taskNo) {
        List<FeedTowerMsg> msgs;
        synchronized (this) {
            RList<FeedTowerMsg> data = redissonClient.getList(CacheKey.Admin.tower_cache_data.key + deviceNo + ":" + taskNo);
            if (!data.isExists() || data.isEmpty()) {
                return null;
            }
            msgs = data.readAll();
            log.info("【{}:{}】收集到{}帧数据", deviceNo, taskNo, msgs.size());
        }
        if (ObjectUtil.isEmpty(msgs)) {
            return null;
        }
        towerMsgService.saveBatch(msgs);

        Map<Integer, Map<Integer, Integer>> steeringAngle = new HashMap<>();
        msgs.forEach(msg -> {
            Map<Integer, Integer> angleDis = new HashMap<>();
            Integer steering = Integer.parseInt(msg.getContent().substring(0, 2), 16);
            String angleDisContent = msg.getContent().substring(2);
            wrap(angleDis, angleDisContent);
            if (steeringAngle.containsKey(steering)) {
                steeringAngle.get(steering).putAll(angleDis);
            } else {
                steeringAngle.put(steering, angleDis);
            }
        });
        towerLogService.findByTaskNo(deviceNo, taskNo).ifPresent(towerLog -> {
            towerLogSlaveMapper.insert(FeedTowerLogSlave.builder().logId(towerLog.getId()).deviceNo(towerLog.getDeviceNo())
                    .taskNo(towerLog.getTaskNo()).data(JSONUtil.parseObj(steeringAngle).toString()).createTime(towerLog.getCreateTime()).build());
//            //存文件
//            String base = measureDataSavePath + File.separator + deviceNo;
//            Arrays.stream(Objects.requireNonNull(new File(base).listFiles())).map(f -> f.getName()).max(String::compareTo).get();

//            + File.separator + DateUtil.formatDate(new Date());
//            File store = new File(path);
//            if (Objects.requireNonNull(store.listFiles()).length <= 500) {
//                path += LocalDateTimeUtil.formatNormal(towerLog.getCreateTime()) + ".json";
//            } else {
//
//            }
//            FileUtil.writeString(JSONUtil.parseObj(steeringAngle).toString(), )
        });
        return steeringAngle;
    }

    public  Long transferHYB(String requestData) {
        log.info("向英孚发送请求.......");
        String token;
        // token放redis里面方便修改
        RBucket<String> tokenBucket = redissonClient.getBucket(CacheKey.Admin.hyb_token.key);
        if (tokenBucket.isExists()) {
            token = tokenBucket.get();
        }else{
            log.warn("英孚token不存在,请配置!");
            return null;
        }

        String url = "http://api.infoexdata.cn/userData/farm/getAppFeedTowerInfoByFeedTowerId";

//        // 构建表单请求体
//        FormBody requestBody = new FormBody.Builder()
//                .add("", requestData)
//                .build();

//        // 不仅可以支持传文件，还可以在传文件的同时，传参数
//        MultipartBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM) // 设置传参为form-data格式
//                .addFormDataPart("",  requestData) // 中间参数为文件名
//                .build();

//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), requestData);
        // 构建请求体
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestData);

        System.out.println(requestBody.toString());


        // 构建request请求体，添加请求头
        Request request = new Request.Builder()
                .url(url)
                .header("token", token)
                .header("User-Agent", "okhttp/4.9.1")
                .header("channel", "app")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
        System.out.println(request);

        // 发送请求并获取响应
        OkHttpClient httpClient = new OkHttpClient();
        try {
            Response response = httpClient.newCall(request).execute();
            // 获取响应状态码和响应数据
            int statusCode = response.code();
            String responseBody = response.body().string();
            // 打印响应状态码和数据
            log.info("状态码: " + statusCode);
            log.info("响应数据: " + responseBody);
            // 使用Gson解析JSON数据
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            JsonObject dataObject = jsonObject.getAsJsonObject("data");
            JsonObject feedTowerObject = dataObject.getAsJsonObject("feedTower");
            int towerRemainWeight = feedTowerObject.get("towerRemainWeight").getAsInt();
            // 打印remaining字段值
            log.info("称重系统结果为：" + towerRemainWeight +"kg");
            // 将remainingValue乘以1000并转换为Long类型
            long result = (long) (towerRemainWeight * 1000);
            log.info("称重系统结果转换为g之后的结果为：" + result +"g");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 精度：竖直角度 0.01°、距离 0.1cm, V2返回的是1cm精度，故下面需要在实际值上面乘以 10 转为 0.1cm
     * @param angleDis
     * @param content
     */
    private void wrap(Map<Integer, Integer> angleDis, String content) {
        if (ObjectUtil.isEmpty(content) || content.length() < 8) {
            return;
        }
        int angle = ByteUtil.bytesToShort(CRC16Util.hexToByteArray(content.substring(0, 4)));
        int dis = ByteUtil.bytesToShort(CRC16Util.hexToByteArray(content.substring(4, 8)));
        if (0 != dis) {
            angleDis.put(angle, dis*10);
        }
        wrap(angleDis, content.substring(8));
    }

    public static void deleteFileIfExists(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 发送料塔剩余饲料重量 kg
     * @param weight kg
     * @return TowerTreatyV2
     */
    TowerTreatyV2 sendResidualWeight(String deviceNo, int weight) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 20)
                .version(VERSION)
                .cmd("0A")
                .code(code())
                .contentLength((short) 4)
                .content(CRC16Util.bytesToHex(ByteUtil.intToBytes(weight)))
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        if (enable) {
            mqttServer.sendToMqtt(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        }
        return treaty;
    }

//    public static void main(String[] args) {
//        System.out.println(checkInitOutOfRange(100L,97L,2D));
//    }

    /**
     * 老化检查是否需要再次启动
     * @param feedTowerLog
     * @param code
     */
    @Transactional(readOnly = false)
    public void isRunAgingCheck(FeedTowerLog feedTowerLog, String code){
        try {
            if (ObjectUtil.isNotNull(feedTowerLog) && ObjectUtil.isNotNull(feedTowerLog.getAgingId())){
                DeviceAgingCheck deviceAgingCheck = deviceAgingCheckService.selectByTd(feedTowerLog.getAgingId());
                int err = deviceAgingCheck.getErrCount()+1;
                //Long runCount = feedTowerLogMapper.selectRunCountByAgingId(feedTowerLog.getAgingId());
                long between = ChronoUnit.HOURS.between(deviceAgingCheck.getStartTime(), LocalDateTime.now());//已运行时长
                if (!"00".equals(code)) {
                    deviceAgingCheck.setErrCount(err);
                }
                //deviceAgingCheck.setRunCount(runCount.intValue());
                deviceAgingCheckService.updateOne(deviceAgingCheck);
                if (between < deviceAgingCheck.getCheckCount() && DeviceCheckStatusEnum.CANCEL.getStatus() != deviceAgingCheck.getPass()){
                    if (isRestDate()) {//判断当前时间是否在设备重启时间段
                        towerService.measureStartAgingWithDelayAging(feedTowerLog.getDeviceNo(),feedTowerLog.getAgingId());
                    }
                }else {
                    updateAgingPassed(deviceAgingCheck);//更新老化表相关数据
                    deviceAgingCheck.setEndTime(LocalDateTime.now());
                    deviceAgingCheckService.updateOne(deviceAgingCheck);
                }
            }
        } catch (Exception e) {
            throw new BaseException("老化异常，请联系管理员！");
        }
    }


    /**
     * 判断当前时间是否在设备重启时间段
     * @return
     */
    public boolean isRestDate() {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(23, 50, 00);
        LocalTime end = LocalTime.of(00, 10, 00);
        if (now.isAfter(start) || now.isBefore(end)){
            return false;
        }
        return true;
    }

    /**
     * 更新老化数据
     * @param deviceAgingCheck
     */
    public void updateAgingPassed(DeviceAgingCheck deviceAgingCheck) {
        LambdaQueryWrapper<FeedTowerLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FeedTowerLog::getAgingId,deviceAgingCheck.getId());
        List<FeedTowerLog> feedTowerLogs = feedTowerLogMapper.selectList(lambdaQueryWrapper);
        //判断是否异常检测
        List<FeedTowerLog> completedLogs = feedTowerLogs.stream().filter(log -> log.getStatus().equals("completed")).collect(toList());//完成数量
        if(ObjectUtil.isNotNull(deviceAgingCheck.getEndTime())
                && ObjectUtil.isNotNull(deviceAgingCheck.getStartTime())
                && ChronoUnit.HOURS.between(deviceAgingCheck.getStartTime(), deviceAgingCheck.getStartTime()) >= deviceAgingCheck.getCheckCount()){
            if(DeviceCheckStatusEnum.READY.getStatus() >= deviceAgingCheck.getPass()){
                Optional<FeedTowerLog> max = feedTowerLogs.stream().max(Comparator.comparing(FeedTowerLog::getCreateTime));
                FeedTowerLog log = max.get();
                //测量完成,修改状态
                if(feedTowerLogs.size() == completedLogs.size()
                        && !TowerStatus.running.name().equals(log.getStatus())
                        && !TowerStatus.starting.name().equals(log.getStatus())){//老化完成并通过
                    deviceAgingCheck.setPass(DeviceCheckStatusEnum.PASS.getStatus());
                    agingCheckMapper.updateById(deviceAgingCheck);
                    //系统手动通过
                    agingCheckService.handle(deviceAgingCheck.getId(),true,"无","zd");
                }else if (!TowerStatus.running.name().equals(log.getStatus())
                        && !TowerStatus.starting.name().equals(log.getStatus())){
                    deviceAgingCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
                    deviceAgingCheck.setPass(DeviceCheckStatusEnum.NOT_PASS.getStatus());
                    agingCheckMapper.updateById(deviceAgingCheck);
                }
            }
        }
    }

    /**
     * 校准是否需要再次启动
     * @param feedTowerLog
     */
    @Transactional(readOnly = false)
    public void isRunInitCheck(FeedTowerLog feedTowerLog){
        if (ObjectUtil.isNotNull(feedTowerLog) && ObjectUtil.isNotNull(feedTowerLog.getInitId())){
            DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(feedTowerLog.getInitId());
            int err = deviceInitCheck.getErrCount()+1;
            Long runCount = feedTowerLogMapper.selectRunCountByInitId(feedTowerLog.getInitId());
            deviceInitCheck.setRunCount(runCount.intValue());
            if (TowerStatus.completed.name().equals(feedTowerLog.getStatus())) {
                if (runCount < deviceInitCheck.getCheckCount()) {
                    towerService.measureStartWithModeAverage(feedTowerLog.getDeviceNo(),MeasureModeEnum.Init,feedTowerLog.getInitId());
                }else {
                    deviceInitCheck.setCheckStatus(DeviceInitCheckStatusEnum.NORMAL.getStatus());
                    deviceInitCheck.setEndTime(LocalDateTime.now());
                    deviceInitCheck.setUpdateTime(LocalDateTime.now());
                    //计算平均体积
                    long avg = getVolumeAverage(feedTowerLog, deviceInitCheck);
                    deviceInitCheck.setVolume(avg);
                    deviceInitCheck.setRemark("正常");
                    deviceInitCheckMapper.updateById(deviceInitCheck);
                    //更新feedTower数据
                    updateInitFeedTower(feedTowerLog, avg);
                }
            }else {
                if (deviceInitCheck.getCheckStatus() != DeviceInitCheckStatusEnum.ERR.getStatus()) {
                    deviceInitCheck.setCheckStatus(DeviceInitCheckStatusEnum.ERR.getStatus());
                    deviceInitCheck.setEndTime(LocalDateTime.now());
                    deviceInitCheck.setUpdateTime(LocalDateTime.now());
                    deviceInitCheck.setErrCount(err);
                    deviceInitCheck.setRemark(feedTowerLog.getRemark()+" 系统自动停止校准");
                    deviceInitCheckMapper.updateById(deviceInitCheck);
                }
                towerService.delTowerConfigCache(feedTowerLog.getDeviceNo());
            }

        }
    }

    /**
     * 测量平均体积过后更新到feedTower表
     * @param feedTowerLog
     * @param avg
     */
    private void updateInitFeedTower(FeedTowerLog feedTowerLog, long avg) {
        FeedTower feedTower = towerMapper.selectById(feedTowerLog.getTowerId());
        Long initVolume = feedTower.getInitVolume();//原来体积
        Boolean flag = checkInitOutOfRange(initVolume, avg, 2D);
        if(flag){
            feedTower.setInitVolume(avg);
            feedTower.setResidualVolume(0L);
            feedTower.setResidualWeight(0L);
            feedTower.setInit(1);
            feedTower.setResidualDate(LocalDateTime.now());
            feedTower.setInitTime(LocalDateTime.now());
            towerMapper.updateById(feedTower);
            towerService.delTowerConfigCache(feedTowerLog.getDeviceNo());
        }
    }

    private static Boolean checkInitOutOfRange(Long old, Long newOne, double redundancyPercentage) {
        if(ObjectUtil.isEmpty(old)){
            return true;
        }
        double thresholdUp = old * (1 + (redundancyPercentage / 100));
        double thresholdDown = old * (1 - (redundancyPercentage / 100));
        if (newOne > thresholdUp || newOne < thresholdDown) {
            return true;
        } else {
            return false;
        }
    }




    /**
     * 计算平均空腔体积
     * @param feedTowerLog
     * @param deviceInitCheck
     * @return
     */
    private long getVolumeAverage(FeedTowerLog feedTowerLog, DeviceInitCheck deviceInitCheck) {
        LambdaQueryWrapper<FeedTowerLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FeedTowerLog::getInitId,feedTowerLog.getInitId());
        List<FeedTowerLog> feedTowerLogs = feedTowerLogMapper.selectList(lambdaQueryWrapper);
        long sum = feedTowerLogs.stream().collect(Collectors.summarizingLong(FeedTowerLog::getVolumeBase)).getSum();
        long count =  deviceInitCheck.getCheckCount() != 0 ? deviceInitCheck.getCheckCount() : 1L;
        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count), 0, RoundingMode.HALF_UP).longValue();
    }

}
