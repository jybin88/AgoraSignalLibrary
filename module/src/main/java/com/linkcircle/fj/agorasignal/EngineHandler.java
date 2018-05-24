package com.linkcircle.fj.agorasignal;

import android.util.Log;

import com.linkcircle.fj.agorasignal.inter.OnCallListener;
import com.linkcircle.fj.agorasignal.inter.OnAgoraLoginListener;

import io.agora.AgoraAPI;
import io.agora.rtc.IRtcEngineEventHandler;

/**
 * EngineHandler
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 13:47
 */
public class EngineHandler {
    private OnAgoraLoginListener mOnAgoraLoginListener;
    private OnCallListener mOnCallListener;

    public void setOnAgoraLoginListener(OnAgoraLoginListener pOnAgoraLoginListener) {
        mOnAgoraLoginListener = pOnAgoraLoginListener;
    }

    public void setOnCallListener(OnCallListener pOnCallListener) {
        mOnCallListener = pOnCallListener;
    }

    final IRtcEngineEventHandler mMediaEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            mOnCallListener.onJoinChannelSuccess(channel, uid, elapsed);
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.e("wbs-media-onError", err + "");
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            mOnCallListener.onLeaveChannel(stats);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            mOnCallListener.onUserJoined(uid, elapsed);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            mOnCallListener.onUserOffline(uid, reason);
        }
    };

    AgoraAPI.CallBack mSignalEventHandler = new AgoraAPI.CallBack() {
        @Override
        public void onLog(String txt) {
            super.onLog(txt);
            Log.i("singnal_log-->", txt);
        }

        @Override
        public void onLoginSuccess(int uid, int fd) {
            super.onLoginSuccess(uid, fd);
            mOnAgoraLoginListener.onAgoraLoginSuccess(uid, fd);
        }

        @Override
        public void onLogout(int ecode) {
            super.onLogout(ecode);
            mOnAgoraLoginListener.onAgoraLogout(ecode);
        }

        @Override
        public void onLoginFailed(int ecode) {
            super.onLoginFailed(ecode);
            mOnAgoraLoginListener.onAgoraLoginFailed(ecode);
        }

        @Override
        public void onInviteReceived(String channelID, String account, int uid, String extra) {
            //App 收到呼叫
            super.onInviteReceived(channelID, account, uid, extra);
            mOnCallListener.onInviteReceived(channelID, account, uid, extra);
        }

        @Override
        public void onInviteReceivedByPeer(String channelID, String account, int uid) {
            //被叫方接起电话
            super.onInviteReceivedByPeer(channelID, account, uid);
            mOnCallListener.onInviteReceivedByPeer(channelID, account, uid);
        }

        @Override
        public void onInviteAcceptedByPeer(String channelID, String account, int uid, String extra) {
            super.onInviteAcceptedByPeer(channelID, account, uid, extra);
            mOnCallListener.onInviteAcceptedByPeer(channelID, account, uid, extra);
        }

        @Override
        public void onInviteRefusedByPeer(String channelID, String account, int uid, String extra) {
            super.onInviteRefusedByPeer(channelID, account, uid, extra);
            mOnCallListener.onInviteRefusedByPeer(channelID, account, uid, extra);
        }

        @Override
        public void onInviteFailed(String channelID, String account, int uid, int ecode, String extra) {
            super.onInviteFailed(channelID, account, uid, ecode, extra);
            mOnCallListener.onInviteFailed(channelID, account, uid, ecode, extra);
        }

        @Override
        public void onInviteEndByPeer(String channelID, String account, int uid, String extra) {
            super.onInviteEndByPeer(channelID, account, uid, extra);
            mOnCallListener.onInviteEndByPeer(channelID, account, uid, extra);
        }

        @Override
        public void onQueryUserStatusResult(String name, String status) {
            super.onQueryUserStatusResult(name, status);
            mOnCallListener.onQueryUserStatusResult(name, status);
        }

        @Override
        public void onMessageSendSuccess(String messageID) {
            super.onMessageSendSuccess(messageID);
            mOnCallListener.onMessageSendSuccess(messageID);
        }

        @Override
        public void onMessageSendError(String messageID, int ecode) {
            super.onMessageSendError(messageID, ecode);
            mOnCallListener.onMessageSendError(messageID, ecode);
        }

        @Override
        public void onMessageInstantReceive(String account, int uid, String msg) {
            super.onMessageInstantReceive(account, uid, msg);
            mOnCallListener.onMessageInstantReceive(account, uid, msg);
        }
    };
}
