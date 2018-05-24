package com.linkcircle.fj.agorasignal.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SignalTokenUtils
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 09:32
 */
public class SignalTokenUtils {
    /**
     * gen Token
     *
     * @param pAppID          信令的app_id
     * @param pAppCertificate 信令的app_certificate
     * @param pAccount        账号
     * @param pExpiredTime    超时时间
     * @return 信令Token
     */
    public static String calcToken(String pAppID, String pAppCertificate, String pAccount, long pExpiredTime) {
        String sign = md5hex((pAccount + pAppID + pAppCertificate + pExpiredTime).getBytes());
        return "1:" + pAppID + ":" + pExpiredTime + ":" + sign;
    }

    private static String md5hex(byte[] s) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(s);
            return hexlify(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String hexlify(byte[] data) {
        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }

        return String.valueOf(out);
    }
}
