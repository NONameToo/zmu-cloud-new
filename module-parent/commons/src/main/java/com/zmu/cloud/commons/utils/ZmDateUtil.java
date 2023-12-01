package com.zmu.cloud.commons.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ZmDateUtil {

    public static Date getTodayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }


    /**
     * 获取当月开始时间
     */
    public static Date getCurrentMonthStartTime() {

        Long currentTime = System.currentTimeMillis();

        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(currentTime);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }


    /**
     * 获取当月的结束时间戳
     */
    public static Date getCurrentMonthEndTime() {
        Long currentTime = System.currentTimeMillis();
        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(currentTime);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 获取当前月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }


    /**
     * 获取指定年月的开始时间
     */
    public static Date getYearMonthStartTime(Integer year,Integer month) {

        Long currentTime = System.currentTimeMillis();

        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }




    /**
     *  获取指定年月的结束时间戳
     */
    public static Date getYearMonthEndTime(Integer year,Integer month) {
        Long currentTime = System.currentTimeMillis();
        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 获取当前月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }




    /**
     * 获取指定年月的开始时间
     */
    public static Date getYearMonthStartTime(Date date) {
        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }


    /**
     * 获取指定年月结束时间
     */
    public static Date getYearMonthEndTime(Date date) {
        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 获取当前月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();

    }







    /**
     * 获取日期开始时间
     */
    public static Date getDateStartTime(Date date) {
        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }



    public static Date getDateEndTime(Date date) {
        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }



    /**
     *  将年月日格式转换成date
     */
    public static Date getDateStartTime(String  date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date d = null;
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(d);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }



    /**
     *  将年月日格式转换成date
     */
    public static Date getDateEndTime(String  date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date d = null;
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(d);
        todayStart.set(Calendar.HOUR_OF_DAY, 23);
        todayStart.set(Calendar.MINUTE, 59);
        todayStart.set(Calendar.SECOND, 59);
        todayStart.set(Calendar.MILLISECOND, 999);
        return todayStart.getTime();
    }








    /**
     * 根据日期获得所在周的日期
     * @param weekWantonlyDay 周内任意日期
     * @return
     */
    public static List<Date> weekDays(Date weekWantonlyDay) {
        List<Date> dates = new ArrayList<>();
        DateTime begin = DateUtil.beginOfWeek(weekWantonlyDay);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);
        dates.add(begin);
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            dates.add(calendar.getTime());
        }
        return dates;
    }

    /**
     * 近一周的日期
     * day is null, 返回当天的近一周日期
     * @param day
     * @return
     */
    public static List<Date> nearWeekDays(Date day) {
        if (ObjectUtil.isNull(day)) {
            day = new Date();
        }
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        dates.add(calendar.getTime());
        for (int i = 5; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            dates.add(calendar.getTime());
        }
        return ListUtil.reverse(dates);
    }




    /**
     * 近一个月的日期
     * day is null, 返回当天的近一个月日期
     * @param day
     * @return
     */
    public static List<Date> nearMonthDays(Date day) {
        if (ObjectUtil.isNull(day)) {
            day = new Date();
        }
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        dates.add(calendar.getTime());
        for (int i = 29; i > 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dates.add(calendar.getTime());
        }
        return ListUtil.reverse(dates);
    }


    /**
     * 时间线
     */
    public static String getTimeLine(Date startTime,Date endTime) {
        return  DateUtil.format(endTime, "M/d") +
                "-" +
                DateUtil.format(startTime, "M/d");
    }



    public static void main(String[] args) {
        System.out.println(nearWeekDays(new Date()));
    }


    /**
     *  将date格式转换成年月日
     */
    public static String getStringFromDate(Date  date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        return df.format(date);
    }


    /**
     *  将date格式转换成近一周只显示   月/日
     */
    public static String forMateDateToMonthDay(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("M/d");//设置日期格式
//        SimpleDateFormat df = new SimpleDateFormat("d");//设置日期格式
        return df.format(date);
    }


    /**
     *  将date格式转换成近一周只显示   月/日
     */
    public static String forMateDateToYearMonthDay(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        return df.format(date);
    }


    /**
     *  将date格式转换成近一周只显示   月/日
     */
    public static Date forMateStringToYearMonthDayDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date d = null;
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(d);
        return todayStart.getTime();
    }



    /**
     *  将date格式转换成只显示   上午 分:秒
     */
    public static String forMateDateToHM(Date date) {
//        SimpleDateFormat df = new SimpleDateFormat("aa HH:mm",Locale.CHINA);//设置日期格式
//        return df.format(date);
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        StringBuffer stringBuilder = new StringBuffer();
        int a = Integer.parseInt(str);
        if (a >= 0 && a <= 6) {
            stringBuilder.append("凌晨");
        }
        if (a > 6 && a <= 12) {
            stringBuilder.append("上午");
        }
        if (a > 12 && a <= 13) {
            stringBuilder.append("中午");
        }
        if (a > 13 && a <= 18) {
            stringBuilder.append("下午");
        }
        if (a > 18 && a <= 24) {
            stringBuilder.append("晚上");

        }

        SimpleDateFormat df1 = new SimpleDateFormat(" HH:mm", Locale.CHINA);//设置日期格式
        stringBuilder.append(df1.format(date));
        return stringBuilder.toString();

    }


        /**
         *  获取月份01,02,03格式
         */
    public static String forMateMonth(Integer month) {
        String date = "";
        if (month< 10) {
            date = date.concat("0");
        }
        date = date.concat(String.valueOf(month));
        return date;
    }



    /**
     *  将年月日时分秒格式转换成想要的年
     */
    public static Integer getYearFromString(String  date) {
        return Integer.parseInt(date.substring(0,4));
    }
    /**
     *  将年月日时分秒格式转换成想要的月
     */
    public static Integer getMonthFromString(String  date) {
        return Integer.parseInt(date.substring(5,7));
    }

    /**
     *  将年月日时分秒格式转换成想要的日
     */
    public static Integer getDayFromString(String  date) {
        return Integer.parseInt(date.substring(8,10));
    }

    /**
     * 将LocalDateTime转换成String
     */

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        if (ObjectUtil.isEmpty(localDateTime)) {
            return "";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(fmt);
    }


    /**
     * 将LocalDateTime转换成Date
     */

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from( localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    //传入指定的日期beginDay和时间间隔days，往前为负数，往后为正数
    public static Date calculationDate(Date beginDay,long days){
        //获取指定日期的时间戳
        long beginTime= beginDay.getTime();
        //计算时间间隔的时间戳
        long intervalTime = days*24*60*60*1000;
        //用指定日期时间戳加上时间间隔得到所求的日期
        long lastTime = beginTime + intervalTime;
        //将所求日期的时间戳转为日期并返回
        return new Date(lastTime);
    }

}
