package com.linkcircle.fj.agorasignal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.linkcircle.fj.agorasignal.inter.OnCallListener;
import com.linkcircle.fj.agorasignal.inter.OnAgoraLoginListener;
import com.linkcircle.fj.agorasignal.util.SignalTokenUtils;

import java.util.Random;

import io.agora.AgoraAPIOnlySignal;
import io.agora.rtc.RtcEngine;

/**
 * LogicWorker
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 12:00
 */
public class LogicWorker extends Thread {
    private Context mContext;
    private LogicWorkThreader mThreader;
    private boolean mIsReady;
    private AgoraAPIOnlySignal mSignalEngine;
    private RtcEngine mMediaEngine;
    private EngineHandler mEngineHandler;
    private String mAgoraAppId;
    private String mAgoraCertificate;

    public LogicWorker(Context pContext, String pAgoraAppId, String pAgoraCertificate) {
        mContext = pContext;
        mAgoraAppId = pAgoraAppId;
        mAgoraCertificate = pAgoraCertificate;
        mEngineHandler = new EngineHandler();
    }

    public void setCallEventHandler(OnCallListener pOnCallListener) {
        mEngineHandler.setOnCallListener(pOnCallListener);
    }

    public void setLoginEventHandler(OnAgoraLoginListener pOnAgoraLoginListener) {
        mEngineHandler.setOnAgoraLoginListener(pOnAgoraLoginListener);
    }

    private static final class LogicWorkThreader extends Handler {
        private LogicWorker mWorker;

        LogicWorkThreader(LogicWorker worker) {
            this.mWorker = worker;
        }

        void release() {
            this.mWorker = null;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWorker == null)
                return;

