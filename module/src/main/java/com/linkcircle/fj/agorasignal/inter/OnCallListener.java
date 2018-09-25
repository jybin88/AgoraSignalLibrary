package com.linkcircle.fj.agorasignal.inter;

/**
 * 拨打电话监听
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 09:22
 */
public interface OnCallListener {
    /**
     * received remote call
     *
     * @param pAccount 账号
     * @param pUid     用户id
     */
    void onInviteReceived(String pAccount, int pUid);

    /**
     * remote received local call
     *
     * @param pAccount 账号
     * @param pUid     用户id
     */
    void onInviteReceivedByPeer(String pAccount, int pUid);

    /**
     * remote accept local call 被叫接起电话
     *
     * @param pAccount 账号
     * @param pUid     用户id
     */
    void onInviteAcceptedByPeer(String pAccount, int pUid);

    /**
     * @param pAccount 账号
     * @param pUid     用户id
     */
    void onInviteRefusedByPeer(String pAccount, int pUid);

    /**
     * invited failed 被叫拒绝电话
     *
     * @param pAccount   账号
     * @param pUid       用户id
     */
    void onInviteFailed(String pAccount, int pUid);

    /**
     * if remote end-call, sdk will call this method
     *
     * @param pAccount 账号
     * @param pUid     用户id
     */
    void onInviteEndByPeer(String pAccount, int pUid);
}
