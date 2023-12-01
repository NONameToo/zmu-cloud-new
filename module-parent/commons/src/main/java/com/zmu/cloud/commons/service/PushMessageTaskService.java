package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.vo.PushMessageTaskCountVO;

import java.util.List;

/**
 * @Description //消息推送定时任务Service
 * @Date 2022/11/18 14:16
 * @Param
 * @Return
 **/
public interface PushMessageTaskService {
    //生产预警推送
    void createBasePush(String appName);

    //4G卡片流量不足预警推送
    void createCardDataWarningPush(String appName);

    //猪场余额不足预警推送[该推送不单独设定任务,根据月租扣款情况触发,推送给猪场和客服]
    void createBalanceWarningPush(List<PushMessageTaskCountVO> pmtcList, String appName);

}
