package com.zmu.cloud.commons.constants;

/**
 * Description: 区分APP云慧养和智慧猪家(推送用)
 */
public final class JpushConstants {
    //app名字
    public static final  String SPH = "智慧猪家";
    public static final  String ZM_CLOUD = "云慧养";


    //推送模板-正文[生产预警]
    public static String YHY_PUSH_MESSAGE_BASE_TEMPLATE = "您的农场[%s]有%d条%s任务待处理";
    public static String SPH_PUSH_MESSAGE_BASE_TEMPLATE = "您的农场[%s]有%d条%s任务待处理";
    //推送模板-标题[生产预警]
    public static String YHY_PUSH_MESSAGE_BASE_TITLE = "%s任务提醒";
    public static String SPH_PUSH_MESSAGE_BASE_TITLE = "%s任务提醒";


    //推送模板-正文[流量不足预警]
    public static String YHY_PUSH_MESSAGE_DATA_WARING_TEMPLATE = "您的农场[%s]有%d张物联卡流量不足!";
    public static String SPH_PUSH_MESSAGE_DATA_WARING_TEMPLATE = "您的农场[%s]有%d张物联卡流量不足!";
    //推送模板-标题[流量不足预警]
    public static String YHY_PUSH_MESSAGE_DATA_WARING_TITLE = "%s提醒";
    public static String SPH_PUSH_MESSAGE_DATA_WARING_TITLE = "%s提醒";



    //推送模板-正文[余额不足预警]
    public static String YHY_PUSH_MESSAGE_BALANCE_WARING_TEMPLATE = "您的农场[%s]月租续费大约需要%.2f元,当前余额%.2f元,请及时充值避免不能自动续费导致停机给您带来不便!";
    public static String SPH_PUSH_MESSAGE_BALANCE_WARING_TEMPLATE = "您的农场[%s]月租续费大约需要%.2f元,当前余额%.2f元,请及时充值避免不能自动续费导致停机给您带来不便!";
    //推送模板-标题[余额不足预警]
    public static String YHY_PUSH_MESSAGE_BALANCE_WARING_TITLE = "%s提醒";
    public static String SPH_PUSH_MESSAGE_BALANCE_WARING_TITLE = "%s提醒";



}
