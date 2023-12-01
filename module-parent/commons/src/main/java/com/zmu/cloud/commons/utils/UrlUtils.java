package com.zmu.cloud.commons.utils;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class UrlUtils {

    /**
     * url转码、解码
     *
     * @author lifq
     * @date 2015-3-17 下午04:09:35
     */
    private final static String ENCODE = "UTF-8";

    /**
     * URL 解码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:09:51
     */
    public static String getURLDecoderString(String str) {
        String result = null;
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * URL 转码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:10:28
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, String> getParameters(String parameters) {
        Map<String, String> map = new HashMap<String, String>();
        String[] arr = parameters.split("&");
        for (int i = 0; i < arr.length; i++) {
            String key = arr[i].substring(0, arr[i].indexOf("="));
            String value = arr[i].substring(arr[i].indexOf("=") + 1);
            map.put(key, value);
        }
        return map;
    }

}