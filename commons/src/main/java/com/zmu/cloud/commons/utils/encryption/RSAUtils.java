package com.zmu.cloud.commons.utils.encryption;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RSAUtils {

    private static final RSAUtils instance = new RSAUtils();

    private RSAUtils() {
    }

    public static RSAUtils getInstance() {
        return instance;
    }

    /**
     * 签名处理
     *
     * @param prikeyvalue ：私钥文件
     * @param sign_str    ：签名源内容
     * @return
     */
    public static String sign(String prikeyvalue, String sign_str) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64Utils.decodeToByte(prikeyvalue));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey myprikey = keyf.generatePrivate(priPKCS8);
            // 用私钥对信息生成数字签名
            java.security.Signature signet = java.security.Signature.getInstance("MD5withRSA");
            signet.initSign(myprikey);
            signet.update(sign_str.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signet.sign(); // 对信息的数字签名
            return Base64Utils.encode(signed);
        } catch (Exception e) {
            log.error("签名失败," + e.getMessage());
        }
        return null;
    }

    /**
     * 签名验证
     *
     * @param pubkeyvalue ：公钥
     * @param oid_str     ：源串
     * @param signed_str  ：签名结果串
     * @return
     */
    public static boolean checksign(String pubkeyvalue, String oid_str, String signed_str) {
        try {
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64Utils.decodeToByte(pubkeyvalue));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
            byte[] signed = Base64Utils.decodeToByte(signed_str);// 这是SignatureData输出的数字签名
            java.security.Signature signetcheck = java.security.Signature.getInstance("MD5withRSA");
            signetcheck.initVerify(pubKey);
            signetcheck.update(oid_str.getBytes(StandardCharsets.UTF_8));
            return signetcheck.verify(signed);
        } catch (Exception e) {
            log.error("签名验证异常," + e.getMessage());
            log.error("原文：" + oid_str);
            log.error("密文：" + signed_str);
        }
        return false;
    }

}
