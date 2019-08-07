package com.linkcircle.fj.agorasignal.api;

import com.cqtsip.linkcircle.sipphone.SipCall;
import com.cqtsip.linkcircle.sipphone.control.SipCallBack;
import com.cqtsip.linkcircle.sipphone.control.SipControl;
import com.linkcircle.fj.agorasignal.inter.OnCallListener;
import com.linkcircle.fj.agorasignal.inter.OnSipLoginListener;
import com.linkcircle.fj.agorasignal.util.PhoneUtil;

import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

/**
 * EngineHandler
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 13:47
 */
class EngineHandler {
    private static final int LOGIN_FAIL_STATE = -1;//登录失败
    private static final int LOGOUT_STATE = 0;//登出
    private static final int LOGIN_SUCCESS_STATE = 1;//登入成功
    private OnSipLoginListener mOnSipLoginListener;
    private OnCallListener mOnCallListener;
    private int mRegState = LOGIN_FAIL_STATE;//默认失败

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
        public void notifyRegState(pjsip_status_code code, String reason, int expiration) {
            if (pjsip_status_code.PJSIP_SC_OK == code) {//登入成功
                if (0 == expiration) {//退出登出
                    if (LOGOUT_STATE != mRegState) {
                        mRegState = LOGOUT_STATE;
                        mOnSipLoginListener.onSipLogout(expiration);
                    }
                    return;
                }

                if (LOGIN_SUCCESS_STATE != mRegState) {//不是登入成功
                    mRegState = LOGIN_SUCCESS_STATE;
                    mOnSipLoginListener.onSipLoginSuccess(0, 0);
                }
            } else {
                if (0 == expiration) {//退出登出
                    if (LOGOUT_STATE != mRegState) {
                        mRegState = LOGOUT_STATE;
                        mOnSipLoginListener.onSipLogout(expiration);
                    }
                } else {//其他情况按失败处理
                    mRegState = LOGIN_FAIL_STATE;
                    mOnSipLoginListener.onSipLoginFailed(expiration, reason);
                }
            }
        }

        @Override
        public void notifyCallState(SipCall call) {
            try {
                CallInfo info = call.getInfo();
                pjsip_inv_state callStatus = info.getState();
                pjsip_status_code statusCode = info.getLastStatusCode();
                String phone = PhoneUtil.getPhone(info.getRemoteUri());

                if (pjsip_inv_state.PJSIP_INV_STATE_CONNECTING == callStatus) {
                    mOnCallListener.onInviteAcceptedByPeer(phone, info.getAccId());
                } else if (pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED == callStatus) {
                    if (pjsip_status_code.PJSIP_SC_BUSY_HERE == statusCode) {//拒接
                        mOnCallListener.onInviteRefusedByPeer(phone, info.getAccId());
                    } else if (pjsip_status_code.PJSIP_SC_OK == statusCode) {//接通后挂断
                        mOnCallListener.onInviteEndByPeer(phone, info.getAccId());
                    } else if (pjsip_status_code.PJSIP_SC_TEMPORARILY_UNAVAILABLE == statusCode) {//拒接
                        mOnCallListener.onInviteRefusedByPeer(phone, info.getAccId());
                    } else if (pjsip_status_code.PJSIP_SC_REQUEST_TERMINATED == statusCode) {//回呼后回呼方挂断
                        mOnCallListener.onInviteEndByPeer(phone, info.getAccId());
                    } else if (pjsip_status_code.PJSIP_SC_REQUEST_TIMEOUT == statusCode) {//未接
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
                pjsip_inv_state callStatus = info.getState();
                String phone = PhoneUtil.getPhone(info.getRemoteUri());

                if (pjsip_inv_state.PJSIP_INV_STATE_INCOMING == callStatus) {//回呼
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
