package com.zmu.cloud.commons.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA 17.0.1
 *
 * @DESCRIPTION: CoverUtil
 * @Date 2018-10-29 10:59
 */

public class CoverUtil {


    /**
     * @Description 手机号加* 处理
     * @Date 2018/10/29 11:20
     */
    public static String coverPhone(String phone) {
        if (StringUtils.isBlank(phone))
            return "";
        if (phone.length() < 3) {
            return "**";
        }
        if (phone.length() == 11) {
            return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        } else {
            StringBuilder sb = new StringBuilder(phone.subSequence(0, 1));
            for (int i = 0; i < (phone.length() - 2); i++) {
                sb.append("*");
            }
            sb.append(phone.substring(phone.length() - 1));
            return sb.toString();
        }
    }

    /**
     * @Description 微信号加*处理
     * @Date 2018/10/29 11:30
     */
    public static String coverWechat(String wechat) {
        if (StringUtils.isBlank(wechat) || wechat.length() < 2)
            return "";
        StringBuilder sb = new StringBuilder(wechat.subSequence(0, 1));
        for (int i = 0; i < (wechat.length() - 2); i++) {
            sb.append("*");
        }
        sb.append(wechat.substring(wechat.length() - 1));
        return sb.toString();
    }

    /**
     * @Description email加* 处理
     * @Date 2018/10/29 11:30
     */
    public static String coverEmail(String email) {
        if (StringUtils.isBlank(email) || !email.contains("@")) {
            return email;
        }
        String[] split = email.split("@");
        String s = split[0];
        if (s.length() < 2) {
            return email.subSequence(0, 1) + "****" + email.substring(email.indexOf("@"));
        }
        return email.subSequence(0, 2) + "****" + email.substring(email.indexOf("@"));
    }

    public static String coverIdCard(String idCardNum, int... num) {
        //身份证不能为空
        if (StringUtils.isBlank(idCardNum)) {
            return "";
        }
        int front = 4, end = 4;
        if (num.length == 2) {
            front = num[0];
            end = num[1];
        }
        //需要截取的长度不能大于身份证号长度
        if ((front + end) > idCardNum.length()) {
            return "";
        }
        //需要截取的不能小于0
        if (front < 0 || end < 0) {
            return "";
        }
        //计算*的数量
        int asteriskCount = idCardNum.length() - (front + end);
        StringBuilder asteriskStr = new StringBuilder();
        for (int i = 0; i < asteriskCount; i++) {
            asteriskStr.append("*");
        }
        String regex = "(\\w{" + front + "})(\\w+)(\\w{" + end + "})";
        return idCardNum.replaceAll(regex, "$1" + asteriskStr + "$3");
    }

}
