package com.linkcircle.fj.agorasignal;

/**
 * 登录失败code
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/6/27 11:41
 */
public class LoginFailCode {
    /**
     * sip信道初始化失败
     */
    public static final int SIP_INIT_FAIL = -1;
    /**
     * 账号为空
     */
    public static final int ACCOUNT_EMPTY = 0;
    /**
     * 密码为空
     */
    public static final int PASSWORD_EMPTY = 1;
    /**
     * 域名为空
     */
    public static final int DOMAIN_EMPTY = 2;
    /**
     * 其他错误
     */
    public static final int OTHER = 3;
}
