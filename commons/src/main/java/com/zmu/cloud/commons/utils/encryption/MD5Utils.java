package com.zmu.cloud.commons.utils.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class MD5Utils {

    private static MD5Utils instance;

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private MD5Utils() {
    }

    public static MD5Utils getInstance() {
        if (null == instance)
            return new MD5Utils();
        return instance;
    }

    /**
     * 转换字节数组�?6进制字串
     */
    private String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换字节数组为高位字符串
     */
    private String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5 摘要计算(byte[]).
     */
    public String md5Digest(byte[] src) {
        MessageDigest alg;
        try {
            alg = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return byteArrayToHexString(alg.digest(src));
    }

    public String encryptionMD5(String plainText) {
        if (null == plainText || "".equals(plainText)) {
            return null;
        }
        try {
            return md5Digest(plainText.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("MD5加密签名时失败", e);
        }
    }

    /**
     * 原文加密(参数环绕加密)
     */
    public String encryptionMD5Around(Map<String, String> paramMap, String md5Key) {
        if (null == paramMap || paramMap.isEmpty()) {
            return null;
        }
        try {
            StringBuilder signbuffer = new StringBuilder();
            for (String key : paramMap.keySet()) {
                signbuffer.append(paramMap.get(key));
            }
            return this.encryptionMD5Around(signbuffer.toString(), md5Key);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密签名时失败", e);
        }
    }

    /**
     * 原文加密(参数环绕加密)
     */
    public String encryptionMD5Around(String plainText, String md5Key) {
        if (null == plainText || "".equals(plainText)) {
            return null;
        }
        try {
            return md5Digest((md5Key + plainText + md5Key).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("MD5加密签名时失败", e);
        }
    }

    /**
     * 原文加密(在参数之后加密)
     */
    public String encryptionMD5After(String plainText, String md5Key) {
        if (null == plainText || "".equals(plainText)) {
            return null;
        }
        StringBuilder signbuffer = new StringBuilder(plainText);
        signbuffer.append("&key=").append(md5Key);
        try {
            return md5Digest(signbuffer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("MD5加密签名时失败", e);
        }
    }

}
