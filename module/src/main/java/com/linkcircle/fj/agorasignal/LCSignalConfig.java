package com.linkcircle.fj.agorasignal;

/**
 * LCSignalConfig 配置类，需要在Application
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/9/25 11:12
 */
public class LCSignalConfig {
    private static LCSignalConfig sInstance;
    private boolean mEnableLog; //开启log标识，默认开启
    private String mLogTag; //log标识

    public static void init(LCSignalBuilder pLCSignalBuilder) {
        if (null == sInstance) {
            synchronized (LCSignalConfig.class) {
                sInstance = new LCSignalConfig(pLCSignalBuilder);
            }
        }
    }

    public static LCSignalConfig getInstance() {
        if (null == sInstance) {
            init();
        }

        return sInstance;
    }

    public boolean isEnableLog() {
        return mEnableLog;
    }

    public String getLogTag() {
        return mLogTag;
    }

    private LCSignalConfig(LCSignalBuilder pLCSignalBuilder) {
        mEnableLog = pLCSignalBuilder.isEnableLog();
        mLogTag = pLCSignalBuilder.getLogTag();
    }

    private static void init() {
        if (null == sInstance) {
            synchronized (LCSignalConfig.class) {
                sInstance = new LCSignalConfig(new LCSignalBuilder());
            }
        }
    }

    public static class LCSignalBuilder {
        private boolean mEnableLog = true; //开启log标识，默认开启
        private String mLogTag = "LCSignal"; //log标识

        public LCSignalBuilder() {
            /*no-op*/
        }

        boolean isEnableLog() {
            return mEnableLog;
        }

        public LCSignalBuilder setEnableLog(boolean pEnableLog) {
            mEnableLog = pEnableLog;
            return this;
        }

        String getLogTag() {
            return mLogTag;
        }

        public LCSignalBuilder setLogTag(String pLogTag) {
            mLogTag = pLogTag;
            return this;
        }
    }
}
