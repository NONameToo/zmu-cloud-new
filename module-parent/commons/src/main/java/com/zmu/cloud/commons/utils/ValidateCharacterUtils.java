package com.zmu.cloud.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class ValidateCharacterUtils {

    public static final String REGEX = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    public static boolean validate(String str) {
        if (StringUtils.isBlank(str))
            return false;
        Matcher m = Pattern.compile(REGEX).matcher(str);
        return m.find();
    }

    public static boolean isChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find())
            flg = true;
        return flg;
    }
}

