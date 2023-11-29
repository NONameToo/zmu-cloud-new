package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jaemon.dinger.DingerSender;
import com.zmu.cloud.commons.dinger.FourGCardDinger;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.entity.PigMatingTask;
import com.zmu.cloud.commons.entity.PushMessage;
import com.zmu.cloud.commons.entity.admin.Company;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.FourGCardWeStatusEnum;
import com.zmu.cloud.commons.jpush.JPushApi;
import com.zmu.cloud.commons.jpush.JPushMessage;
import com.zmu.cloud.commons.jpush.JpushTagTypeEnum;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.FourGService;
import com.zmu.cloud.commons.service.PushMessageTaskService;
import com.zmu.cloud.commons.utils.ZmDateUtil;
import com.zmu.cloud.commons.utils.ZmPayUtil;
import com.zmu.cloud.commons.vo.PigFarm4GCardInfoVO;
import com.zmu.cloud.commons.vo.PigFarm4GCardOneInfoVO;
import com.zmu.cloud.commons.vo.PushMessageTaskCountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zmu.cloud.commons.constants.JpushConstants.*;


@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_= {@Lazy})
public class PushMessageTaskServiceImpl extends ServiceImpl<PigMatingTaskMapper, PigMatingTask> implements PushMessageTaskService {

    final PigMatingTaskMapper pigMatingTaskMapper;
    final PigPregnancyTaskMapper pigPregnancyTaskMapper;
    final PigLaborTaskMapper pigLaborTaskMapper;
    final PigWeanedTaskMapper pigWeanedTaskMapper;
    final PushUserTypeMapper pushUserTypeMapper;
    final PushMessageMapper pushMessageMapper;
    final FourGService fourGService;
    final PigFarmMapper pigFarmMapper;
    final CompanyMapper companyMapper;
    final DingerSender dingerSender;
    final FourGCardDinger fourGCardDinger;
    final JPushApi jPushApi;


    /**
     * 生产预警推送
     */
    @Override
    public void createBasePush(String appName) {
        //待配种
        createPushMethod(JpushTagTypeEnum.MATING, appName);
        //待妊检
        createPushMethod(JpushTagTypeEnum.PREGNANCY,appName);
        //待分娩
        createPushMethod(JpushTagTypeEnum.LABOR,appName);
        //待断奶
        createPushMethod(JpushTagTypeEnum.WEANED,appName);
    }


    /**
     * 4G卡片流量不足预警推送
     */
    @Override
    public void createCardDataWarningPush(String appName) {
        //4G卡片流量不足预警推送
        createOverFlowDataCardPushMethod(appName);
    }

    /**
     * 猪场余额不足预警推送[该推送不单独设定任务,根据月租扣款情况触发,推送给猪场和客服]
     */
    @Override
    public void createBalanceWarningPush(List<PushMessageTaskCountVO>pmtcList, String appName) {
        //猪场余额不足预警推送
        createBalanceWarningPushMethod(pmtcList,appName);
    }



    /**
     * 生产预警推送Method
     */
    private void createPushMethod(JpushTagTypeEnum jpushTagTypeEnum,String appName) {
        //查询需要待发送的农场和公司及待处理消息数
        List<PushMessageTaskCountVO> pmtcList=null;
        switch (appName){
            case "云慧养":
                switch (jpushTagTypeEnum.getType()){
                    case "Mating":
                        pmtcList = pigMatingTaskMapper.selectPushMessageTaskCount(jpushTagTypeEnum.getType());
                        break;
                    case "Pregnancy":
                        pmtcList = pigPregnancyTaskMapper.selectPushMessageTaskCount(jpushTagTypeEnum.getType());
                        break;
                    case "Labor":
                        pmtcList = pigLaborTaskMapper.selectPushMessageTaskCount(jpushTagTypeEnum.getType());
                        break;
                    case "Weaned":
                        pmtcList = pigWeanedTaskMapper.selectPushMessageTaskCount(jpushTagTypeEnum.getType());
                        break;
                }
                break;
            case "智慧猪家":
                break;
        }
        pushExecutorHandler(pmtcList,jpushTagTypeEnum,appName);
    }



