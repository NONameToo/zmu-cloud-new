package com.zmu.cloud.commons.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

@Slf4j
public class GoogleGenerator {

    // 生成的key长度( Generate secret key length)
    private static final int SECRET_SIZE = 15;

    private static final String SEED = "g8GjEvTbW5oVSV7avL47357438reyhreyuryetredLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx";

    // Java实现随机数算法
    private static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

    // 最多可偏移的时间
    private   static int window_size = 1; // default 3 - max 17


    public static String generateSecretKey() {
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decodeBase64(SEED));
            Base32 codec = new Base32();
            return new String(codec.encode(sr.generateSeed(SECRET_SIZE)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getQRBarcode(String user, String secret) {
        return String.format("otpauth://totp/%s?secret=%s", user, secret);
    }

    public static boolean check_code(String secret, String code) {
        long t = (System.currentTimeMillis() / 1000L) / 30L;
        for (int i = -window_size; i <= window_size; ++i) {
            long hash;
            try {
                hash = verify_code(new Base32().decode(secret), t + i);
            } catch (Exception e) {
                log.error("校验Google验证码失败：", e);
                return false;
            }
            if (code.equals(addZero(hash))) {
                return true;
            }
        }
        return false;
    }

    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key, "HmacSHA1"));
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

    private static String addZero(long code) {
        return String.format("%06d", code);
    }
}
