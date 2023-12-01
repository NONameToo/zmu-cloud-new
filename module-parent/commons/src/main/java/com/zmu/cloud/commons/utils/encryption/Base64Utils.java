package com.zmu.cloud.commons.utils.encryption;

import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;

public class Base64Utils {


    public static String encode(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return encode(s.getBytes(StandardCharsets.UTF_8));
    }

    public static String encode(byte[] b) {
        byte[] rb = org.apache.commons.codec.binary.Base64.encodeBase64(b);
        if (rb == null) {
            return null;
        }
        return new String(rb, StandardCharsets.UTF_8);
    }

    public static String decode(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        byte[] b = decodeToByte(s);
        if (b == null) {
            return null;
        }
        return new String(b, StandardCharsets.UTF_8);
    }

    public static byte[] decodeToByte(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            return org.apache.commons.codec.binary.Base64.decodeBase64(s.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
