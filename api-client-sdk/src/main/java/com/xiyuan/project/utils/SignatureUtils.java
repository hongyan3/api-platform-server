package com.xiyuan.project.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @author xiyuan
 * @className SignatureUtils
 * @description 签名工具
 * @date 2024/1/8 14:05
 */
public class SignatureUtils {
    public static String generateSignature(String body,String secretKey) {
        Digester sha256 = new Digester(DigestAlgorithm.SHA256);
        String content = body +'.'+secretKey;
        return sha256.digestHex(content);
    }
}
