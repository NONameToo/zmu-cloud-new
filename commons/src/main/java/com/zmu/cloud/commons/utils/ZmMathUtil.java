package com.zmu.cloud.commons.utils;

import cn.hutool.core.util.ObjectUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ZmMathUtil {

    public static void main(String[] args) {
        System.out.println(getPercent(12L,100L));
    }


    public static int calculateAccuracy(double measuredValue, double standardValue) {
        // 计算准确度百分比
        int accuracy = (int)((100D - ((Math.abs(measuredValue - standardValue) / standardValue) * 100D))*100D);
        return accuracy;
    }




    /**
     *
     * @Description // 计算百分比
     * @Date  2023/2/17 1:21
     * @Param [count, total]
     * @Return java.lang.Integer
     **/
    public static Integer getPercent(Long count, Long total) {
        if(total == null || total==0){
            return 0;
        }
        BigDecimal currentCount = new BigDecimal(count);
        BigDecimal totalCount = new BigDecimal(total);
        BigDecimal divide = currentCount.divide(totalCount, 2, RoundingMode.HALF_UP);
        int i = divide.multiply(new BigDecimal(100)).intValue();
        if(i>100){
            return 100;
        }
        return i;
    }

    /**
     *
     * @Description // g转T用于前端展示统计图等报表信息
     * @Date  2023/2/17 1:26
     * @Param [g]
     * @Return java.lang.String
     **/
    public static String gToTString(Long g) {
        if(g == null || g==0){
            return "0.00";
        }
        return new BigDecimal(g.toString()).divide(new BigDecimal(1000000), 2, RoundingMode.DOWN).toString();
    }


    /**
     *
     * @Description // kg转T用于饲喂器首页kg转换为吨
     * @Date  2023/2/17 1:26
     * @Param [g]
     * @Return java.lang.String
     **/
    public static BigDecimal kgToTString(BigDecimal g) {
        if(g == null || g.compareTo(BigDecimal.ZERO)==0){
            return BigDecimal.ZERO;
        }
        return g.divide(new BigDecimal(1000), 3, RoundingMode.DOWN);
    }



    /**
     *
     * @Description // kg转g用于饲料密度kg/m³转g/m³
     * @Date  2023/2/17 1:26/m
     * @Param [g]
     * @Return java.lang.String
     **/
    public static Long kgTog(Double kg) {
        if(kg == null || kg==0){
            return 0L;
        }
        return BigDecimal.valueOf(kg).multiply(new BigDecimal(1000)).longValue();
    }


    /**
     *
     * @Description // 用于饲料密度g/m³转kg/m³给前端展示
     * @Date  2023/2/17 1:26/m
     * @Param [g]
     * @Return java.lang.String
     **/
    public static String gTokgDensityString(Long g) {
        if (ObjectUtil.isNull(g)) {
            return "0.00";
        }
       return new BigDecimal(g.toString()).divide(new BigDecimal(1000), 2, RoundingMode.DOWN).toString();
    }




    /**
     *
     * @Description // g转T用于前端展示TowerCheckVO
     * @Date  2023/2/17 1:26
     * @Param [g]
     * @Return java.lang.String
     **/
    public static String gToTStringWithT(Long g) {
        if(g == null || g==0){
            return "0.00";
        }
        return new BigDecimal(g.toString()).divide(new BigDecimal(1000000), 3, RoundingMode.DOWN).toString().concat("T");
    }

    public static BigDecimal gToT(Long g) {
        if(g == null || g==0){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(g.toString()).divide(new BigDecimal(1000000), 3, RoundingMode.DOWN);
    }




    /**
     *
     * @Description // T转g用于前端表单转数据库持久化
     * @Return java.lang.String
     **/
    public static Long tTog(Double t) {
        if(t == null){
            return 0L;
        }
        return BigDecimal.valueOf(t).multiply(new BigDecimal(1000000)).longValue();
    }

    /**
     *
     * @Description // cm³转m³
     **/
    public static String cmTmString(Long cm) {
        if(cm == null || cm==0){
            return "0.00";
        }
        return new BigDecimal(cm.toString()).divide(new BigDecimal(1000000), 2, RoundingMode.DOWN).toString();
    }

    /**
     *立方米 m³转立方厘米 cm³
     **/
    public static Long m3ToCm3(Double m) {
        m = ObjectUtil.isNull(m)?0:m;
        return new BigDecimal(m.toString()).multiply(new BigDecimal("1000000")).longValue();
    }

    /**
     * 计算体积 立方米
     * @param capacity 单位 g
     * @param density 单位 g/m³
     * @return 立方米（m³),保留三位小数
     */
    public static Double calVolumeForCm3(Long capacity, Long density) {
        capacity = ObjectUtil.isNull(capacity)?0:capacity;
        density = ObjectUtil.isNull(density)?0:density;
        return new BigDecimal(capacity.toString()).divide(new BigDecimal(density.toString()), 3, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算体积  立方厘米
     * @param capacity 单位 g
     * @param density 单位 g/m³
     * @return 立方厘米（cm³)
     */
    public static Long calVolumeForM3(Long capacity, Long density) {
        return m3ToCm3(calVolumeForCm3(capacity, density));
    }


    /**
     *
     * @Description
     **/
    public  static Double  cmTm(Long m) {
        if(m == null || m==0){
            return 0D;
        }
        return new BigDecimal(m.toString()).divide(new BigDecimal(1000000), 2, RoundingMode.DOWN).doubleValue();
    }

    /**
     *
     * @Description //密度  g/m³转g/cm³(分母变大,是除法)
     **/
    public  static Long  mTcmDestiny(Long m) {
        if(m == null || m==0){
            return 0L;
        }
        return new BigDecimal(m.toString()).divide(new BigDecimal(1000000), 3, RoundingMode.DOWN).longValue();
    }

}
