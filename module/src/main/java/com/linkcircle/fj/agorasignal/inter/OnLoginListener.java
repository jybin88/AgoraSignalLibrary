package com.linkcircle.fj.agorasignal.inter;

/**
 * 登录监听
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 17:18
 */
public interface OnLoginListener {
    /**
     * 登录成功
     *
     * @param pAccount 账号
     */
    void onLoginSuccess(String pAccount);

    /**
     * 登录失败
     *
     * @param pCode    错误码
     * @param pMessage 错误信息
     */
    void onLoginFail(int pCode, String pMessage);
}
