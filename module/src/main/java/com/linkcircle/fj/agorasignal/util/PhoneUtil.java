package com.linkcircle.fj.agorasignal.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

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
}
