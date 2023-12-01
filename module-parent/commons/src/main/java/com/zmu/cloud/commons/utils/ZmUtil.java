package com.zmu.cloud.commons.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zmu.cloud.commons.exception.BaseException;
import org.springframework.util.StringUtils;

public class ZmUtil {


    /**
     * 检查原始密码
     * @param password
     * @return
     */
    public static Boolean checkOriginalPassword(String password) {
        return "123456".equals(password)?true:false;
    }

    /**
     * 二维码解析
     * @param content
     * @return
     */
    public static JSONObject rqCodeAnalysis(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BaseException("content不能为空");
        }
        String type = "";
        JSONObject object = null;
        try {
            object = JSONUtil.parseObj(content);
            type = object.get("type").toString();
        } catch (Exception e) {
            throw new BaseException("二维码错误");
        }
        if (StringUtils.isEmpty(type)) {
            throw new BaseException("二维码错误");
        }
        return object;
    }

    public static Long parseLong(String rs) {
        return ObjectUtil.isEmpty(rs)?null:Long.parseLong(rs);
    }

    public static Integer parseInt(String rs) {
        return ObjectUtil.isEmpty(rs)?null:Integer.parseInt(rs);
    }

    /**
     * 圆台体积计算公式
     * V = ⅓πh(r²+R²+rR)
     * @return
     */
    public static double roundPlatformVolumeFormula(double R, double r, double h) {
        return Math.PI*h*(R*R + r*r +R*r)/3;
    }




}
