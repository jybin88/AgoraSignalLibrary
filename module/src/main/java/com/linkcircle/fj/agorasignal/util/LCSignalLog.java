package com.linkcircle.fj.agorasignal.util;

import android.util.Log;

import com.itgoyo.logtofilelibrary.LogToFileUtils;
import com.linkcircle.fj.agorasignal.LCSignalConfig;

/**
 * @author lifuhai@linkcircle.cn
 * @date 2018/9/25 11:25
 */
public class LCSignalLog {
    private static final String DEFAULT_LOG = "LCSipLog";

    public static void w(String pMessage) {
        if (LCSignalConfig.getInstance().isEnableLog()) {
            Log.w(LCSignalConfig.getInstance().getLogTag(), pMessage);
        }

        LogToFileUtils.write(DEFAULT_LOG + "_" + LCSignalConfig.getInstance().getLogTag() + ":" + pMessage);
    }

    public static void e(String pMessage) {
        if (LCSignalConfig.getInstance().isEnableLog()) {
            Log.e(LCSignalConfig.getInstance().getLogTag(), pMessage);
        }

        LogToFileUtils.write(DEFAULT_LOG + "_" + LCSignalConfig.getInstance().getLogTag() + ":" + pMessage);
    }

    public static void d(String pMessage) {
        if (LCSignalConfig.getInstance().isEnableLog()) {
            Log.d(LCSignalConfig.getInstance().getLogTag(), pMessage);
        }

        LogToFileUtils.write(DEFAULT_LOG + "_" + LCSignalConfig.getInstance().getLogTag() + ":" + pMessage);
    }

    public static void i(String pMessage) {
        if (LCSignalConfig.getInstance().isEnableLog()) {
            Log.i(LCSignalConfig.getInstance().getLogTag(), pMessage);
        }

        LogToFileUtils.write(DEFAULT_LOG + "_" + LCSignalConfig.getInstance().getLogTag() + ":" + pMessage);
    }
}
