package com.zmu.cloud.commons.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zmu.cloud.commons.exception.BaseException;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ZmPayUtil {

    /**
     * @Description 元转分 用于前端转换为后端持久化
     **/
    public static int yuanToFen(Double amount) {
        if(amount == null || amount<0D){
            throw  new BaseException("请输入有效金额!");
        }
        //BigDecimal money=new BigDecimal(100.99);
        //String money="100.99";
        //money可以是字符串，可以是double，可以是BigDecimal 类型
        //基本上传过来的支付金额，后面会保留两位小数到分
        //BigDecimal自带的方法，把金额转换成字符串---》字符串小数点向右移动两位--->转换成int
        return new BigDecimal(String.valueOf(amount)).movePointRight(2).intValue();
    }

    /**
     * 分转元，转换为bigDecimal在转成double
     * @return
     */
    public static double fenToYuan(int price) {
        return new BigDecimal(String.valueOf(price)).divide(new BigDecimal(100)).doubleValue();
    }


    /**
     * 计算手续费,单位为分
     * @return
     */
    public static int otherCalculate(int price,Integer percent) {
        return getInt(new BigDecimal(String.valueOf(price)).multiply(new BigDecimal(String.valueOf(percent))).divide(new BigDecimal(1000),2, RoundingMode.HALF_UP).doubleValue());
    }

    /**
     * （1）四舍五入把double转化int整型，0.5进一，小于0.5不进一
     */
    public static int getInt(double number){
        BigDecimal bd=new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
        return Integer.parseInt(bd.toString());
    }
}
