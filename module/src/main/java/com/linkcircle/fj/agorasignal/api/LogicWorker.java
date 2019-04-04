package com.linkcircle.fj.agorasignal.api;

import android.content.Context;
import android.text.TextUtils;

import com.cqtsip.linkcircle.sipphone.control.SipControl;
import com.cqtsip.linkcircle.sipphone.control.SipInitResult;
import com.linkcircle.fj.agorasignal.LoginFailCode;
import com.linkcircle.fj.agorasignal.inter.OnCallListener;
import com.linkcircle.fj.agorasignal.inter.OnSipInitListener;
import com.linkcircle.fj.agorasignal.inter.OnSipLoginListener;
import com.linkcircle.fj.agorasignal.util.LCSignalLog;
import com.linkcircle.fj.agorasignal.util.PhoneUtil;

/**
 * LogicWorker
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 12:00
 */
class LogicWorker {
    private Context mContext;
    private EngineHandler mEngineHandler;
    private boolean mSipContralInitSuccess;
    private OnSipInitListener mOnSipInitListener;

    LogicWorker(Context pContext) {
        mEngineHandler = new EngineHandler();
        mContext = pContext;
    }

    public void initSip() {
        SipControl.setInitResult(new SipInitResult() {
            @Override
            public void initResult(boolean pResult) {
                mSipContralInitSuccess = pResult;

                if (null != mOnSipInitListener) {
                    mOnSipInitListener.onSipInit(pResult);
                }

                LCSignalLog.d("init sip " + pResult);
            }
        });
        SipControl.Init(mContext);
    }

    public void setOnSipInitListener(OnSipInitListener pOnSipInitListener) {
        mOnSipInitListener = pOnSipInitListener;
    }

    public void setCallEventHandler(OnCallListener pOnCallListener) {
        mEngineHandler.setOnCallListener(pOnCallListener);
    }

    public void setLoginEventHandler(OnSipLoginListener pOnSipLoginListener) {
        mEngineHandler.setOnSipLoginListener(pOnSipLoginListener);
    }

    public void removeSipCallback() {
        mEngineHandler.removeSipCallback();
    }

    /**
     * 登录sip。用户在进行任何操作前，必须先登录
     * 成功回调 onLoginSuccess
     * 失败回调 onLoginFailed
     *
     * @param pAccount 用户账号，最大 128 字节可见字符（不能使用空格），必须保证唯一
     */
    public void signalLogin(String pAccount) {
        if (!mSipContralInitSuccess) {
            if (null != mEngineHandler.getSipLoginListener()) {
                mEngineHandler.getSipLoginListener().onSipLoginFailed(LoginFailCode.SIP_INIT_FAIL, "初始化失败");
                LCSignalLog.d("sip login fail: OnSipLoginListener is null please check LCSignalApi.getInstance().setSipLoginListener(OnSipLoginListener pOnSipLoginListener) method has called");
            }
            return;
        }

        if (TextUtils.isEmpty(pAccount)) {
            if (null != mEngineHandler.getSipLoginListener()) {
                mEngineHandler.getSipLoginListener().onSipLoginFailed(LoginFailCode.ACCOUNT_EMPTY, "账号为空");
                LCSignalLog.d("sip login fail: account is empty");
            }
            return;
        }

        if (TextUtils.isEmpty(LoginApi.getInstance().getPassword())) {
            if (null != mEngineHandler.getSipLoginListener()) {
                mEngineHandler.getSipLoginListener().onSipLoginFailed(LoginFailCode.PASSWORD_EMPTY, "密码为空");
                LCSignalLog.d("sip login fail: password is empty");
            }
            return;
        }

        if (TextUtils.isEmpty(LoginApi.getDomain())) {
            if (null != mEngineHandler.getSipLoginListener()) {
                mEngineHandler.getSipLoginListener().onSipLoginFailed(LoginFailCode.DOMAIN_EMPTY, "域名为空");
                LCSignalLog.d("sip login fail: domain is empty");
            }
            return;
        }

        SipControl.signIn(pAccount, LoginApi.getInstance().getPassword(), LoginApi.getDomain());
    }

    /**
     * 登出sip
     * 用户退出 Agora 信令系统。成功退出 Agora 信令系统时会触发 onLogout 回调
     */
    public void signalLogOut() {
        SipControl.signOut();
    }

    /**
     * 发起呼叫
     *
     * @param pCallNum 拨打的号码
     */
    public void signalInvitedCall(String pCallNum) {
        String phone = PhoneUtil.getPhone(pCallNum);
        SipControl.callSip(phone);
    }

    /**
     * 结束呼叫
     */
    public void signalEndCall() {
        SipControl.hangUpCall();
    }

    /**
     * 拒绝呼叫
     */
    public void signalRefuse() {
        SipControl.hangUpCall();
    }

    /**
     * 接受呼叫
     */
    public void signalAccept() {
        SipControl.acceptCall();
    }

    /**
     * 释放资源
     */
    public void release() {
        SipControl.release();
    }
}
