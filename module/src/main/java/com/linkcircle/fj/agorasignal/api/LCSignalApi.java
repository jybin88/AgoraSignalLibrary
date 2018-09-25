package com.linkcircle.fj.agorasignal.api;

import android.content.Context;

import com.linkcircle.fj.agorasignal.inter.OnCallListener;
import com.linkcircle.fj.agorasignal.inter.OnSipInitListener;
import com.linkcircle.fj.agorasignal.inter.OnSipLoginListener;

/**
 * AgoraSignalHelper
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 13:56
 */
public class LCSignalApi extends BaseSignalApi {
    private LogicWorker mWorker;
    private static LCSignalApi sInstance;

    private LCSignalApi() {
        /*private constructor*/
    }

    public static LCSignalApi getInstance() {
        if (null == sInstance) {
            synchronized (LCSignalApi.class) {
                sInstance = new LCSignalApi();
            }
        }

        return sInstance;
    }

    @Override
    public synchronized void init(Context pContext, OnSipInitListener pOnSipInitListener) {
        if (mWorker == null) {
            mWorker = new LogicWorker(pContext.getApplicationContext());
        }

        mWorker.setOnSipInitListener(pOnSipInitListener);
        mWorker.initSip();
    }

    @Override
    public void setSipLoginListener(OnSipLoginListener pOnSipLoginListener) {
        mWorker.setLoginEventHandler(pOnSipLoginListener);
    }

    @Override
    public void setCallListener(OnCallListener pOnCallListener) {
        mWorker.setCallEventHandler(pOnCallListener);
    }

    @Override
    public void loginSip(String pAccount) {
        mWorker.signalLogin(pAccount);
    }

    @Override
    public void logoutSip() {
        mWorker.signalLogOut();
    }

    @Override
    public void call(String pCallNumber) {
        mWorker.signalInvitedCall(pCallNumber);
    }

    @Override
    public void endCall() {
        mWorker.signalEndCall();
    }

    @Override
    public void acceptCall() {
        mWorker.signalAccept();
    }

    @Override
    public void refuseCall() {
        mWorker.signalRefuse();
    }

    @Override
    public synchronized void release() {
        if (null != mWorker) {
            mWorker.release();
            mWorker = null;
        }
    }

    @Override
    public void removeSipCallback() {
        mWorker.removeSipCallback();
    }
}
