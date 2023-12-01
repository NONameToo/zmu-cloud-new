package com.zmu.cloud.commons.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.zmu.cloud.commons.dto.CardChargeDTO;
import com.zmu.cloud.commons.dto.OneCardCharge;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.Sku;
import com.zmu.cloud.commons.enums.FourGCardWeStatusEnum;
import com.zmu.cloud.commons.enums.OrderTypeEnum;
import com.zmu.cloud.commons.enums.SkuTypeEnum;
import com.zmu.cloud.commons.vo.PigFarm4GCardInfoVO;
import com.zmu.cloud.commons.vo.PigFarm4GVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


public interface FourGService{

    PigFarm4GVO getFarmBalance(Long farmId);
    PayResponse createChargeOrder(BestPayTypeEnum payType, Double amount, String openid,String buyerLogonId,String buyerId);
    PayResponse payNotifyHandler(String notifyData);
    void payNotifyHandler(Long pigFarmId,double amount,String passWord);
    PigFarm4GCardInfoVO getFarmCards(Long farmId,FourGCardWeStatusEnum status);
    void chargeCard(List<OneCardCharge> chargeCard, OrderTypeEnum orderTypeEnum, String passWord, Boolean auto, Boolean sph,  HttpServletRequest request,Long userId, Long farmId);
    void cardChargeTask();
    void cardAutoChargeTask();
    Map<Long, List<FeedTower>> getFarmTowerMap();
    Sku getSkuInfo(SkuTypeEnum type);
}
