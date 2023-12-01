package com.zmu.cloud.commons.dinger;

import com.github.jaemon.dinger.core.annatations.DingerMarkdown;
import com.github.jaemon.dinger.core.annatations.Parameter;
import com.github.jaemon.dinger.core.entity.DingerResponse;

// wetalk不支持markdown格式的`@`功能
public interface FourGCardDinger {
    //客户充值
    @DingerMarkdown(
            value = "#### <font color=\"info\">【客户充值】</font>  \n" +
                    ">猪场:${farmName}\n" +
                    ">操作类型：充值<font color=\"info\">${amount}</font>元\n" +
                    ">实付：<font color=\"warning\">${total}</font>元\n" +
                    ">手续费：${other}元\n" +
                    ">用户：${userName}\n" +
                    ">手机号：${phone}\n" +
                    ">状态：成功\n" +
                    ">时间:${time}",
            title = "客户充值提醒",
            phones = {"18283893628"}
    )
    DingerResponse charge(@Parameter("farmName") String farmName, @Parameter("amount") Double amount, @Parameter("total") Double total, @Parameter("other") Double other, @Parameter("userName") String userName , @Parameter("phone") String phone, @Parameter("time") String time);


    //客户对公充值
    @DingerMarkdown(
            value = "#### <font color=\"info\">【客户对公充值】</font>  \n" +
                    ">猪场:${farmName}\n" +
                    ">操作类型：对公充值<font color=\"info\">${amount}</font>元\n" +
                    ">实付：<font color=\"warning\">${total}</font>元\n" +
                    ">手续费：${other}元\n" +
                    ">经办人：${userName}\n" +
                    ">手机号：${phone}\n" +
                    ">状态：成功\n" +
                    ">时间:${time}",
            title = "客户对公充值提醒",
            phones = {"18283893628"}
    )
    DingerResponse chargeCompany(@Parameter("farmName") String farmName, @Parameter("amount") Double amount, @Parameter("total") Double total, @Parameter("other") Double other, @Parameter("userName") String userName , @Parameter("phone") String phone, @Parameter("time") String time);



    //客户下单
    @DingerMarkdown(
            value = "#### <font color=\"info\">【客户下单】</font>   \n" +
                    ">猪场:${farmName}\n" +
                    ">操作类型：${orderType}\n" +
                    ">数量：${count}\n" +
                    ">扣款：<font color=\"warning\">${total}</font>元\n" +
                    ">单号：${code}\n" +
                    ">用户：${userName}\n" +
                    ">手机号：${phone}\n" +
                    ">状态：已下单\n" +
                    ">时间:${time}",
            title = "客户客户下单提醒",
            phones = {"18283893628"}
    )
    DingerResponse createOrder(@Parameter("farmName") String farmName, @Parameter("orderType") String orderType, @Parameter("count") Integer count, @Parameter("total") Double total, @Parameter("code") String code, @Parameter("userName") String userName , @Parameter("phone") String phone,@Parameter("time") String time);


    //自动续费
    @DingerMarkdown(
            value = "#### <font color=\"info\">【客户卡片自动续费任务】</font>  \n" +
                    ">猪场:${farmName}\n" +
                    ">操作类型：${orderType}\n" +
                    ">数量：${count}\n" +
                    ">扣款：<font color=\"warning\">${total}</font>元\n" +
                    ">单号：${code}\n" +
                    ">状态：已下单\n" +
                    ">时间:${time}",
            title = "自动续费任务",
            phones = {"18283893628"}
    )
    DingerResponse createOrderAuto(@Parameter("farmName") String farmName, @Parameter("orderType") String orderType, @Parameter("count") Integer count, @Parameter("total") Double total, @Parameter("code") String code, @Parameter("time") String time);



    //订单完成
    @DingerMarkdown(
            value = "#### <font color=\"info\">【订单完成】</font>  \n" +
                    ">猪场:${farmName}\n" +
                    ">操作类型：${orderType}\n" +
                    ">单号：${code}\n" +
                    ">状态：<font color=\"info\">已到账</font>\n" +
                    ">时间:${time}",
            title = "订单完成提醒",
            phones = {"18283893628"}
    )
    DingerResponse orderFinish(@Parameter("farmName") String farmName, @Parameter("orderType") String orderType, @Parameter("code") String code, @Parameter("time") String time);


    //流量不足
    @DingerMarkdown(
            value = "#### <font color=\"warning\">【客户流量不足】</font>  \n" +
                    ">猪场:${farmName}\n" +
                    ">操作类型：有<font color=\"warning\">${count}</font>张物联卡流量不足,请提醒客户充值!\n" +
                    ">时间:${time}",
            title = "客户流量不足提醒",
            phones = {"18283893628"}
    )
    DingerResponse dataWarning(@Parameter("farmName") String farmName, @Parameter("count") Long count, @Parameter("time") String time);


    //余额不足
    @DingerMarkdown(
            value = "#### <font color=\"warning\">【客户余额不足】</font>  \n" +
                    ">猪场:${farmName}\n" +
                    ">操作类型：月租续费大约需要<font color=\"info\">${need}</font>元,当前余额<font color=\"warning\">${remain}</font>元,请提醒客户充值!\n" +
                    ">时间:${time}",
            title = "客户余额不足提醒",
            phones = {"18283893628"}
    )
    DingerResponse balanceWarning(@Parameter("farmName") String farmName, @Parameter("need") Double need, @Parameter("remain") Double remain, @Parameter("time") String time);



}