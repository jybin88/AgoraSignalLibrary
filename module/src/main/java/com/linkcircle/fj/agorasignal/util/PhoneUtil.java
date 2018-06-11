package com.linkcircle.fj.agorasignal.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机相关工具类
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 14:19
 */
public class PhoneUtil {
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEI(Context pContext) {
        TelephonyManager telephonyManager = (TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE);

        if (null == telephonyManager) {
            return "";
        }

        return telephonyManager.getDeviceId();
    }

    /**
     * 截取字符串中第一个连续的数字
     *
     * @param pContent 字符串
     * @return 连续的数字
     */
    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public static String getPhone(String pContent) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(pContent);
        while (matcher.find()) {
            return matcher.group(0);
        }

        return "";
    }
}
