package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.jaemon.dinger.DingerSender;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.utils.JsonUtil;
import com.zmu.cloud.commons.annotations.RedissonDistributedLock;
import com.zmu.cloud.commons.card.CardApi;
import com.zmu.cloud.commons.constants.FourGCardConstants;
import com.zmu.cloud.commons.constants.JpushConstants;
import com.zmu.cloud.commons.dinger.FourGCardDinger;
import com.zmu.cloud.commons.dto.CardChargeDTO;
import com.zmu.cloud.commons.dto.OneCardCharge;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.entity.admin.Company;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.FourGService;
import com.zmu.cloud.commons.service.PushMessageTaskService;
import com.zmu.cloud.commons.service.SysUserService;
import com.zmu.cloud.commons.service.TowerService;
import com.zmu.cloud.commons.sphservice.SphEmployService;
import com.zmu.cloud.commons.utils.*;
import com.zmu.cloud.commons.vo.PigFarm4GCardInfoVO;
import com.zmu.cloud.commons.vo.PigFarm4GCardOneInfoVO;
import com.zmu.cloud.commons.vo.PigFarm4GVO;
import com.zmu.cloud.commons.vo.PushMessageTaskCountVO;
import com.ztwj.consts.Operator;
import com.ztwj.data.GetCardMemberDetailsData;
import com.ztwj.requests.handle.RechargePackageRequest;
import com.ztwj.requests.query.GetCardMemberDetailsRequest;
import com.ztwj.requests.query.GetPackageRechargeListRequest;
import com.ztwj.responses.BaseResponse;
import com.ztwj.responses.handle.RechargePackageResponse;
import com.ztwj.responses.query.GetCardMemberDetailsResponse;
import com.ztwj.responses.query.GetPackageRechargeListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FourGServiceImpl implements FourGService {
    final PushMessageTaskService pushMessageTaskService;
    final OrderDetailMapper orderDetailMapper;
    final PasswordEncoder passwordEncoder;
    final PigFarmMapper pigFarmMapper;
    final CompanyMapper companyMapper;
    final BestPayService bestPayService;
    final SysUserService sysUserService;
    private final SphEmployService employService;
    final TowerService towerService;
    final FeedTowerMapper feedTowerMapper;
    final BaseOrderMapper baseOrderMapper;
    final FourGCardDinger fourGCardDinger;
    final DingerSender dingerSender;
    final SkuMapper skuMapper;
    final CardApi cardApi;




    @Override
    public PigFarm4GVO getFarmBalance(Long farmId) {
        PigFarm pigfarm = pigFarmMapper.selectById(farmId);
        PigFarm4GVO pigFarm4GVO = new PigFarm4GVO();
        BeanUtils.copyProperties(pigfarm,pigFarm4GVO);
        PigFarm4GCardInfoVO cards = getFarmCards(pigfarm.getId(), null);
        pigFarm4GVO.setCardNum(cards.getCards()==null?0:cards.getCards().size());
        //预计可用多少月 余额/（卡片总数*月套餐价格)
        PigFarm4GCardInfoVO farmCards = getFarmCards(farmId,FourGCardWeStatusEnum.Enable);
        List<PigFarm4GCardOneInfoVO> cardsEnable = farmCards.getCards();
        if (ObjectUtil.isNotEmpty(cardsEnable)){
            LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
            skuWrapper.eq(Sku::getPackageType,SkuTypeEnum.BASE.getType());
            Sku sku = skuMapper.selectOne(skuWrapper);
            Integer  remainMonth = pigFarm4GVO.getBalance()/(cardsEnable.size() * sku.getPrice());
            pigFarm4GVO.setEnableRemain(remainMonth);
        }
        return pigFarm4GVO;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public PayResponse createChargeOrder(BestPayTypeEnum payType,Double amount,String openid,String buyerLogonId,String buyerId) {
        //支付请求参数
        //创建本地订单
        Integer balance = getFarmBalance(getFarmId()).getBalance();
        String orderId = SnowflakeIdUtil.getSnowflakeIdString();
        //计算手续费
        int other = 0;
        //手续费比例(单位:千分之)
        Integer otherPercent = 0;
        switch (payType){
            case ALIPAY_APP:
            case ALIPAY_PC:
            case ALIPAY_WAP:
            case ALIPAY_H5:
            case ALIPAY_QRCODE:
            case ALIPAY_BARCODE:
                otherPercent = PayOtherPercentEnum.ALIPAY.getPercent();
                other = ZmPayUtil.otherCalculate(ZmPayUtil.yuanToFen(amount),PayOtherPercentEnum.ALIPAY.getPercent());
                break;
            case WXPAY_MP:
            case WXPAY_MWEB:
            case WXPAY_NATIVE:
            case WXPAY_MINI:
            case WXPAY_APP:
            case WXPAY_MICRO:
                otherPercent = PayOtherPercentEnum.WECHAT.getPercent();
                other = ZmPayUtil.otherCalculate(ZmPayUtil.yuanToFen(amount),PayOtherPercentEnum.WECHAT.getPercent());
                break;
            case COMPANY_CHARGE:
                otherPercent = PayOtherPercentEnum.COMPANY.getPercent();
                other = ZmPayUtil.otherCalculate(ZmPayUtil.yuanToFen(amount),PayOtherPercentEnum.COMPANY.getPercent());
                break;
        }
        //计算最后的总额
        int total = ZmPayUtil.yuanToFen(amount) + other;
        //充值完成后的余额
        int balanceAfter = balance + ZmPayUtil.yuanToFen(amount);
        BaseOrder order = BaseOrder.builder()
                .amount(ZmPayUtil.yuanToFen(amount))
                .otherPercent(otherPercent)
                .other(other)
                .total(total)
                .orderStatus(PayStatusEnum.NOTPAY.getStatus())
                .weStatus(WePayStatusEnum.PENDING.getStatus())
                .type(OrderTypeEnum.CHARGE.getType())
                .balanceBefore(balance)
                .balanceAfter(balanceAfter)
                .orderCode(orderId)
                .companyId(getCompanyId())
                .pigFarmId(getFarmId())
                .userId(getUserId())
                .paymentType(payType.getCode())
                .createTime(new Date()).build();
        baseOrderMapper.insert(order);
        PayRequest request = new PayRequest();
        request.setPayTypeEnum(payType);
        request.setOrderId(orderId);
        request.setOrderAmount(ZmPayUtil.fenToYuan(total));
        request.setOrderName(String.format("%s余额充值",getFarmName()));
        request.setOpenid(openid);
        request.setAttach(String.valueOf(getFarmId()));
        if (payType == BestPayTypeEnum.ALIPAY_H5) {
            request.setBuyerLogonId(buyerLogonId);
            request.setBuyerId(buyerId);
        }
        log.info("【发起支付】request={}", JsonUtil.toJson(request));
        PayResponse payResponse = bestPayService.pay(request);
        log.info("【发起支付】response={}", JsonUtil.toJson(payResponse));
        return payResponse;
    }


    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    @RedissonDistributedLock(prefix = "CHARGE",key = "CHARGE_LOCK")
    public PayResponse payNotifyHandler(String notifyData) {
        log.info("【异步通知】支付平台的数据request={}", notifyData);
        PayResponse response = bestPayService.asyncNotify(notifyData);
        log.info("【异步通知】处理后的数据data={}", JsonUtil.toJson(response));
        LambdaQueryWrapper<BaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseOrder::getOrderCode, response.getOrderId());
        BaseOrder order = baseOrderMapper.selectOne(wrapper);
        if (ObjectUtils.isEmpty(order)){
            throw new BaseException("回调订单号:{%s}不存在!");
        }
        //校验金额是否一致
        double orderAmount = response.getOrderAmount();
        if(ZmPayUtil.yuanToFen(orderAmount) != order.getTotal()){
            throw new BaseException("订单金额和实际支付金额不一致!");
        }
        if(PayStatusEnum.FINISHPAY.getStatus()==order.getOrderStatus() || WePayStatusEnum.PROCESSED.getStatus()==order.getOrderStatus()){
            throw new BaseException("订单已处理,请勿重复提交!");
        }
        Long pigFarmId = order.getPigFarmId();
        String attach = response.getAttach();
        if(pigFarmId != Long.parseLong(attach)){
            throw new BaseException("订单猪场和实际猪场和不一致!");
        }
        //增加余额
        PigFarm farm = pigFarmMapper.selectById(pigFarmId);
        Integer newBalance = farm.getBalance() + order.getAmount();
        farm.setBalance(newBalance);
        pigFarmMapper.updateById(farm);
        //修改订单表状态
        order.setPayCode(response.getOutTradeNo());
        order.setOrderStatus(PayStatusEnum.FINISHPAY.getStatus());
        order.setWeStatus(WePayStatusEnum.PROCESSED.getStatus());
        baseOrderMapper.updateById(order);
        //是否有待自动充值任务

        try {

        }catch (Exception e){
            log.error("检查是否有待自动充值任务失败");
            log.error(e.getMessage(),e);
        }

        //发送通知
        try {
            SysUser user = sysUserService.getById(order.getUserId());
            if(ObjectUtil.isNotEmpty(user)){
                fourGCardDinger.charge(farm.getName(),ZmPayUtil.fenToYuan(order.getAmount()),ZmPayUtil.fenToYuan(order.getTotal()),ZmPayUtil.fenToYuan(order.getOther()),user.getRealName(),user.getPhone(),ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
            }else{
                fourGCardDinger.charge(farm.getName(),ZmPayUtil.fenToYuan(order.getAmount()),ZmPayUtil.fenToYuan(order.getTotal()),ZmPayUtil.fenToYuan(order.getOther()),getUserName(),"无",ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
            }
        }catch (Exception e){
            log.error("发送企业微信通知失败!",e);
        }
        return response;
    }



    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    @RedissonDistributedLock(prefix = "CHARGE",key = "CHARGE_LOCK")
    public void payNotifyHandler(Long pigFarmId,double amount,String passWord) {
        if(!RoleUtils.isSuperAdmin(getUserId())){
            throw new BaseException("您没有操作权限!");
        }else{
            //校验密码
            SysUser user = sysUserService.getById(getUserId());
            if (!passwordEncoder.matches(passWord, user.getPassword())) {
                throw new BaseException("密码错误!");
            }
        }
        //增加余额(对公转账,管理员手动给猪场充值)
        //创建本地订单
        PigFarm farm = pigFarmMapper.selectById(pigFarmId);
        Integer balance = farm.getBalance();
        String orderId = SnowflakeIdUtil.getSnowflakeIdString();
        //计算手续费
        int other = 0;
        //手续费比例(单位:千分之)
        Integer otherPercent = 0;
        //计算最后的总额
        int total = ZmPayUtil.yuanToFen(amount) + other;
        //充值完成后的余额
        int balanceAfter = balance + ZmPayUtil.yuanToFen(amount);
        BaseOrder order = BaseOrder.builder()
                .amount(ZmPayUtil.yuanToFen(amount))
                .otherPercent(otherPercent)
                .other(other)
                .total(total)
                .orderStatus(PayStatusEnum.FINISHPAY.getStatus())
                .weStatus(WePayStatusEnum.PROCESSED.getStatus())
                .type(OrderTypeEnum.CHARGE.getType())
                .balanceBefore(balance)
                .balanceAfter(balanceAfter)
                .orderCode(orderId)
                .companyId(farm.getCompanyId())
                .pigFarmId(farm.getId())
                .userId(null)
                .paymentType(BestPayTypeEnum.COMPANY_CHARGE.getCode())
                .createTime(new Date()).build();
        baseOrderMapper.insert(order);
        farm.setBalance(balanceAfter);
        pigFarmMapper.updateById(farm);
        //发送通知
        try {
            SysUser user = sysUserService.getById(getUserId());
            if(ObjectUtil.isNotEmpty(user)){
                fourGCardDinger.chargeCompany(farm.getName(),ZmPayUtil.fenToYuan(order.getAmount()),ZmPayUtil.fenToYuan(order.getTotal()),ZmPayUtil.fenToYuan(order.getOther()),user.getRealName(),user.getPhone(),ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
            }else{
                fourGCardDinger.chargeCompany(farm.getName(),ZmPayUtil.fenToYuan(order.getAmount()),ZmPayUtil.fenToYuan(order.getTotal()),ZmPayUtil.fenToYuan(order.getOther()),getUserName(),"无",ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
            }
        }catch (Exception e){
            log.error("发送企业微信通知失败!",e);
        }
    }


    @Override
    public PigFarm4GCardInfoVO getFarmCards(Long farmId,FourGCardWeStatusEnum status) {
        //查询猪场所有的4G卡
        PigFarm4GCardInfoVO pigFarm4GCardInfoVO = new PigFarm4GCardInfoVO();
        PigFarm pigfarm = pigFarmMapper.selectById(farmId);
        //预防资源为null
//        if(ObjectUtil.isEmpty(pigfarm)){
        if("1".equals("1")){
            return pigFarm4GCardInfoVO;
        }
//        }
        BeanUtils.copyProperties(pigfarm,pigFarm4GCardInfoVO);
        String ids = towerService.farmAllCardIds(farmId);
        Map<String, FeedTower> towerMap = towerService.farmAllTowerCard(farmId);
        if(ObjectUtils.isEmpty(ids)){
            List<PigFarm4GCardOneInfoVO> pigFarm4GCardOneInfoVOList = new ArrayList<>();
            pigFarm4GCardInfoVO.setCards(pigFarm4GCardOneInfoVOList);
            return pigFarm4GCardInfoVO;
        }
        GetCardMemberDetailsRequest request = new GetCardMemberDetailsRequest();
        request.setCardIds(ids);
        request.setOperator(Operator.MOBILE);
        GetCardMemberDetailsResponse response = cardApi.cardClient().execute(request);
        Boolean flag = checkResponse(response);
        if(!flag){
            return pigFarm4GCardInfoVO;
        }
        List<GetCardMemberDetailsData> cards = response.getData();
        //补充料塔设备相关信息
        List<PigFarm4GCardOneInfoVO> list = new ArrayList<>();
        cards.forEach(oneData->{
            PigFarm4GCardOneInfoVO one = new PigFarm4GCardOneInfoVO();
            BeanUtils.copyProperties(oneData,one);
            FeedTower feedTower = towerMap.get(oneData.getCardId());
            one.setTowerId(feedTower.getId());
            one.setDeviceNo(feedTower.getDeviceNo());
            one.setTowerName(feedTower.getName());
            one.setIccid(feedTower.getIccid());
            list.add(one);
        });
        //过滤卡片状态
        if(!ObjectUtils.isEmpty(status)){
            List<PigFarm4GCardOneInfoVO> filterCards = null;
            switch (status.getStatus()) {
                case 1://正常(使用中)
                    filterCards = list.stream().filter(one -> Integer.parseInt(one.getSimStatus()) == FourGCardStatusEnum.ACTIVATED.getStatus()).collect(Collectors.toList());
                    break;
                case 2://流量不足以及流量用超停机
                    LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Sku::getPackageType,SkuTypeEnum.EXT.getType());
                    List<Sku> skus = skuMapper.selectList(wrapper);
//                    filterCards = list.stream().filter(one ->ObjectUtils.isNotEmpty(one.getExpiredTime()) &&  new Date().getTime()<=ZmDateUtil.forMateStringToYearMonthDayDate(one.getExpiredTime()).getTime() &&  Double.parseDouble(one.getRemainingData())<= FourGCardConstants.WARNING_REMAIN).peek(one->one.setSku(skus)).collect(Collectors.toList());
                    filterCards = list.stream().filter(one -> Integer.parseInt(one.getSimStatus()) == FourGCardStatusEnum.ACTIVATED.getStatus()).peek(one->{one.setSku(skus);one.setSimStatus(FourGCardStatusEnum.DISABLED.getStatus()+"");}).collect(Collectors.toList());

                    break;
                case 3://已停机(流量用超)
                    filterCards = list.stream().filter(one ->ObjectUtils.isNotEmpty(one.getExpiredTime()) && new Date().getTime()<=ZmDateUtil.forMateStringToYearMonthDayDate(one.getExpiredTime()).getTime() &&  Double.parseDouble(one.getRemainingData())<=0D && Integer.parseInt(one.getSimStatus()) == FourGCardStatusEnum.DISABLED.getStatus()).collect(Collectors.toList());
                    break;
                case 4://已停机(套餐到期)
                    LambdaQueryWrapper<Sku> wrapper1 = new LambdaQueryWrapper<>();
                    wrapper1.eq(Sku::getPackageType,SkuTypeEnum.BASE.getType());
                    List<Sku> skus1 = skuMapper.selectList(wrapper1);
//                    filterCards = list.stream().filter(one ->ObjectUtils.isNotEmpty(one.getExpiredTime()) && new Date().getTime()>ZmDateUtil.forMateStringToYearMonthDayDate(one.getExpiredTime()).getTime() && Integer.parseInt(one.getSimStatus()) == FourGCardStatusEnum.DISABLED.getStatus()).peek(one->one.setSku(skus1)).collect(Collectors.toList());
                    filterCards = list.stream().filter(one -> Integer.parseInt(one.getSimStatus()) == FourGCardStatusEnum.ACTIVATED.getStatus()).peek(one->{one.setSku(skus1);one.setSimStatus(FourGCardStatusEnum.DISABLED.getStatus()+"");}).collect(Collectors.toList());
                    break;
                case 5://已失效
                    filterCards = list.stream().filter(one -> Integer.parseInt(one.getSimStatus()) == FourGCardStatusEnum.STOP.getStatus()).collect(Collectors.toList());
                    break;
                case 6://本月到期(待充月基础套餐) 此种情况是判断是否需要进行自动充值月租,也就是当前时间+提前天数阈值 >卡片到期时间
                    filterCards = list.stream().filter(one ->ObjectUtils.isNotEmpty(one.getExpiredTime()) && ZmDateUtil.calculationDate(new Date(),FourGCardConstants.REMAIN_DATE).getTime()>=ZmDateUtil.forMateStringToYearMonthDayDate(one.getExpiredTime()).getTime() ).collect(Collectors.toList());
                    break;

                case 7://除未激活和作废的卡片以外的所有卡
                    filterCards = list.stream().filter(one ->Integer.parseInt(one.getSimStatus()) != FourGCardStatusEnum.ACTIVATE.getStatus() && Integer.parseInt(one.getSimStatus()) != FourGCardStatusEnum.STOP.getStatus()).collect(Collectors.toList());
                    break;
            }
            pigFarm4GCardInfoVO.setCards(filterCards);
        }else{
            pigFarm4GCardInfoVO.setCards(list.stream().sorted((o1, o2) -> Collator.getInstance(Locale.SIMPLIFIED_CHINESE).compare(o2.getSimStatus(),o1.getSimStatus())).collect(Collectors.toList()));
        }
        return pigFarm4GCardInfoVO;
    }


    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    @RedissonDistributedLock(prefix = "BUY",key = "#farmId")
    public void chargeCard(List<OneCardCharge> chargeCard, OrderTypeEnum orderTypeEnum, String passWord, Boolean auto, Boolean sph, HttpServletRequest request, Long userId, Long farmId) {
        //密码校验
        if(!auto){
            if(!sph){
                SysUser user = sysUserService.getById(userId);
                if (!passwordEncoder.matches(passWord, user.getPassword())) {
                    throw new BaseException("密码错误!");
                }
            }else{
                //智慧猪家用户从缓存中获取加密后密码比对
                Map<String, Object> info = employService.info(request);
                String password = (String)info.get("password");
                if(ObjectUtil.isEmpty(password)){
                    throw new BaseException("缓存异常!请联系管理员!");
                }
                passWord = DigestUtils.md5Hex(passWord.getBytes(StandardCharsets.UTF_8));
                if (!password.equals(passWord)) {
                    throw new BaseException("密码错误!");
                }
            }
        }else{
            //防止重复自动下单
            LambdaQueryWrapper<BaseOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BaseOrder::getPigFarmId,farmId);
            wrapper.eq(BaseOrder::getType,OrderTypeEnum.AUTO.getType());
            List<BaseOrder> baseOrders = baseOrderMapper.selectList(wrapper);
            if(ObjectUtils.isNotEmpty(baseOrders)){
                throw new BaseException(String.format("猪场id:{%d}已存在待处理的自动下单任务!",farmId));
            }
        }

        //订单入库
        PigFarm pigFarm = pigFarmMapper.selectById(farmId);
        Company company = companyMapper.selectById(pigFarm.getCompanyId());
        String orderId = SnowflakeIdUtil.getSnowflakeIdString();
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        chargeCard.forEach(oneCard->{
            String orderNo = SnowflakeIdUtil.getSnowflakeIdString();
            //获取套餐
            Sku sku = skuMapper.selectById(oneCard.getSkuId());
            OrderDetail detail = OrderDetail.builder()
                    .orderCode(orderId)
                    .iccid(oneCard.getIccid())
                    .towerId(oneCard.getTowerId())
                    .skuId(oneCard.getSkuId())
                    .actualPrice(sku.getPrice())
                    .orderno(orderNo)
                    .num(1)
                    .weStatus(WePayStatusEnum.PENDING.getStatus())
                    .createTime(new Date())
                    .build();
            orderDetailList.add(detail);
        });
        //计算订单总额
        int  amount = orderDetailList.stream().mapToInt(OrderDetail::getActualPrice).sum();
        int balance = getFarmBalance(farmId).getBalance();
        //判断余额是否充足

        if(balance<amount){
            if(auto){
                List<PushMessageTaskCountVO> pmtcList = new ArrayList<>();
                PushMessageTaskCountVO pushMessageTaskCountVO = PushMessageTaskCountVO.builder()
                        .farmId(farmId)
                        .companyId(company.getId())
                        .companyName(company.getName())
                        .farmName(pigFarm.getName())
                        .need(amount)
                        .balance(balance)
                        .build();
                pmtcList.add(pushMessageTaskCountVO);
                pushMessageTaskService.createBalanceWarningPush(pmtcList, JpushConstants.ZM_CLOUD);
                return;
            }
            throw new BaseException("余额不足,请先充值!");
        }
        log.info(String.format("猪场:{%s}:余额充足,开始自动充值月基础套餐!",pigFarm.getName()));
        //购买完成后的余额
        int balanceAfter = balance - amount;
        //扣除余额
        pigFarm.setBalance(balanceAfter);
        pigFarmMapper.updateById(pigFarm);
        //余额充足才会创建订单
        for (OrderDetail oneDetail : orderDetailList) {
            orderDetailMapper.insert(oneDetail);
        }
        BaseOrder order = BaseOrder.builder()
                .amount(amount)
                .otherPercent(PayOtherPercentEnum.BALANCE.getPercent())
                .other(0)
                .total(amount)
                .orderStatus(PayStatusEnum.FINISHPAY.getStatus())
                .weStatus(WePayStatusEnum.PENDING.getStatus())
                .type(orderTypeEnum.getType())
                .balanceBefore(balance)
                .balanceAfter(balanceAfter)
                .orderCode(orderId)
                .companyId(pigFarm.getCompanyId())
                .pigFarmId(farmId)
                .userId(userId)
                .paymentType(BestPayTypeEnum.BALANCE_PAY.getCode())
                .createTime(new Date()).build();
        baseOrderMapper.insert(order);
        try {
            SysUser user = sysUserService.getById(userId);
            if(!auto){
                if(ObjectUtil.isNotEmpty(user)){
                    fourGCardDinger.createOrder(pigFarm.getName(),orderTypeEnum.getDesc(),orderDetailList.size(),ZmPayUtil.fenToYuan(order.getTotal()),order.getOrderCode(),user.getRealName(),user.getPhone(),ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
                }else{
                    fourGCardDinger.createOrder(pigFarm.getName(),orderTypeEnum.getDesc(),orderDetailList.size(),ZmPayUtil.fenToYuan(order.getTotal()),order.getOrderCode(),getUserName(),"无",ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
                }
            }else{
                if(ObjectUtil.isNotEmpty(user)){
                    fourGCardDinger.createOrderAuto(pigFarm.getName(),orderTypeEnum.getDesc(),orderDetailList.size(),ZmPayUtil.fenToYuan(order.getTotal()),order.getOrderCode(),ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
                }else{
                    fourGCardDinger.createOrder(pigFarm.getName(),orderTypeEnum.getDesc(),orderDetailList.size(),ZmPayUtil.fenToYuan(order.getTotal()),order.getOrderCode(),getUserName(),"无",ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
                }
            }
        }catch (Exception e){
            log.error(String.format("发送猪场:{%s}下单通知失败!",pigFarm.getName()));
            log.error(e.getMessage(),e);
        }
    }


    @Override
    public void cardChargeTask() {
        //卡片充值业务[套餐购买]办理
        LambdaQueryWrapper<BaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseOrder::getOrderStatus,PayStatusEnum.FINISHPAY.getStatus());
        wrapper.eq(BaseOrder::getWeStatus,WePayStatusEnum.PENDING.getStatus());
        wrapper.orderByAsc(BaseOrder::getId);
        List<BaseOrder> orderList = baseOrderMapper.selectList(wrapper);
        if(ObjectUtils.isEmpty(orderList)){
            log.info("没有待处理的4G卡充值业务订单!");
            return;
        }else{
            log.info(String.format("查询到%d条4G卡充值业务待处理订单,开始执行操作!",orderList.size()));
        }
        orderList.forEach(oneOrder->{
            LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
            orderDetailWrapper.eq(OrderDetail::getOrderCode,oneOrder.getOrderCode());
            orderDetailWrapper.eq(OrderDetail::getWeStatus,WePayStatusEnum.PENDING.getStatus());
            List<OrderDetail> orderDetails = orderDetailMapper.selectList(orderDetailWrapper);
            //购买套餐
            if(!ObjectUtils.isEmpty(orderDetails)){
                orderDetails.forEach(oneDetail->{
                    RechargePackageRequest request = new RechargePackageRequest();
                    //查询套餐表里面的详情
                    Sku sku = skuMapper.selectById(oneDetail.getSkuId());
                    request.setCardId(oneDetail.getIccid());
                    request.setOrderNo(oneDetail.getOrderno());
                    request.setPackageCode(sku.getPackageCode());
                    request.setPackageType(sku.getPackageType());
                    request.setRechargeType(SkuChargeTypeEnum.CURRENT.getType());
                    RechargePackageResponse response = cardApi.cardClient().execute(request);
                    if(response != null && "0".equals(response.getCode())){
                        log.info(String.format("订单:%s的子订单%s物联卡号:%s开始充值!",oneOrder.getOrderCode(),oneDetail.getOrderCode(),oneDetail.getIccid()));
                        //修改订单详情状态为处理中
                        oneDetail.setWeStatus(WePayStatusEnum.PROCESSING.getStatus());
                        orderDetailMapper.updateById(oneDetail);
                    }
                });
            }else{
                //order是待处理,但是oder_detail已经没有待处理任务,证明所有detail已经进入处理中状态
                //查询所有充值的卡片的充值是否到账
                LambdaQueryWrapper<OrderDetail> orderDetailProccessWrapper = new LambdaQueryWrapper<>();
                orderDetailProccessWrapper.eq(OrderDetail::getOrderCode,oneOrder.getOrderCode());
                orderDetailProccessWrapper.eq(OrderDetail::getWeStatus,WePayStatusEnum.PROCESSING.getStatus());
                List<OrderDetail> orderProcessDetails = orderDetailMapper.selectList(orderDetailProccessWrapper);
                if(!ObjectUtils.isEmpty(orderProcessDetails)){
                    orderProcessDetails.forEach(oneProcessOrder->{
                        GetPackageRechargeListRequest request = new GetPackageRechargeListRequest();
                        request.setOrderNo(oneProcessOrder.getOrderno());
                        GetPackageRechargeListResponse response = cardApi.cardClient().execute(request);
                        if(response != null && "0".equals(response.getCode()) && "1".equals(response.getData().getStatus())){
                            //如果充值到账,修改状态
                            oneProcessOrder.setWeStatus(WePayStatusEnum.PROCESSED.getStatus());
                            orderDetailMapper.updateById(oneProcessOrder);
                            log.info(String.format("订单:%s的子订单%s物联卡号:%s已充值到账!",oneOrder.getOrderCode(),oneProcessOrder.getOrderCode(),oneProcessOrder.getIccid()));
                        }
                    });
                }else{
                    //如果也没有处理中的detail,那么证明该oder的所有detail已经全部完成
                    //修改order状态为完成
                    oneOrder.setWeStatus(WePayStatusEnum.PROCESSED.getStatus());
                    baseOrderMapper.updateById(oneOrder);
                    log.info(String.format("订单:%s的所有物联卡已全部充值到账!该订单已完成!",oneOrder.getOrderCode()));
                    PigFarm pigFarm = pigFarmMapper.selectById(oneOrder.getPigFarmId());
                    try {
                        fourGCardDinger.orderFinish(pigFarm.getName(),OrderTypeEnum.getType(oneOrder.getType()).getDesc(),oneOrder.getOrderCode(),ZmDateUtil.localDateTimeToString(LocalDateTime.now()));
                    }catch (Exception e){
                        log.error(String.format("发送猪场:{%s}订单完成通知失败!",pigFarm.getName()));
                    }
                }
            }
        });
    }


    @Override
    public void cardAutoChargeTask() {
        //卡片月租自动续费任务
        log.info("=======卡片月租自动续费任务开始执行!=====");
        Map<Long, List<FeedTower>> farmTowerMap = getFarmTowerMap();
        if(ObjectUtils.isEmpty(farmTowerMap)){
            log.info("=====所有猪场都没有待充值月基础套餐的卡片! 任务结束!====");
            return;
        }
        //每个猪场进行操作
        farmTowerMap.forEach((farmId,FeedTowerList)->{
            PigFarm4GCardInfoVO farmCards = getFarmCards(farmId, FourGCardWeStatusEnum.CURRENTMONTHTIMEOUT);
            List<PigFarm4GCardOneInfoVO> cards = farmCards.getCards();
            if(ObjectUtils.isEmpty(cards)){
                log.info(String.format("猪场:{%s}:没有待充值月基础套餐的卡片",farmCards.getName()));
                return;
            }
            LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
            skuWrapper.eq(Sku::getPackageType,SkuTypeEnum.BASE.getType());
            Sku sku = skuMapper.selectOne(skuWrapper);
            //构建订单
            List<OneCardCharge> chargeDTOList = new ArrayList<>();
            cards.forEach(oneCard->{
                OneCardCharge cardChargeDTO = new OneCardCharge();
                cardChargeDTO.setTowerId(oneCard.getTowerId());
                cardChargeDTO.setIccid(oneCard.getIccid());
                cardChargeDTO.setSkuId(sku.getId());
                chargeDTOList.add(cardChargeDTO);
            });
            //下单
            try {
                chargeCard(chargeDTOList,OrderTypeEnum.AUTO,null,true,false,null,null,farmCards.getId());
            }catch (Exception e){
                log.error(String.format("猪场:{%s}自动充值月基础套餐下单失败!",farmCards.getName()));
                log.error(e.getMessage(), e);
            }
        });
    }


    @Override
    public Map<Long, List<FeedTower>> getFarmTowerMap() {
        //查询所有的料塔然后按照猪场分组
        LambdaQueryWrapper<FeedTower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTower::getDel, 0);
        wrapper.eq(FeedTower::getEnable, 0);
        wrapper.isNotNull(FeedTower::getIccid);
        List<FeedTower> feedTowers = feedTowerMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(feedTowers)) {
            log.info("没有查询到符合要求的料塔!");
            return null;
        }
        return feedTowers.stream().filter(x -> ObjectUtils.isNotEmpty(x.getPigFarmId())).collect(Collectors.groupingBy(FeedTower::getPigFarmId));
    }

    @Override
    public Sku getSkuInfo(SkuTypeEnum type) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getPackageType,type.getType());
        return skuMapper.selectOne(wrapper);
    }


    public Long getUserId(){
        return RequestContextUtils.getUserId();
    }

    public String getUserName(){
        return RequestContextUtils.getRequestInfo().getLoginAccount();
    }

    public Long getCompanyId(){
        return RequestContextUtils.getRequestInfo().getCompanyId();
    }

    public Long getFarmId(){
        return RequestContextUtils.getRequestInfo().getPigFarmId();
    }
    public String getFarmName(){
        return pigFarmMapper.selectById(RequestContextUtils.getRequestInfo().getPigFarmId()).getName();
    }
    private Boolean checkResponse(BaseResponse baseResponse){
        Boolean flag = true;
        if(ObjectUtils.isEmpty(baseResponse)){
//            throw new BaseException("4G接口调用失败!");
            log.error("4G接口调用失败!");
            flag = false;
        }
        if(!"0".equals(baseResponse.getCode())){
            log.error(String.format("4G接口调用异常! 提示: {%s}",baseResponse.getMessage()));
            flag = false;
//            throw new BaseException(String.format("4G接口调用异常! 提示: {%s}",baseResponse.getMessage()));
        }
        return flag;
    }
}