    /**
     * 4G卡片流量不足预警推送执行方法
     */
    private void createOverFlowDataCardPushMethod(String appName){
        //卡片月租自动续费任务
        log.info("=======4G卡片流量不足预警推送任务开始执行!=====");
        Map<Long, List<FeedTower>> farmTowerMap = fourGService.getFarmTowerMap();
        if(ObjectUtils.isEmpty(farmTowerMap)){
            log.info("=====所有猪场都没有流量不足预警的卡片! 任务结束!====");
            return;
        }
        List<PushMessageTaskCountVO> pmtcList=new ArrayList<>();
        //每个猪场进行操作
        farmTowerMap.forEach((farmId,FeedTowerList)->{
            PigFarm4GCardInfoVO cards = fourGService.getFarmCards(farmId, FourGCardWeStatusEnum.WARNINGDATE);
            List<PigFarm4GCardOneInfoVO> cardsInner = cards.getCards();
            PigFarm pigFarm = pigFarmMapper.selectById(farmId);
            Company company = companyMapper.selectById(pigFarm.getCompanyId());
            long total;
            if(ObjectUtils.isNotEmpty(cardsInner)){
                total = (Long.parseLong(cardsInner.size()+""));
                PushMessageTaskCountVO pushMessageTaskCountVO = PushMessageTaskCountVO.builder()
                        .total(total)
                        .companyId(company.getId())
                        .farmId(cards.getId())
                        .companyName(company.getName())
                        .farmName(pigFarm.getName())
                        .build();
                pmtcList.add(pushMessageTaskCountVO);
            }
        });
        pushExecutorHandler(pmtcList,JpushTagTypeEnum.DATAWARNING,appName);
        log.info("<<<<<<<4G卡片流量不足预警推送任务执行完毕!!>>>>>");
    }

    private void createBalanceWarningPushMethod(List<PushMessageTaskCountVO> pmtcList, String appName) {
        log.info("=======猪场余额不足预警推送任务开始执行!=====");
        pushExecutorHandler(pmtcList,JpushTagTypeEnum.BALANCEWARNING,appName);
        log.info("<<<<<<<猪场余额不足预警推送任务执行完毕!>>>>>");
    }


