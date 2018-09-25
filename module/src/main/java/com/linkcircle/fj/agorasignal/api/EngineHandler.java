package com.linkcircle.fj.agorasignal.api;

import com.cqtsip.linkcircle.sipphone.SipCall;
import com.cqtsip.linkcircle.sipphone.control.SipCallBack;
import com.cqtsip.linkcircle.sipphone.control.SipControl;
import com.linkcircle.fj.agorasignal.LoginFailCode;
import com.linkcircle.fj.agorasignal.inter.OnCallListener;
import com.linkcircle.fj.agorasignal.inter.OnSipLoginListener;
import com.linkcircle.fj.agorasignal.util.PhoneUtil;

import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallStatus;
import org.pjsip.pjsua2.StatusCode;

/**
 * EngineHandler
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 13:47
 */
class EngineHandler {
    private OnSipLoginListener mOnSipLoginListener;
    private OnCallListener mOnCallListener;

    public void setOnSipLoginListener(OnSipLoginListener pOnSipLoginListener) {
        mOnSipLoginListener = pOnSipLoginListener;
        SipControl.addSipCallBack(mSipCallBack);
    }

    public void setOnCallListener(OnCallListener pOnCallListener) {
        mOnCallListener = pOnCallListener;
    }

    public OnSipLoginListener getSipLoginListener() {
        return mOnSipLoginListener;
    }

    public OnCallListener getCallListener() {
        return mOnCallListener;
    }

    public void removeSipCallback() {
        SipControl.removeSipCallBack(mSipCallBack);
    }

    private final SipCallBack mSipCallBack = new SipCallBack() {
        @Override
        public void notifyRegState(StatusCode code, String reason, int expiration) {
            if (StatusCode.CQTSIP_SC_OK == code) {//登入成功
                if (0 == expiration) {//退出登出
                    mOnSipLoginListener.onSipLogout(expiration);
                    return;
                }

                mOnSipLoginListener.onSipLoginSuccess(0, 0);
            } else {
                if (0 == expiration) {//退出登出
                    mOnSipLoginListener.onSipLogout(expiration);
                } else {//其他情况按失败处理
                    mOnSipLoginListener.onSipLoginFailed(LoginFailCode.OTHER);
                }
            }
        }

        @Override
        public void notifyCallState(SipCall call) {
            try {
                CallInfo info = call.getInfo();
                CallStatus callStatus = info.getState();
                StatusCode statusCode = info.getLastStatusCode();
                String phone = PhoneUtil.getPhone(info.getRemoteUri());

                if (CallStatus.CALL_STATUS_CONNECTING == callStatus) {
                    mOnCallListener.onInviteAcceptedByPeer(phone, info.getAccId());
                } else if (CallStatus.CALL_STATUS_DISCONNECTED == callStatus) {
                    if (StatusCode.PJSIP_SC_BUSY_HERE == statusCode) {//拒接
                        mOnCallListener.onInviteRefusedByPeer(phone, info.getAccId());
                    } else if (StatusCode.CQTSIP_SC_OK == statusCode) {//接通后挂断
                        mOnCallListener.onInviteEndByPeer(phone, info.getAccId());
                    } else if (StatusCode.PJSIP_SC_TEMPORARILY_UNAVAILABLE == statusCode) {//拒接
                        mOnCallListener.onInviteRefusedByPeer(phone, info.getAccId());
                    } else if (StatusCode.PJSIP_SC_REQUEST_TERMINATED == statusCode) {//回呼后回呼方挂断
                        mOnCallListener.onInviteEndByPeer(phone, info.getAccId());
                    } else if (StatusCode.PJSIP_SC_REQUEST_TIMEOUT == statusCode) {//未接
                        mOnCallListener.onInviteFailed(phone, info.getAccId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void notifyIncomingCall(SipCall call) {
            try {
                CallInfo info = call.getInfo();
                CallStatus callStatus = info.getState();
                String phone = PhoneUtil.getPhone(info.getRemoteUri());

                if (CallStatus.CALL_STATUS_INCOMING == callStatus) {//回呼
                    mOnCallListener.onInviteReceived(phone, info.getAccId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void notifyCallMediaState(SipCall call) {
            super.notifyCallMediaState(call);
        }
    };
}
