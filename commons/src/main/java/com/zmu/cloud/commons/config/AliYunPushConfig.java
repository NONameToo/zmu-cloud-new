package com.zmu.cloud.commons.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.push.model.v20160801.PushRequest;
import com.aliyuncs.push.model.v20160801.PushResponse;
import com.aliyuncs.utils.ParameterHelper;
import com.zmu.cloud.commons.enums.AliyunPushEnum;
import com.zmu.cloud.commons.utils.StrUtils;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: AliYunPushConfig
 * @Date 2019-12-11 15:57
 */
@Component
@ConfigurationProperties(prefix = "commons.config.aliyun.push", ignoreInvalidFields = true)
@Slf4j
@Data
public class AliYunPushConfig {

    private String regionId = "cn-hangzhou";
    private String accessKey;
    private String accessSecret;
    private String androidActivity = "";
    private Long androidKey;
    private Long iosKey;
    private String iosEnv = "DEV";


    public void push(String title, String content, String extParameters, List<Long> addresses, AliyunPushEnum aliyunPushEnum) {
        log.info("阿里云推送：size={}，title={}，body={}，extParameters={}", addresses.size(), title, content, extParameters);
        PushRequest pushRequest = new PushRequest();
        pushRequest.setTarget("ACCOUNT"); //推送目标: DEVICE:推送给设备; ACCOUNT:推送给指定帐号,TAG:推送给自定义标签; ALIAS: 按别名推送; ALL: 全推
        pushRequest.setTargetValue(join(addresses)); //根据Target来设定，如Target=DEVICE, 则对应的值为 设备id1,设备id2. 多个值使用逗号分隔.(帐号与设备有一次最多1000个的限制)
        pushRequest.setStoreOffline(true); // 离线消息是否保存,若保存, 在推送时候，用户即使不在线，下一次上线则会收到
        pushRequest.setExpireTime(ParameterHelper.getISO8601Time(new Date(System.currentTimeMillis() + 72 * 3600 * 1000)));
        pushRequest.setPushType(aliyunPushEnum.name()); // MESSAGE:表示消息(默认), NOTICE:表示通知
        pushRequest.setTitle(StrUtils.cutStr(title, 20)); // 消息的标题
        pushRequest.setBody(StrUtils.cutStr(content, 50)); // 消息的内容
        pushRequest.setAndroidExtParameters(extParameters);
        pushRequest.setIOSExtParameters(extParameters);
        pushAndroid(pushRequest);
        pushIOS(pushRequest);

    }

    private void pushAndroid(PushRequest pushRequest) {
// 推送目标
        pushRequest.setAppKey(androidKey);
        pushRequest.setDeviceType("ANDROID"); // 设备类型deviceType, iOS设备: "iOS"; Android设备: "ANDROID"; 全部: "ALL", 这是默认值.
// 推送配置: Android
        pushRequest.setAndroidOpenType("APPLICATION"); // 点击通知后动作 'APPLICATION': 打开应用 'ACTIVITY': 打开应用AndroidActivity 'URL': 打开URL 'NONE': 无跳转
        pushRequest.setAndroidNotifyType("BOTH"); // 通知的提醒方式 ‘VIBRATE': 振动  'SOUND': 声音 'DEFAULT': 声音和振动 'NONE': 不做处理，用户自定义
        pushRequest.setAndroidMusic("default"); // Android通知声音
        pushRequest.setAndroidNotificationBarType(1);//通知栏自定义样式0-100
        pushRequest.setAndroidPopupTitle(pushRequest.getTitle());
        pushRequest.setAndroidPopupBody(pushRequest.getBody());
        pushRequest.setStoreOffline(true); // 离线消息是否保存,若保存, 在推送时候，用户即使不在线，下一次上线则会收到
        pushRequest.setAndroidNotificationChannel("1");
        // 内容
        // 设置辅助弹窗打开Activity
        pushRequest.setAndroidPopupActivity(androidActivity);

        // 12小时后消息失效, 不会再发送

        PushResponse pushResponse = null;
        try {
            pushResponse = new DefaultAcsClient(DefaultProfile.getProfile(regionId, accessKey, accessSecret)).getAcsResponse(pushRequest);
            log.info("阿里云推送：Android --> RequestId: {}, MessageID: {}", pushResponse.getRequestId(), pushResponse.getMessageId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void pushIOS(PushRequest pushRequest) {
// 推送配置: iOS
        pushRequest.setAppKey(iosKey);
        pushRequest.setDeviceType("iOS");
        pushRequest.setIOSBadgeAutoIncrement(true);
        pushRequest.setIOSMusic("default"); // iOS通知声音
        pushRequest.setIOSApnsEnv(iosEnv);//iOS的通知是通过APNs中心来发送的，需要填写对应的环境信息。'DEV': 表示开发环境 'PRODUCT': 表示生产环境
        pushRequest.setIOSRemind(true); //  消息推送时设备不在线（既与移动推送的服务端的长连接通道不通），则这条推送会做为通知，通过苹果的APNs通道送达一次。注意：**离线消息转通知仅适用于`生产环境`**
        pushRequest.setIOSRemindBody(pushRequest.getBody()); // iOS消息转通知时使用的iOS通知内容，仅当iOSApnsEnv=`PRODUCT` && iOSRemind为true时有效
        PushResponse pushResponse = null;
        try {
            pushResponse = new DefaultAcsClient(DefaultProfile.getProfile(regionId, accessKey, accessSecret)).getAcsResponse(pushRequest);
            log.info("阿里云推送：IOS --> RequestId: {}, MessageID: {}", pushResponse.getRequestId(), pushResponse.getMessageId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String join(List<Long> addresses) {
        StringJoiner sj = new StringJoiner(",");
        addresses.forEach(id->sj.add(id.toString()));
        return sj.toString();
    }
}
