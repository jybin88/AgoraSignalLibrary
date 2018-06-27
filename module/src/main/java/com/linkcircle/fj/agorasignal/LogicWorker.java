package com.linkcircle.fj.agorasignal;

import android.content.Context;
import android.text.TextUtils;

import com.cqtsip.linkcircle.sipphone.control.SipContral;
import com.cqtsip.linkcircle.sipphone.control.SipInitResult;
import com.linkcircle.fj.agorasignal.helper.LoginHelper;
import com.linkcircle.fj.agorasignal.inter.OnAgoraLoginListener;
import com.linkcircle.fj.agorasignal.inter.OnCallListener;
import com.linkcircle.fj.agorasignal.inter.OnSipInitListener;
import com.linkcircle.fj.agorasignal.util.PhoneUtil;
import com.linkcircle.fj.agorasignal.util.SignalTokenUtils;

import java.util.Random;

import io.agora.AgoraAPIOnlySignal;
import io.agora.rtc.RtcEngine;
import log.KLog;

/**
 * LogicWorker
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 12:00
 */
public class LogicWorker {
    private Context mContext;
    private AgoraAPIOnlySignal mSignalEngine;
    private RtcEngine mMediaEngine;
    private EngineHandler mEngineHandler;
    private String mAgoraAppId;
    private String mAgoraCertificate;
    private boolean mSipContralInitSuccess;
    private OnSipInitListener mOnSipInitListener;

