package com.linkcircle.fj.agorasignal.helper;

import android.content.Context;

import com.linkcircle.fj.agorasignal.LogicWorker;
import com.linkcircle.fj.agorasignal.SignalType;
import com.linkcircle.fj.agorasignal.inter.OnSipInitListener;

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

    public synchronized void initWorker(Context pContext, String pAgoraAppId, String pAgoraCertificate, OnSipInitListener pOnSipInitListener) {
        if (mWorker == null) {
            mWorker = new LogicWorker(pContext.getApplicationContext(), pAgoraAppId, pAgoraCertificate);
        }

        if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            mWorker.setOnSipInitListener(pOnSipInitListener);
            mWorker.initSipControl();
        }
    }

    public synchronized LogicWorker getWorkThreader() {
        return mWorker;
    }

    public synchronized void exitWorker() {
        if (null != mWorker) {
            mWorker = null;
        }
    }
}
