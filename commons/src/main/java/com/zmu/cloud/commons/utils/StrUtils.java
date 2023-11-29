package com.zmu.cloud.commons.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zmu.cloud.commons.enums.FeederType;
import com.zmu.cloud.commons.exception.BaseException;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: StrUtils
 * @Date 2019-04-12 20:52
 */

public class StrUtils {

    public static final Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    public static String removeBlank(String str) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        return str.replaceAll("\\s*", "");
    }

    public static String addr(String addr) {
        if (StringUtils.isBlank(addr)) {
            return "-";
        }
        try {
            return addr.subSequence(0, 4) + ".." + addr.substring(addr.length() - 4);
        } catch (Exception e) {
            return "-";
        }
    }

    public static String cutStr(String str, int length) {
        if (StringUtils.isBlank(str)) {
            return "-";
        }
        return str.length() > length ? (str.subSequence(0, length) + "...") : str;
    }

    public static boolean isContainsChinese(String str) {
        Matcher m = CHINESE_PATTERN.matcher(str);
        return m.find();
    }

    public static String decimal(BigDecimal value) {
        return value == null ? "null" : value.stripTrailingZeros().toPlainString();
    }

    public static Long parseLong(String rs) {
        return ObjectUtil.isEmpty(rs)?null:Long.parseLong(rs);
    }

    public static Integer parseInt(String rs) {
        return ObjectUtil.isEmpty(rs)?null:Integer.parseInt(rs);
    }

    public static LocalDate parseLocalDate(String rs) {
        return ObjectUtil.isEmpty(rs)?null: LocalDateTimeUtil.parseDate(rs);
    }

    public static LocalDateTime parseLocalDateTime(String rs) {
        return ObjectUtil.isEmpty(rs)?null: LocalDateTimeUtil.parse(rs);
    }

}