    public LogicWorker(Context pContext, String pAgoraAppId, String pAgoraCertificate) {
        mEngineHandler = new EngineHandler();
        mContext = pContext;

        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            mAgoraAppId = pAgoraAppId;
            mAgoraCertificate = pAgoraCertificate;
            ensureEngineAlready();
        } else if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            SipContral.setInitResult(new SipInitResult() {
                @Override
                public void initResult(boolean pResult) {
                    mSipContralInitSuccess = pResult;

                    if (null != mOnSipInitListener) {
                        mOnSipInitListener.onSipInit(pResult);
                    }

                    KLog.i("LogicWorker", "init sipContral" + pResult);
                }
            });
            SipContral.Init(pContext);
        }
    }

    public void setOnSipInitListener(OnSipInitListener pOnSipInitListener) {
        mOnSipInitListener = pOnSipInitListener;
    }

    public void setCallEventHandler(OnCallListener pOnCallListener) {
        mEngineHandler.setOnCallListener(pOnCallListener);
    }

    public void setLoginEventHandler(OnAgoraLoginListener pOnAgoraLoginListener) {
        mEngineHandler.setOnAgoraLoginListener(pOnAgoraLoginListener);
    }

    public void removeSipCallback() {
        mEngineHandler.removeSipCallback();
    }

    private void ensureEngineAlready() {
        if (mSignalEngine == null || mMediaEngine == null) {
            if (mSignalEngine == null) {
                mSignalEngine = AgoraAPIOnlySignal.getInstance(mContext, mAgoraAppId);
                mSignalEngine.callbackSet(mEngineHandler.mSignalEventHandler);
            }

            if (mMediaEngine == null) {
                try {
                    mMediaEngine = RtcEngine.create(mContext, mAgoraAppId, mEngineHandler.mMediaEventHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                mMediaEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_COMMUNICATION);
                mMediaEngine.disableVideo();
                mMediaEngine.enableAudio();
            }
        }
    }

    /**
     * 登录 Agora 信令系统。用户在进行任何操作前，必须先登录
     * 1.登录成功：回调 onLoginSuccess，
     * 2.登录失败：回调 onLoginFailed，
     * 3.登录之后失去与服务器的连接：回调 onLogout。
     *
     * @param account 用户账号，最大 128 字节可见字符（不能使用空格）。
     *                可以是用户的 uid、昵称、guid 等任何内容，但必须保证唯一
     */
    public void signalLogin(String account) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            long expiredTime = System.currentTimeMillis() / 1000 + 3600;
            String token = SignalTokenUtils.calcToken(mAgoraAppId, mAgoraCertificate, account, expiredTime);
            ensureEngineAlready();
            mSignalEngine.login2(mAgoraAppId, account, token, 0, "", 5, 10);
        } else if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            if (!mSipContralInitSuccess) {
                if (null != mEngineHandler.getAgoraLoginListener()) {
                    mEngineHandler.getAgoraLoginListener().onAgoraLoginFailed(LoginFailCode.SIP_INIT_FAIL);
                }
                return;
            }

            if (TextUtils.isEmpty(account)) {
                if (null != mEngineHandler.getAgoraLoginListener()) {
                    mEngineHandler.getAgoraLoginListener().onAgoraLoginFailed(LoginFailCode.ACCOUNT_EMPTY);
                }
                return;
            }

            if (TextUtils.isEmpty(LoginHelper.getInstance().getPassword())) {
                if (null != mEngineHandler.getAgoraLoginListener()) {
                    mEngineHandler.getAgoraLoginListener().onAgoraLoginFailed(LoginFailCode.PASSWORD_EMPTY);
                }
                return;
            }

            if (TextUtils.isEmpty(LoginHelper.getInstance().getDomain())) {
                if (null != mEngineHandler.getAgoraLoginListener()) {
                    mEngineHandler.getAgoraLoginListener().onAgoraLoginFailed(LoginFailCode.DOMAIN_EMPTY);
                }
                return;
            }

            SipContral.signIn(account, LoginHelper.getInstance().getPassword(), LoginHelper.getInstance().getDomain());
        }
    }

    /**
     * 登出
     * 用户退出 Agora 信令系统。成功退出 Agora 信令系统时会触发 onLogout 回调
     */
    public void signalLogOut() {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.logout();
        } else if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            SipContral.signOut();
        }
    }

    /**
     * 加入频道
     * 用户一次只能加入一个频道
     * 用户加入频道成功后，自己将收到回调 onChannelJoined，其他同一频道内用户将收到回调 onChannelUserJoined
     * 用户加入失败后，自己将收到回调 onChannelJoinFailed
     *
     * @param pChannelID 频道名。最大为 128 字节可见字符
     */
    public void signalJoinChannel(String pChannelID) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.channelJoin(pChannelID);
        }
    }

    /**
     * 离开频道
     * 退出成功后，所有频道用户将收到回调 onChannelUserLeaved
     *
     * @param pChannelID 频道名。最大为 128 字节可见字符
     */
    public void signalLeaveChannel(String pChannelID) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.channelLeave(pChannelID);
        }
    }

    public void mediaJoin(String channelName) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            int ts = (int) (System.currentTimeMillis() / 1000);
            int r = new Random().nextInt();
            int expiredTs = 0;
            String mediaDynamicKey = "";
            try {
                mediaDynamicKey = DynamicKey4.generateMediaChannelKey(mAgoraAppId, mAgoraCertificate, channelName, ts, r, 0, expiredTs);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ensureEngineAlready();
            mMediaEngine.joinChannel(mediaDynamicKey, channelName, "", 0);
        }
    }

    public void mediaLeave() {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mMediaEngine.leaveChannel();
        }
    }

    /**
     * 发起呼叫
     *
     * @param pChannel 频道名。最大为 128 字节可见字符
     * @param pCallNum 拨打的号码
     * @param pExtra   主叫方想传递给呼叫方的其他信息，最大为 8K 字节可见字符。必须为 JSON 格式。如：
     *                 {“_require_peer_online”:1} 如果对方不在线，则立即触发 ICallBack 回调接口类的 onInviteFailed 回调
     *                 {“_require_peer_online”:0} 如果对方不在线超过 20 秒，则触发 ICallBack 回调接口类的 onInviteFailed 回调（默认）
     *                 {“destMediaUid” : “YourMediaUid123”} 指定的加入相应媒体频道的 uid。
     *                 {“srcNum” : “+123456789”} 指定的在远端手机屏幕上显示的手机号码。
     */
    public void signalInvitedCall(String pChannel, String pCallNum, String pExtra) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.channelInviteUser2(pChannel, pCallNum, pExtra);
        } else if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            String phone = PhoneUtil.getPhone(pCallNum);
            SipContral.callSip(phone);
        }
    }

    /**
     * 结束呼叫
     *
     * @param channelName 频道名。最大为 128 字节可见字符
     * @param callNum     呼叫的号码
     */
    public void signalEndCall(String channelName, String callNum) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.channelInviteEnd(channelName, callNum, 0);
        } else if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            SipContral.hangUpCall();
        }
    }

    /**
     * 拒绝呼叫
     *
     * @param channelName 频道名。最大为 128 字节可见字符
     * @param callNum     呼叫的号码
     * @param extra       其他信息。最大为 8K 字节可见字符。必须为 JSON 格式
     */
    public void signalRefuse(String channelName, String callNum, String extra) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.channelInviteRefuse(channelName, callNum, 0, extra);
        } else if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            SipContral.hangUpCall();
        }
    }

    /**
     * 接受呼叫
     *
     * @param channelName 频道名。最大为 128 字节可见字符
     * @param callNum     呼叫的号码
     */
    public void signalAccept(String channelName, String callNum) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.channelInviteAccept(channelName, callNum, 0, "");
        } else if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            SipContral.acceptCall();
        }
    }

    /**
     * 查询用户是否在线
     * 结果通过 {@link io.agora.IAgoraAPI.ICallBack} 类的 onQueryUserStatusResult() 回调返回
     *
     * @param pUserName 用户名
     */
    public void signalQueryOnLine(String pUserName) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.queryUserStatus(pUserName);
        } else if (SignalType.CQT_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            if (null != mEngineHandler.getCallListener()) {
                mEngineHandler.getCallListener().onQueryUserStatusResult(pUserName, "0");
            }
        }
    }

    /**
     * 发送点对点消息到名为 pAccount 的用户
     * 1.发送成功本地将回调 onMessageSendSuccess，对方将收到 onMessageInstantReceive 回调
     * 2.发送失败将回调 onMessageSendError
     *
     * @param pUserName 用户
     * @param pMsg      消息正文。每条消息最大为 8196 字节可见字符
     * @param msgId     消息的 ID
     */
    public void signalMessageInstantSend(String pUserName, String pMsg, String msgId) {
        if (SignalType.AGORA_SIGNAL.equals(LoginHelper.getInstance().getSignalType())) {
            ensureEngineAlready();
            mSignalEngine.messageInstantSend(pUserName, 0, pMsg, msgId);
        }
    }
}
