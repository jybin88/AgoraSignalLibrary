package com.linkcircle.fj.agorasignal.inter;

import io.agora.rtc.IRtcEngineEventHandler;

/**
 * 通过声网拨打电话监听
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 09:22
 */
public interface OnCallListener {
    /**
     * received remote call
     *
     * @param pChannelId
     * @param pAccount
     * @param pUid
     * @param pExtra
     */
    void onInviteReceived(String pChannelId, String pAccount, int pUid, String pExtra);

    /**
     * remote received local call
     *
     * @param pChannelId
     * @param pAccount
     * @param pUid
     */
    void onInviteReceivedByPeer(String pChannelId, String pAccount, int pUid);

    /**
     * remote accept local call 被叫接起电话
     *
     * @param pChannelId
     * @param pAccount
     * @param pUid
     * @param pExtra
     */
    void onInviteAcceptedByPeer(String pChannelId, String pAccount, int pUid, String pExtra);

    /**
     * @param pChannelId
     * @param pAccount
     * @param pUid
     * @param pExtra
     */
    void onInviteRefusedByPeer(String pChannelId, String pAccount, int pUid, String pExtra);

    /**
     * invited failed 被叫拒绝电话
     *
     * @param pChannelId
     * @param pAccount
     * @param pUid
     * @param pErrorCode
     * @param pExtra
     */
    void onInviteFailed(String pChannelId, String pAccount, int pUid, int pErrorCode, String pExtra);

    /**
     * if remote end-call, sdk will call this method
     *
     * @param pChannelId
     * @param pAccount
     * @param pUid
     * @param pExtra
     */
    void onInviteEndByPeer(String pChannelId, String pAccount, int pUid, String pExtra);

    /**
     * media remote user joined
     *
     * @param pUid
     * @param pElapsed
     */
    void onUserJoined(int pUid, int pElapsed);

    /**
     * media local user joined success
     *
     * @param pChannel
     * @param pUid
     * @param pElapsed
     */
    void onJoinChannelSuccess(String pChannel, int pUid, int pElapsed);

    /**
     * media local user leave success
     *
     * @param pStatus
     */
    void onLeaveChannel(IRtcEngineEventHandler.RtcStats pStatus);

    /**
     * media remote user leave
     *
     * @param pUid
     * @param pReason
     */
    void onUserOffline(int pUid, int pReason);

//    void onChannelJoined();
//
//    void onChannelJoinedFailed(int err);

    /**
     * signaling query status
     *
     * @param pName
     * @param pStatus
     */
    void onQueryUserStatusResult(String pName, String pStatus);

    /**
     * signaling send message success
     *
     * @param pMessageId
     */
    void onMessageSendSuccess(String pMessageId);

    /**
     * signaling send message error
     *
     * @param pMessageId
     * @param pErrorCode
     */
    void onMessageSendError(String pMessageId, int pErrorCode);

    /**
     * signaling received message
     *
     * @param pAccount
     * @param pUid
     * @param pMessage
     */
    void onMessageInstantReceive(String pAccount, int pUid, String pMessage);
}
