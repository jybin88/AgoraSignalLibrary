package com.linkcircle.fj.agorasignal.helper;

import android.content.Context;

import com.linkcircle.fj.agorasignal.LogicWorker;

/**
 * AgoraSignalHelper
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 13:56
 */
public class AgoraSignalHelper {
    private LogicWorker mWorker;
    private static AgoraSignalHelper sInstance;

    private AgoraSignalHelper() {
        /*private constructor*/
    }

    public static AgoraSignalHelper getInstance() {
        if (null == sInstance) {
            synchronized (AgoraSignalHelper.class) {
                sInstance = new AgoraSignalHelper();
            }
        }

        return sInstance;
    }

    /**
     * @deprecated 不需要的方法
     */
    public synchronized void startWorker() {

    }

    public synchronized void initWorker(Context pContext, String pAgoraAppId, String pAgoraCertificate) {
        if (mWorker == null) {
            mWorker = new LogicWorker(pContext.getApplicationContext(), pAgoraAppId, pAgoraCertificate);
        }
    }

    public synchronized LogicWorker getWorkThreader() {
        return mWorker;
    }

    public synchronized void exitWorker() {
        mWorker.removeSipCallback();
        mWorker = null;
    }
}
