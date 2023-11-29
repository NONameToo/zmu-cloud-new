package com.zmu.cloud.commons.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * Created with IntelliJ IDEA 17.3.4
 *
 * @DESCRIPTION: ReplaceEnvVariateUtil
 * @Date 2018-06-12 10:22
 */

@Slf4j
public class ReplaceEnvVariableUtil {

    private static final String PREFIX = "${";
    private static final String SUFFIX = "}";


    /**
     * @Description file 文件转 string
     * @Date 2018/6/12 0012 10:49
     */
    public static String toString(InputStream inputStream, Environment environment) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
            String s;
            while ((s = bf.readLine()) != null) {//使用readLine方法，一次读一行
                sb.append(s.trim());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return replaceEnvVariable(sb.toString(), environment);
    }

    /**
     * @Description 替换string中的 ${} 为真实环境中的值
     * @Date 2018/6/12 0012 10:50
     */
    public static String replaceEnvVariable(String string, Environment environment) {
        String newString = string;
        if (newString != null && environment != null) {
            int start = newString.indexOf(PREFIX);
            int end = newString.indexOf(SUFFIX);
            while (start > -1 && end > start) {
                String prepend = newString.substring(0, start);
                String append = newString.substring(end + SUFFIX.length());
                String propName = newString.substring(start + PREFIX.length(), end);
                String propValue = environment.getProperty(propName);
                if (propValue == null) {
                    newString = prepend + propName + append;
                } else {
                    newString = prepend + propValue + append;
                }
                start = newString.indexOf(PREFIX);
                end = newString.indexOf(SUFFIX);
            }
        }
        return newString;
    }

}
