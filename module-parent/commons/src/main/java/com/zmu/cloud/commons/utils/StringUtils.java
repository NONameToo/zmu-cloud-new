package com.zmu.cloud.commons.utils;

import cn.hutool.extra.pinyin.PinyinUtil;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 格式化金额格式
     */
    private static final DecimalFormat numFormat = new DecimalFormat("###,###.####");
    private static Logger logger = LoggerFactory.getLogger("sys-error");

    /**
     * 空字符串
     */
    private static final String NULLSTR = "";

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';

    /**
     * 获取参数不为空值
     *
     * @param value defaultValue 要判断的value
     * @return value 返回值
     */
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个对象数组是否非空
     *
     * @param objects 要判断的对象数组
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * * 判断一个对象是否是数组类型（Java基本型别的数组）
     *
     * @param object 对象
     * @return true：是数组 false：不是数组
     */
    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }


    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return NULLSTR;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }

        return str.substring(start);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return NULLSTR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }


    /**
     * 获取字符串中每个字符的拼音首字部大写，并返回
     *
     * @param str 字符串
     * @return 拼接大写字符串
     */
    public static String getStrFirstPinyin(String str) {
        String pinyin = "";
        char[] strs = str.toCharArray();
        for (char c : strs) {
            pinyin +=  PinyinUtil.getFirstLetter(c);
        }
        return pinyin.toUpperCase();
    }


    /**
     * 下划线转驼峰命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰式命名法 例如：user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获取英文首字母
     * @param name
     * @return
     */
    public static String getFirstLetter(String name){
        try {
            char[] arr = name.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            if (arr.length>0){
                return String.valueOf(PinyinHelper.toHanyuPinyinStringArray(arr[0],defaultFormat)[0].toCharArray()[0]).toUpperCase();
            }

        } catch (Exception e) {
            logger.error("StringUtils--getFirstLetter 获取英文首字母异常", e);
        }
        return null;
    }

    /**
     * 首字母转大写
     * @param name
     * @return
     */
    public static String firstToUpperCase(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return  name;

    }

    /**
     * 首字母转小写
     * @param name
     * @return
     */
    public static String firstToLowerCase(String name) {
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        return  name;

    }


    /**
     * 特殊符号处理
     * @param symbol
     * @return
     */
    public static String spicalSymbolHandle(String symbol){
        if(symbol==null){
            return null;
        }
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(symbol);
        // symbol = m.replaceAll("");
        symbol = symbol.trim();
        symbol = symbol.toUpperCase();
        symbol = symbol.replaceAll("　", "");
        symbol = symbol.replaceAll("“", "\"");
        symbol = symbol.replaceAll("”", "\"");
        symbol = symbol.replaceAll("：", ":");
        symbol = symbol.replaceAll("。", ".");
        symbol = symbol.replaceAll("（", "(");
        symbol = symbol.replaceAll("）", ")");
        symbol = symbol.replaceAll("&ldquo;", "\"");
        symbol = symbol.replaceAll("&rdquo;", "\"");
        symbol = symbol.replaceAll("&quot;", "\"");
        symbol = symbol.replaceAll("，", ",");
        symbol = symbol.replaceAll("；", ";");
        symbol = symbol.replaceAll("！", "!");
        symbol = symbol.replaceAll("？", "?");
        symbol = symbol.replaceAll("，", ",");
        symbol = symbol.replaceAll("【", "[");
        symbol = symbol.replaceAll("】", "]");
        symbol = symbol.replaceAll("\\{", "{");
        symbol = symbol.replaceAll("\\}", "}");
        symbol = symbol.replaceAll("、", ",");
        symbol = symbol.replaceAll("\\*", "*");
        return symbol;
    }

    /**
     * 替换特殊符号为空格
     * @param var
     * @return
     */
    public static String symbolHandler(String var){
        String regEx="[\n`~!@#$%^&*()+=|{}':：;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；”“’。， 、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(var);
       return m.replaceAll("").trim();
    }
    /**
     * 替换特殊符号为空格
     * @param str
     * @return
     */
    public static String filterSpecialChars(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }
    /**
     * 校验是否为数字
     * @param numStr
     * @return true：数字   false：非数字
     */
    public static boolean checkBigDecimal(String numStr){
        boolean result = true;
        try{
            new BigDecimal(numStr);
        }catch (Exception e) {
            result = false;
        }
        return result;
    }


    /**
     * 批量插入的时候，获取集合数量
     */
    public static int getSubListIdx(int i, int size){
        int stepSize = 100;
        return Math.min((i + stepSize), size);
    }


    public static String getPinYinStr(String chinese) {
        StringBuffer pybf = new StringBuffer();
        try {
            char[] arr = chinese.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            for (int i = 0; i < arr.length; i++) {
                String str = String.valueOf(arr[i]);
                if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                  pybf.append( PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                    pybf.append(arr[i]);
                } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母
                    pybf.append(arr[i]);
                } else {// 否则不转换
                    pybf.append(arr[i]);// 如果是标点符号的话，带着
                }
            }

        } catch (Exception e) {
            logger.error("StringUtils--getPinYin 汉字转拼音首字母大写异常", e);
        }
        return pybf.toString();
    }
}