            switch (msg.what) {
                case AgoraSignalConstants.SIGNALING_LOGIN:
                    String channelCount = msg.obj.toString();
                    mWorker.signalLogin(channelCount);
                    break;
                case AgoraSignalConstants.SINGALING_CALL:
                    String[] callNumber = (String[]) msg.obj;
                    mWorker.signalInvitedCall(callNumber[0], callNumber[1], callNumber[2]);
                    break;
                case AgoraSignalConstants.SIGNALING_END:
                    String[] endNumber = (String[]) msg.obj;
                    mWorker.signalEndCall(endNumber[0], endNumber[1]);
                    break;
                case AgoraSignalConstants.SIGNALING_REFUSE:
                    String[] refuseNumber = (String[]) msg.obj;
                    mWorker.signalRefuse(refuseNumber[0], refuseNumber[1], refuseNumber[2]);
                    break;
                case AgoraSignalConstants.SIGNALING_ACCEPT:
                    String[] acceptNumber = (String[]) msg.obj;
                    mWorker.signalAccept(acceptNumber[0], acceptNumber[1]);
                    break;
                case AgoraSignalConstants.MEDIA_JOINCHANNEL:
                    String mediaChannelname = msg.obj.toString();
                    mWorker.mediaJoin(mediaChannelname);
                    break;
                case AgoraSignalConstants.MEDIA_LEAVECHANNEL:
                    mWorker.mediaLeave();
                    break;
                case AgoraSignalConstants.SIGNALING_JOIN_CHANNEL:
                    String sigChannelName = msg.obj.toString();
                    mWorker.signalJoinChannel(sigChannelName);
                    break;
                case AgoraSignalConstants.SIGNALING_LEAVE_CHANNEL:
                    String sigLeaveName = msg.obj.toString();
                    mWorker.signalLeaveChannel(sigLeaveName);
                    break;
                case AgoraSignalConstants.SIGNALING_LOGOUT:
                    mWorker.signalLogOut();
                    break;
                case AgoraSignalConstants.SIGNALING_QUERY_LOGIN:
                    String userName = msg.obj.toString();
                    mWorker.signalQueryOnLine(userName);
                    break;
                case AgoraSignalConstants.SIGNALING_INSTANT_SEND:
                    String[] contents = (String[]) msg.obj;
                    mWorker.signalMessageInstantSend(contents[0], contents[1], contents[2]);
                    break;
                default:
                    break;
            }
        }
    }

    public void waitForReady() {
        while (!mIsReady) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        mThreader = new LogicWorkThreader(this);
        ensureEngineAlready();
        mIsReady = true;
        Looper.loop();
    }

    public void exit() {
        if (Thread.currentThread() != this) {
            mThreader.sendEmptyMessage(AgoraSignalConstants.WORKER_EXIT);
            return;
        }

        mIsReady = false;

        Looper looper = Looper.myLooper();

        if (null != looper) {
            looper.quit();
        }

        mThreader.release();
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
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SIGNALING_LOGIN;
            msg.obj = account;
            mThreader.sendMessage(msg);
            return;
        }

        long expiredTime = System.currentTimeMillis() / 1000 + 3600;
        String token = SignalTokenUtils.calcToken(mAgoraAppId, mAgoraCertificate, account, expiredTime);
        ensureEngineAlready();
        mSignalEngine.login2(mAgoraAppId, account, token, 0, "", 5, 10);
    }

    /**
     * 登出
     * 用户退出 Agora 信令系统。成功退出 Agora 信令系统时会触发 onLogout 回调
     */
    public void signalLogOut() {
        if (Thread.currentThread() != this) {
            mThreader.sendEmptyMessage(AgoraSignalConstants.SIGNALING_LOGOUT);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.logout();
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
        //主叫方app调用媒体加入到房间
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SIGNALING_JOIN_CHANNEL;
            msg.obj = pChannelID;
            mThreader.sendMessage(msg);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.channelJoin(pChannelID);
    }

    /**
     * 离开频道
     * 退出成功后，所有频道用户将收到回调 onChannelUserLeaved
     *
     * @param pChannelID 频道名。最大为 128 字节可见字符
     */
    public void signalLeaveChannel(String pChannelID) {
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SIGNALING_LEAVE_CHANNEL;
            msg.obj = pChannelID;
            mThreader.sendMessage(msg);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.channelLeave(pChannelID);
    }

    public void mediaJoin(String channelName) {
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.MEDIA_JOINCHANNEL;
            msg.obj = channelName;
            mThreader.sendMessage(msg);
            return;
        }

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

    public void mediaLeave() {
        if (Thread.currentThread() != this) {
            mThreader.sendEmptyMessage(AgoraSignalConstants.MEDIA_LEAVECHANNEL);
            return;
        }

        ensureEngineAlready();
        mMediaEngine.leaveChannel();
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
        if (Thread.currentThread() != this) {
            final Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SINGALING_CALL;
            msg.arg1 = 1;
            msg.obj = new String[]{pChannel, pCallNum, pExtra};
            mThreader.sendMessage(msg);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.channelInviteUser2(pChannel, pCallNum, pExtra);
    }

    /**
     * 结束呼叫
     *
     * @param channelName 频道名。最大为 128 字节可见字符
     * @param callNum     呼叫的号码
     */
    public void signalEndCall(String channelName, String callNum) {
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SIGNALING_END;
            msg.obj = new String[]{channelName, callNum};
            mThreader.sendMessage(msg);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.channelInviteEnd(channelName, callNum, 0);
    }

    /**
     * 拒绝呼叫
     *
     * @param channelName 频道名。最大为 128 字节可见字符
     * @param callNum     呼叫的号码
     * @param extra       其他信息。最大为 8K 字节可见字符。必须为 JSON 格式
     */
    public void signalRefuse(String channelName, String callNum, String extra) {
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SIGNALING_REFUSE;
            msg.obj = new String[]{channelName, callNum, extra};
            mThreader.sendMessage(msg);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.channelInviteRefuse(channelName, callNum, 0, extra);
    }

    /**
     * 接受呼叫
     *
     * @param channelName 频道名。最大为 128 字节可见字符
     * @param callNum     呼叫的号码
     */
    public void signalAccept(String channelName, String callNum) {
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SIGNALING_ACCEPT;
            msg.obj = new String[]{channelName, callNum};
            mThreader.sendMessage(msg);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.channelInviteAccept(channelName, callNum, 0, "");
    }

    /**
     * 查询用户是否在线
     * 结果通过 {@link io.agora.IAgoraAPI.ICallBack} 类的 onQueryUserStatusResult() 回调返回
     *
     * @param pUserName 用户名
     */
    public void signalQueryOnLine(String pUserName) {
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SIGNALING_QUERY_LOGIN;
            msg.obj = pUserName;
            mThreader.sendMessage(msg);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.queryUserStatus(pUserName);
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
        if (Thread.currentThread() != this) {
            Message msg = Message.obtain();
            msg.what = AgoraSignalConstants.SIGNALING_INSTANT_SEND;
            msg.obj = new String[]{pUserName, pMsg, msgId};
            mThreader.sendMessage(msg);
            return;
        }

        ensureEngineAlready();
        mSignalEngine.messageInstantSend(pUserName, 0, pMsg, msgId);
    }
}
