package com.zmu.cloud.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: ReadRequestBodyUtils
 * @Date 2020-04-21 16:33
 */

public class ReadRequestBodyUtils {

    public static String readBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("");
        try (BufferedReader br = request.getReader()) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
