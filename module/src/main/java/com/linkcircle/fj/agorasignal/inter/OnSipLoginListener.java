package com.linkcircle.fj.agorasignal.inter;

/**
 * Sip登录监听
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 09:19
 */
public interface OnSipLoginListener {
    /**
     * 信令登录成功
     *
     * @param pUid uid
     * @param pFd  fd
     */
    void onSipLoginSuccess(int pUid, int pFd);

    /**
     * 信令登录失败
     *
     * @param pErrorCode 错误码
     */
    void onSipLoginFailed(int pErrorCode, String reason);

    /**
     * 信令登出
     *
     * @param pErrorCode 错误码
     */
    void onSipLogout(int pErrorCode);
}