    private void pushExecutorHandler(List<PushMessageTaskCountVO> pmtcList,JpushTagTypeEnum jpushTagTypeEnum,String appName){
            if(ObjectUtils.isEmpty(pmtcList)){
                log.info(String.format("{%s}没有待推任务",jpushTagTypeEnum.getDesc()));
                return;
             }
            pmtcList.forEach(pmtc -> {
                //查询该农场下的所有用户,并过滤出订阅了对应消息的用户
                List<SysUser> userList = pushUserTypeMapper.getUserRidByFarmIdAndMessageTypeKey(pmtc.getFarmId(), jpushTagTypeEnum.getType());
                ArrayList<String> ridList = new ArrayList<>();
                userList.forEach(oneUser -> {
                    ridList.add(oneUser.getRid());
                });
                //发送消息
                JPushMessage jPushMessage = new JPushMessage();
                if(ObjectUtils.isNotEmpty(pmtc.getTotal())){
                    jPushMessage.setBadge(Integer.parseInt(pmtc.getTotal().toString()));
                }
                String messageTemplateFinal = "";
                String title = "";
                switch (jpushTagTypeEnum.getType()){
                    case "Mating":
                    case "Pregnancy":
                    case "Labor":
                    case "Weaned":
                        switch (appName) {
                            case "云慧养":
                                messageTemplateFinal = String.format(YHY_PUSH_MESSAGE_BASE_TEMPLATE, pmtc.getFarmName(),pmtc.getTotal(), jpushTagTypeEnum.getDesc());
                                title = String.format(YHY_PUSH_MESSAGE_BASE_TITLE, jpushTagTypeEnum.getDesc());
                                break;
                            case "智慧猪家":
                                messageTemplateFinal = String.format(SPH_PUSH_MESSAGE_BASE_TEMPLATE, pmtc.getFarmName(), pmtc.getTotal(), jpushTagTypeEnum.getDesc());
                                title = String.format(SPH_PUSH_MESSAGE_BASE_TITLE, jpushTagTypeEnum.getDesc());
                                break;
                        }
                        break;
                    case "DataWarning":
                        switch (appName) {
                            case "云慧养":
                                messageTemplateFinal = String.format(YHY_PUSH_MESSAGE_DATA_WARING_TEMPLATE, pmtc.getFarmName(), pmtc.getTotal());
                                title = String.format(YHY_PUSH_MESSAGE_DATA_WARING_TITLE, jpushTagTypeEnum.getDesc());
                                fourGCardDinger.dataWarning(pmtc.getFarmName(), pmtc.getTotal(), ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
                                break;
                            case "智慧猪家":
                                messageTemplateFinal = String.format(SPH_PUSH_MESSAGE_DATA_WARING_TEMPLATE, pmtc.getFarmName(), pmtc.getTotal());
                                title = String.format(SPH_PUSH_MESSAGE_DATA_WARING_TITLE, jpushTagTypeEnum.getDesc());
                                break;
                        }
                        break;
                    case "BalanceWarning":
                        switch (appName) {
                            case "云慧养":
                                messageTemplateFinal = String.format(YHY_PUSH_MESSAGE_BALANCE_WARING_TEMPLATE, pmtc.getFarmName(), ZmPayUtil.fenToYuan(pmtc.getNeed()),ZmPayUtil.fenToYuan(pmtc.getBalance()));
                                title = String.format(YHY_PUSH_MESSAGE_BALANCE_WARING_TITLE, jpushTagTypeEnum.getDesc());
                                fourGCardDinger.balanceWarning(pmtc.getFarmName(),ZmPayUtil.fenToYuan(pmtc.getNeed()),ZmPayUtil.fenToYuan(pmtc.getBalance()), ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
                                break;
                            case "智慧猪家":
                                messageTemplateFinal = String.format(SPH_PUSH_MESSAGE_BALANCE_WARING_TEMPLATE, pmtc.getFarmName(), ZmPayUtil.fenToYuan(pmtc.getNeed()), ZmPayUtil.fenToYuan(pmtc.getBalance()));
                                title = String.format(SPH_PUSH_MESSAGE_BALANCE_WARING_TITLE, jpushTagTypeEnum.getDesc());
                                break;
                        }
                        break;
                }
                jPushMessage.setContent(messageTemplateFinal);
                jPushMessage.setTitle(appName);
                HashMap<String, String> extras = new HashMap<>();
                extras.put("messageType", jpushTagTypeEnum.getType());
                extras.put("farmId", String.valueOf(pmtc.getFarmId()));
                extras.put("companyId", String.valueOf(pmtc.getCompanyId()));
                jPushMessage.setExtras(extras);
                if (ObjectUtils.isEmpty(ridList)) {
                    return;
                }
                Long aLong = jPushApi.pushToDevices(ridList, jPushMessage);
                if (ObjectUtils.isNotEmpty(aLong)) {
                    //发送成功
                    String finalMessageTemplateFinal = messageTemplateFinal;
                    String finalTitle = title;
                    userList.forEach(oneUser -> {
                        //使用统一的消息记录表
                        PushMessage pushMessage = new PushMessage();
                        pushMessage.setTitle(finalTitle);
                        pushMessage.setType(jpushTagTypeEnum.getType());
                        pushMessage.setBody(finalMessageTemplateFinal);
                        pushMessage.setUserId(oneUser.getId());
                        pushMessage.setStatus(0);
                        pushMessage.setPushType("NOTICE");
                        pushMessage.setRemark(String.valueOf(aLong));
                        pushMessage.setCompanyId(pmtc.getCompanyId());
                        pushMessageMapper.insert(pushMessage);
                    });
                    log.info(String.format("发送农场【%s】%s消息成功", pmtc.getFarmName(), jpushTagTypeEnum.getDesc()));
                } else {
                    //发送失败
                    log.error(String.format("发送农场【%s】%s消息失败", pmtc.getFarmName(), jpushTagTypeEnum.getDesc()));
                }
        });
    }
}
